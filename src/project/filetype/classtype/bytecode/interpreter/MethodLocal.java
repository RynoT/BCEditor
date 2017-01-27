package project.filetype.classtype.bytecode.interpreter;

import project.filetype.classtype.bytecode.interpreter.item.MethodItem;

/**
 * Created by Ryan Thomson on 18/01/2017.
 */
class MethodLocal {

    private final MethodItem[] locals;
    private final boolean isStatic;

    MethodLocal(final int max, final boolean isStatic) {
        this.locals = new MethodItem[isStatic ? max : max + 1]; //accommodate space for 'this'
        this.isStatic = isStatic;
    }

    public MethodItem get(final int index) {
        assert index >= 0 && index < this.locals.length;
        return this.locals[index];
    }

    public void set(final MethodItem item, final int index) {
        assert index >= 0 && index < this.locals.length;
        this.locals[index] = item;
    }

    public String getLocalName(final int index) {
        if(index == 0 && !this.isStatic) {
            return this.locals[index].getValue();
        }
        return "local" + index;
    }
}
