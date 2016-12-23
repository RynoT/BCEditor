package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagString extends PoolTag {

    private final int stringIndex;

    public TagString(final DataInputStream dis) throws IOException {
        this.stringIndex = dis.readUnsignedShort();
    }

    public int getStringIndex(){
        return this.stringIndex;
    }

    public TagUTF8 getTagString(final ConstantPool pool){
        return (TagUTF8) pool.getEntry(this.stringIndex);
    }

    @Override
    public int getTagId() {
        return PoolTag.TAG_STRING;
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return this.getTagString(pool).getContentString(pool);
    }
}
