package project.filetype.classtype.bytecode.interpreter.item;

import project.filetype.classtype.bytecode.Instruction;

/**
 * Created by Ryan Thomson on 18/01/2017.
 */
public class NumberItem extends MethodItem {

    private final String value;
    private final PrimitiveType type;

    public NumberItem(final Instruction reference, final String value, final PrimitiveType type) {
        super(reference);

        // NumberItem must have a number type
        assert type == PrimitiveType.INTEGER || type == PrimitiveType.FLOAT
                || type == PrimitiveType.LONG || type == PrimitiveType.DOUBLE;

        this.value = value;
        this.type = type;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public PrimitiveType getType() {
        return this.type;
    }
}
