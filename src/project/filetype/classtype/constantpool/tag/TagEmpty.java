package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagEmpty extends PoolTag {

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
        return "empty";
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return "empty";
    }
}
