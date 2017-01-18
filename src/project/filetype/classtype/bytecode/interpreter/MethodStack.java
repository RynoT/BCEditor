package project.filetype.classtype.bytecode.interpreter;

import project.filetype.classtype.bytecode.interpreter.item.MethodItem;

/**
 * Created by Ryan Thomson on 18/01/2017.
 * <p>
 * LIFO. Stack is safe and will return false or null on fail.
 */
class MethodStack {

    private final MethodItem[] stack;
    private int pointer = -1;

    MethodStack(final int max) {
        this.stack = new MethodItem[max];
    }

    public MethodItem peek() {
        if(this.pointer == -1) {
            return null;
        }
        return this.stack[this.pointer];
    }

    public MethodItem pop() {
        if(this.pointer == -1) {
            return null;
        }
        return this.stack[this.pointer--];
    }

    public boolean push(final MethodItem item) {
        if(this.pointer == this.stack.length - 1) {
            return false;
        }
        this.stack[++this.pointer] = item;
        return true;
    }
}
