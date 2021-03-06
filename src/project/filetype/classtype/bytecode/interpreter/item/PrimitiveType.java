package project.filetype.classtype.bytecode.interpreter.item;

import project.filetype.classtype.bytecode.opcode.Opcode;
import project.filetype.classtype.constantpool.PoolTag;
import project.filetype.classtype.constantpool.tag.*;

/**
 * Created by Ryan Thomson on 18/01/2017.
 */
public enum PrimitiveType {
    INTEGER(1), FLOAT(1), LONG(2), DOUBLE(2), OBJECT(1), INVALID(1);

    private final int stackSize;

    PrimitiveType(final int stackSize) {
        this.stackSize = stackSize;
    }

    public int getStackSize() {
        return this.stackSize;
    }

    public static boolean isNumber(final PrimitiveType type){
        return type == PrimitiveType.INTEGER || type == PrimitiveType.FLOAT
                || type == PrimitiveType.LONG || type == PrimitiveType.DOUBLE;
    }

    public static PrimitiveType get(final Opcode opcode) {
        return PrimitiveType.get(opcode.name().charAt(1));
    }

    public static PrimitiveType get(final char c) {
        switch(c) {
            case 'i': // int
            case 'b': // byte/boolean
            case 'c': // char
            case 's': // short
                return PrimitiveType.INTEGER;
            case 'f': // float
                return PrimitiveType.FLOAT;
            case 'l': // long
                return PrimitiveType.LONG;
            case 'd': // double
                return PrimitiveType.DOUBLE;
            case 'a': // object
                return PrimitiveType.OBJECT;
        }
        return null;
    }

    public static PrimitiveType get(final String str) {
        if(str.equals("int") || str.equals("boolean")) {
            return PrimitiveType.INTEGER;
        }
        if(str.equals("float")) {
            return PrimitiveType.FLOAT;
        }
        if(str.equals("long")) {
            return PrimitiveType.LONG;
        }
        if(str.equals("double")) {
            return PrimitiveType.DOUBLE;
        }
        return PrimitiveType.OBJECT;
    }

    public static PrimitiveType get(final PoolTag tag) {
        if(tag instanceof TagInteger) {
            return PrimitiveType.INTEGER;
        }
        if(tag instanceof TagFloat) {
            return PrimitiveType.FLOAT;
        }
        if(tag instanceof TagLong) {
            return PrimitiveType.LONG;
        }
        if(tag instanceof TagDouble) {
            return PrimitiveType.DOUBLE;
        }
        if(tag instanceof TagString) {
            return PrimitiveType.OBJECT;
        }
        return null;
    }

    public static String getName(final char c){
        switch(c) {
            case 'i':
                return "int";
            case 'b':
                return "byte";
            case 'c':
                return "char";
            case 's':
                return "short";
            case 'f':
                return "float";
            case 'l':
                return "long";
            case 'd':
                return "double";
        }
        return null;
    }
}
