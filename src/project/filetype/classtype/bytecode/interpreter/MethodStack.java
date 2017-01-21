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

    public int getCount() {
        return this.pointer + 1;
    }

    public MethodItem get(final int index){
        assert (this.pointer - index) >= 0 && (this.pointer - index) < this.stack.length
                : "Bounds must be checked before calling";
        return this.stack[this.pointer - index];
    }

    public void swap(final int index1, final int index2) {
        assert index1 >= 0 && index1 < this.stack.length && index2 >= 0 && index2 < this.stack.length;
        final MethodItem temp = this.stack[index1];
        this.stack[index1] = this.stack[index2];
        this.stack[index2] = temp;
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

    public boolean insert(final MethodItem item, int index) {
        // Inserting requires moving elements in the array
        if(index < 0 || index > this.pointer + 1) {
            return false;
        }
        if(this.pointer == this.stack.length - 1) {
            return false;
        }
        index = Math.max(0, this.pointer - index);
        // Shift the array to make space for new element
        System.arraycopy(this.stack, index, this.stack, index + 1, this.stack.length - index - 1);

        // Insert the element
        this.stack[index] = item;
        this.pointer++;
        return true;
    }
}
