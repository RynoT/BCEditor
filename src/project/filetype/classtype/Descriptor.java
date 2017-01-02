package project.filetype.classtype;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ryan Thomson on 22/12/2016.
 */
public class Descriptor {

    private static final Map<Character, String> types = new HashMap<>(9);

    static {
        types.put('B', "byte");
        types.put('C', "char");
        types.put('D', "double");
        types.put('F', "float");
        types.put('I', "int");
        types.put('J', "long");
        types.put('S', "short");
        types.put('Z', "boolean");
        types.put('V', "void");
    }

    // This is a static class used for decoding bytecode descriptors
    private Descriptor(){
    }

/*    public static void main(final String[] args) {
        System.out.println(decode("(Laqz;)V"));
        System.out.println(decode("()V"));
        System.out.println(decode("(Lyc;II)V"));
        System.out.println(decode("(C)Ljava/lang/Character;"));
        System.out.println(decode("(Lye;[Ljava/lang/Object;)Laai;"));
        System.out.println(decode("()[La;"));
        System.out.println(decode("(Ljava/lang/String;ICZ)V"));
        System.out.println(decode("Ljava/util/List<Leditor/project/filetype/classfile/opcode/Operand;>;"));

        //Map<java.util.List<String>, Long>
        System.out.println(decode("Ljava/util/Map<Ljava/util/List<Ljava/lang/String;>;Ljava/lang/Long;>;"));

        //Map<String, java.util.List<HashMap<HashSet<Integer>, Long>>>
        System.out.println(decode("Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ljava/util/HashMap<Ljava/util/HashSet<Ljava/lang/Integer;>;Ljava/lang/Long;>;>;>;"));

        //<T extends org.script.Context>org.script.TestScript<T>
        System.out.println(decode("<T:Lorg/script/Context;>Lorg/script/TestScript<TT;>;"));

        //<A extends java.lang.Object, T extends A, F extends T>ABC<F>, iface<A>
        System.out.println(decode("<A:Ljava/lang/Object;T:TA;F:TT;>LABC<TF;>;Liface<TA;>;"));

        //<T extends org.script.AbstractQuery<T, K, C>, K extends java.lang.Object, C extends org.script.ClientContext>org.script.ClientAccessor<C>, java.lang.Iterable<K>, org.script.Nillable<K>
        System.out.println(decode("<T:Lorg/script/AbstractQuery<TT;TK;TC;>;K:Ljava/lang/Object;C:Lorg/script/ClientContext;>Lorg/script/ClientAccessor<TC;>;Ljava/lang/Iterable<TK;>;Lorg/script/Nillable<TK;>;"));
    }*/

    public static String hideObjectClass(final String descriptor){
        return descriptor.replace(" extends java.lang.Object", "");
    }

    public static int getOffset(final String str, final char c, final int begin, final int end){
        assert end <= str.length();
        for(int i = begin, inner = 0; i < end; i++){
            final char next = str.charAt(i);
            if(next == '<'){
                inner++;
            } else if(next == '>'){
                inner--;
            }
            if(next == c && inner == 0){
                return i;
            }
        }
        return -1;
    }

    public static String decode(final String descriptor) {
        final StringBuilder sb = new StringBuilder();
        final char[] chars = descriptor.toCharArray();
        String array = "";
        for(int i = 0; i < chars.length; i++) {
            switch(chars[i]) {
                case '>':
                    return sb.append('%').append(i).toString();
                case '[':
                    array += "[]";
                    break;
                case '*':
                    sb.append('?');
                    break;
                case '(':
                    sb.append(chars[i]);
                    break;
                case '-':
                    sb.append("? super ");
                    break;
                case '+':
                    sb.append("? extends ");
                    break;
                case '<': {
                    int index = descriptor.indexOf('(');
                    if(index == -1){
                        index = Descriptor.getOffset(descriptor, '>', i, descriptor.length()) + 1;
                        assert index != -1;
                    }
                    i = index - 1;
                    sb.append(Descriptor.decodeGenerics(descriptor.substring(0, index)));
                    break;
                }
                case ')': {
                    sb.append(chars[i]);
                    final int index = sb.indexOf(", )");
                    if(index != -1) {
                        sb.replace(index, index + 3, ")");
                    }
                    break;
                }
                default: {
                    if(Descriptor.types.containsKey(chars[i])) {
                        sb.append(Descriptor.types.get(chars[i])).append(array);
                        array = "";
                    } else if(chars[i] == 'L' || chars[i] == 'T') {
                        loop:
                        for(i++; i < chars.length; i++) {
                            switch(chars[i]) {
                                case ';':
                                    sb.append(array);
                                    array = "";
                                    break loop;
                                case '/':
                                    sb.append('.');
                                    break;
                                case '<':
                                    final String decrypt = Descriptor.decode(descriptor.substring(i + 1));
                                    final int index = decrypt.lastIndexOf('%');
                                    i += Integer.valueOf(decrypt.substring(index + 1)) + 1;
                                    sb.append('<').append(decrypt.substring(0, index)).append('>');
                                    break;
                                default:
                                    sb.append(chars[i]);
                            }
                        }
                    }
                    if(i < chars.length - 1 && chars[i + 1] != '>') {
                        sb.append(", ");
                    }
                }
            }
        }
        return sb.toString();
    }

    private static String decodeGenerics(final String descriptor) {
        final StringBuilder sb = new StringBuilder();
        final char[] chars = descriptor.toCharArray();
        for(int i = 0; i < chars.length; i++) {
            final char next = chars[i];
            if(next == '<' || next == '>') {
                sb.append(next);
            } else if(next == 'L') {
                final int start = i;
                i = Descriptor.getOffset(descriptor, ';', i, descriptor.length());
                sb.append(Descriptor.decode(descriptor.substring(start, i)));

                if(i == chars.length){
                    break;
                }
                if(chars[i + 1] == '>') {
                    continue;
                }
                if(chars[++i] == ':') {
                    sb.append(" & ");
                } else if(chars[i] != '>') {
                    sb.append(", ");
                    for(; i < chars.length; i++) {
                        if(chars[i] == ':') {
                            break;
                        }
                        sb.append(chars[i]);
                    }
                    sb.append(" extends ");
                }
            } else {
                if(next == ':') {
                    if(chars[i + 1] != ':') {
                        sb.append(" extends ");
                    }
                } else if(next == 'T' && chars[i + 1] != ':'){
                    String var = "";
                    for(i += 1; i < chars.length; i++){
                        if(chars[i] == ';'){
                            break;
                        } else {
                            var += chars[i];
                        }
                    }
                    sb.append(var);

                    if(i < chars.length - 1 && chars[i + 1] != '>') {
                        sb.append(", ");
                    }
                } else {
                    sb.append(next);
                }
            }
        }
        return sb.toString();
    }
}