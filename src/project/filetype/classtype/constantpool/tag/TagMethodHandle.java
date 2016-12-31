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

        assert (this.kind >= 1 && this.kind <= 9);
    }

    public int getReferenceKind(){
        return this.kind;
    }

    public int getReferenceIndex(){
        return this.index;
    }

    public PoolTag getTagReference(final ConstantPool pool){
        return pool.getEntry(this.index);
    }

    @Override
    public int getPoolTagId() {
        return PoolTag.TAG_METHOD_HANDLE;
    }

    @Override
    public int getPoolTagBitCount() {
        return 32;
    }

    @Override
    public String getPoolTagName() {
        return "Method Handle";
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return this.kind + " (" + this.getTagReference(pool).getContentString(pool) + ")";
    }
}
