package project.filetype.classtype;

import project.filetype.ClassType;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.FieldInfo;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._ConstantValue;
import project.filetype.classtype.member.attributes._Exceptions;
import project.filetype.classtype.member.attributes._Signature;

/**
 * Created by Ryan Thomson on 28/12/2016.
 */
public class ClassFormat {

    private ClassFormat() {
    }

    // Format field
    // e.g. public static final int NUMBER;
    public static String format(final FieldInfo field, final ConstantPool pool) {
        final StringBuilder sb = new StringBuilder();
        sb.append(field.getAccessFlagsString()).append(" ");
        sb.append(Descriptor.decode(field.getTagDescriptor(pool).getValue())).append(" ");
        sb.append(field.getTagName(pool).getValue());

        final _ConstantValue constantValue = (_ConstantValue) AttributeInfo.findFirst(AttributeInfo.CONSTANT_VALUE, field.getAttributes(), pool);
        if(constantValue != null){
            sb.append(" = ").append(constantValue.getTagConstant(pool).getContentString(pool));
        }
        sb.append(";");
        return sb.toString();
    }

    // Format method name
    // e.g. public static String format(MethodInfo, ConstantPool) {
    public static String format(final MethodInfo method, final ConstantPool pool) {
        final StringBuilder sb = new StringBuilder();
        if(method.getAccessFlags() != 0) {
            sb.append(method.getAccessFlagsString()).append(" ");
        }
        String descriptor;
        final _Signature signatureAttribute = (_Signature) AttributeInfo.findFirst(AttributeInfo.SIGNATURE, method.getAttributes(), pool);
        if(signatureAttribute != null){
            descriptor = Descriptor.decode(signatureAttribute.getTagSignature(pool).getValue());
        } else {
            descriptor = Descriptor.decode(method.getTagDescriptor(pool).getValue());
        }
        descriptor = Descriptor.hideObjectClass(descriptor);

        assert (descriptor.indexOf('(') != -1 && descriptor.indexOf(')') != -1);
        if(descriptor.charAt(0) == '<'){
            sb.append(descriptor.substring(0, descriptor.indexOf('('))).append(" ");
        }
        sb.append(descriptor.substring(descriptor.indexOf(')') + 1)).append(" ");
        sb.append(method.getTagName(pool).getValue());

        if(AccessFlags.containsFlag(method.getAccessFlags(), AccessFlags.ACC_VARARGS)){
            descriptor = descriptor.replaceFirst("\\[]\\)", "...)");
        }
        sb.append(descriptor.substring(descriptor.indexOf('('), descriptor.indexOf(')') + 1));

        final _Exceptions exceptionsAttribute = (_Exceptions) AttributeInfo.findFirst(AttributeInfo.EXCEPTIONS, method.getAttributes(), pool);
        if(exceptionsAttribute != null){
            sb.append(" throws ");
            for(int i = 0; i < exceptionsAttribute.getExceptionCount(); i++){
                sb.append(exceptionsAttribute.getTagException(pool, i).getContentString(pool));
                if(i < exceptionsAttribute.getExceptionCount() - 1){
                    sb.append(", ");
                }
            }
        }
        if(AccessFlags.containsFlag(method.getAccessFlags(), AccessFlags.ACC_ABSTRACT)){
            sb.append(";");
        } else {
            sb.append(" {");
        }
        return sb.toString();
    }

    // Format class name
    // e.g. public class ClassFormat {
    public static String format(final ClassType type) {
        final ConstantPool pool = type.getConstantPool();
        final StringBuilder sb = new StringBuilder();
        if(type.getAccessFlags() != 0 && (type.getAccessFlags() | AccessFlags.ACC_SUPER.mask()) != AccessFlags.ACC_SUPER.mask()) {
            sb.append(type.getAccessFlagsString()).append(" ");
        }
        if(!type.isEnum() && !type.isInterface()) {
            sb.append("class ");
        }
        sb.append(type.getName());
        final _Signature signatureAttribute = (_Signature) AttributeInfo.findFirst(AttributeInfo.SIGNATURE, type.getAttributes(), pool);
        if(signatureAttribute != null) {
            final String signature = signatureAttribute.getTagSignature(pool).getValue();
            int offset = 0;
            if(signature.charAt(0) == '<') {
                assert (signature.indexOf('>') != -1);
                int end = -1;
                for(int i = 1, inner = 0; i < signature.length(); i++) {
                    final char c = signature.charAt(i);
                    if(c == '<') {
                        inner++;
                    } else if(c == '>') {
                        if(inner > 0) {
                            inner--;
                        } else {
                            end = i;
                            break;
                        }
                    }
                }
                sb.append(Descriptor.hideObjectClass(Descriptor.decode(signature.substring(0, offset = end + 1))));
            }
            final String inherit = Descriptor.decode(signature.substring(offset));
            if((offset = inherit.indexOf(',')) != -1) {
                sb.append(Descriptor.hideObjectClass(" extends " + inherit.substring(0, offset)));

                sb.append(" implements").append(inherit.substring(offset + 1));
            } else {
                sb.append(Descriptor.hideObjectClass(" extends " + inherit));
            }
        } else {
            final String superclass = type.getTagSuperClass().getContentString(pool);
            if(!superclass.equals("java.lang.Object")) {
                sb.append(" extends ").append(superclass);
            }
            final int[] interfaces = type.getInterfaceIndices();
            if(interfaces.length > 0) {
                sb.append(" implements ");
                for(int i = 0; i < interfaces.length; i++) {
                    sb.append(type.getTagInterface(i).getContentString(pool));
                    if(i < interfaces.length - 1) {
                        sb.append(", ");
                    }
                }
            }
        }
        sb.append(" {");
        return sb.toString();
    }
}
