package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagEmpty extends PoolTag {

    public static final String NAME = "empty";

    @Override
    public int getPoolTagId() {
        return -1;
    }

    @Override
    public int getPoolTagBitCount() {
        return 0;
    }

    @Override
    public String getPoolTagName() {
        return TagEmpty.NAME;
    }

    @Override
    public PoolTag[] getLinkedTags(final ConstantPool pool) {
        return new PoolTag[0];
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return "empty";
    }
}
