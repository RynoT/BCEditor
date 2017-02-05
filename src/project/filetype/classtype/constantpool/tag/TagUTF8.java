package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagUTF8 extends PoolTag {

    public static final String NAME = "UTF-8";

    private final String value;

    public TagUTF8(final DataInputStream dis) throws IOException {
        final byte[] bytes = new byte[dis.readUnsignedShort()];
        dis.readFully(bytes);
        this.value = new String(bytes);
    }

    public String getValue(){
        return this.value;
    }

    @Override
    public int getPoolTagId() {
        return PoolTag.TAG_UTF8;
    }

    @Override
    public int getPoolTagBitCount() {
        return this.value.getBytes().length * Byte.SIZE;
    }

    @Override
    public String getPoolTagName() {
        return TagUTF8.NAME;
    }

    @Override
    public PoolTag[] getLinkedTags(final ConstantPool pool) {
        return new PoolTag[0];
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return this.value;
    }
}
