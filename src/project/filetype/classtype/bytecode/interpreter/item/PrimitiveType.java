package project.filetype.classtype.bytecode.interpreter.item;

/**
 * Created by Ryan Thomson on 18/01/2017.
 */
public enum PrimitiveType {
    INTEGER, FLOAT, LONG, DOUBLE, OBJECT, INVALID;

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
}
