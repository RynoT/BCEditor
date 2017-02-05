package project.filetype.classtype.bytecode.interpreter.item;

import project.filetype.classtype.bytecode.Instruction;

/**
 * Created by Ryan Thomson on 18/01/2017.
 */
public class ArrayRefItem extends ObjectItem {

    public static final String[] TYPES = {"boolean", "char", "float", "double", "byte", "short", "int", "long"};

    private final PrimitiveType type;
    private final int dimensions;

    public ArrayRefItem(final Instruction reference, final String name, final PrimitiveType type, final int dimensions) {
        super(reference, name);

        this.type = type;
        this.dimensions = dimensions;
    }

    public int getDimensions() {
        return this.dimensions;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public PrimitiveType getType() {
        return this.type;
    }

    public static String getType(final int type){
        assert type >= 4 && type <= 11 : "Invalid type index";
        return ArrayRefItem.TYPES[type - 4];
    }

    public static PrimitiveType getPrimitive(final int type){
        assert type >= 4 && type <= 11 : "Invalid type index";
        switch(type){
            case 4:
            case 5:
            case 8:
            case 9:
            case 10:
                return PrimitiveType.INTEGER;
            case 6:
                return PrimitiveType.FLOAT;
            case 7:
                return PrimitiveType.DOUBLE;
            case 11:
                return PrimitiveType.LONG;
        }
        assert false; //can't ever get here
        return null;
    }

}
