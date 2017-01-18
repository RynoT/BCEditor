package project.filetype.classtype.bytecode.interpreter;

import project.filetype.classtype.bytecode.interpreter.item.MethodItem;

/**
 * Created by Ryan Thomson on 18/01/2017.
 */
class MethodLocal {

    private final MethodItem[] locals;

    MethodLocal(final int max) {
        this.locals = new MethodItem[max];
    }

    public MethodItem get(final int index) {
        assert index >= 0 && index < this.locals.length;
        return this.locals[index];
    }

    public void set(final MethodItem item, final int index) {
        assert index >= 0 && index < this.locals.length;
        this.locals[index] = item;
    }
}
