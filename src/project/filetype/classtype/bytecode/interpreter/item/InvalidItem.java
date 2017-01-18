package project.filetype.classtype.bytecode.interpreter.item;

import project.filetype.classtype.bytecode.Instruction;

/**
 * Created by Ryan Thomson on 18/01/2017.
 */
public class InvalidItem extends MethodItem {

    public InvalidItem(final Instruction reference) {
        super(reference);
    }

    @Override
    public String getValue() {
        return null;
    }

}
