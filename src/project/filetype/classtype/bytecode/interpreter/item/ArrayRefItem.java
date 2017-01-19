package project.filetype.classtype.bytecode.interpreter.item;

import project.filetype.classtype.bytecode.Instruction;

/**
 * Created by Ryan Thomson on 18/01/2017.
 */
public class ArrayRefItem extends MethodItem {

    private final PrimitiveType type;
    private final int dimensions;

    public ArrayRefItem(final Instruction reference, final PrimitiveType type, final int dimensions) {
        super(reference);

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

}
