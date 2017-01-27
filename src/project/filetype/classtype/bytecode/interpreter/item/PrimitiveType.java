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

    public static PrimitiveType get(final Opcode opcode) {
        return PrimitiveType.get(opcode.name().charAt(1));
    }

    public static PrimitiveType get(final char c) {
        switch(c) {
            case 'i':
                return PrimitiveType.INTEGER;
            case 'f':
                return PrimitiveType.FLOAT;
            case 'l':
                return PrimitiveType.LONG;
            case 'd':
                return PrimitiveType.DOUBLE;
            case 'a':
                return PrimitiveType.OBJECT;
        }
        return null;
    }

    public static PrimitiveType get(final String str) {
        if(str.startsWith("int")) {
            return PrimitiveType.INTEGER;
        }
        if(str.startsWith("float")) {
            return PrimitiveType.FLOAT;
        }
        if(str.startsWith("long")) {
            return PrimitiveType.LONG;
        }
        if(str.startsWith("double")) {
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
}
