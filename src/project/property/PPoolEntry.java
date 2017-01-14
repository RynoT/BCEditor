package project.property;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

/**
 * Created by Ryan Thomson on 05/01/2017.
 */
public class PPoolEntry extends Property {

    private final int index;
    private String string;

    public PPoolEntry(final int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public Property[] getChildProperties() {
        return new Property[0];
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        final PoolTag tag = pool.getEntry(this.index);
        if(this.string == null) {
            this.string = tag.getContentString(pool);
        }
        return tag.getPoolTagName() + "(" + this.index + ", '" + this.string + "')";
    }
}
