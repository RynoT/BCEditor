package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagUTF8 extends PoolTag {

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
    public int getTagId() {
        return PoolTag.TAG_UTF8;
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return this.value;
    }
}
