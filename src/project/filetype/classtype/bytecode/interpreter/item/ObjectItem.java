package project.filetype.classtype.bytecode.interpreter.item;

import project.filetype.classtype.bytecode.Instruction;

/**
 * Created by Ryan Thomson on 18/01/2017.
 */
public class ObjectItem extends MethodItem {

    private final String name;

    public ObjectItem(final Instruction reference, final String name){
        super(reference);

        this.name = name;
    }
}
