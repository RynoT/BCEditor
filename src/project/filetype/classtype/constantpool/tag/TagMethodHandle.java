package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagMethodHandle extends PoolTag {

    private final int kind, index;

    public TagMethodHandle(final DataInputStream dis) throws IOException {
        this.kind = dis.readUnsignedShort();
        this.index = dis.readUnsignedShort();
    }

    public int getReferenceKind(){
        return this.kind;
    }

    public int getReferenceIndex(){
        return this.index;
    }

    @Override
    public int getTagId() {
        return PoolTag.TAG_METHOD_HANDLE;
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return "method handle";
    }
}
