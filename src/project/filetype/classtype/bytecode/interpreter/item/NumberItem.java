package project.filetype.classtype.bytecode.interpreter.item;

import project.filetype.classtype.bytecode.Instruction;

/**
 * Created by Ryan Thomson on 18/01/2017.
 */
public class NumberItem extends MethodItem {

    private final String value;
    private final NumberType type;

    public NumberItem(final Instruction reference, final String value, final NumberType type) {
        super(reference);

        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public NumberType getType() {
        return this.type;
    }

    public enum NumberType {
        INTEGER, FLOAT, LONG, DOUBLE
    }
}
