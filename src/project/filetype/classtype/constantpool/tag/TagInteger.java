package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
// int, short, char, byte, boolean
public class TagInteger extends PoolTag {

    private final int value;

    public TagInteger(final DataInputStream dis) throws IOException {
        this.value = dis.readInt();
    }

    public int getValue(){
        return this.value;
    }

    @Override
    public int getPoolTagId() {
        return PoolTag.TAG_INTEGER;
    }

    @Override
    public int getPoolTagBitCount() {
        return 32;
    }

    @Override
    public String getPoolTagName() {
        return "Integer";
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return String.valueOf(this.value);
    }
}
