package project.filetype.classtype.bytecode.interpreter.item;

import project.filetype.classtype.bytecode.Instruction;

/**
 * Created by Ryan Thomson on 18/01/2017.
 */
public abstract class MethodItem {

    private final Instruction reference;

    MethodItem(final Instruction reference) {
        this.reference = reference;
    }

    public abstract String getValue();

    public abstract PrimitiveType getType();

    public Instruction getReference() {
        return this.reference;
    }
}
