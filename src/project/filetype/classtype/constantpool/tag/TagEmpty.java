package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagEmpty extends PoolTag {

    @Override
    public int getTagId() {
        return -1;
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return "";
    }
}
