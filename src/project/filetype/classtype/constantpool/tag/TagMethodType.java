package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.Descriptor;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagMethodType extends PoolTag {

    private final int descriptorIndex;

    public TagMethodType(final DataInputStream dis) throws IOException {
        this.descriptorIndex = dis.readUnsignedShort();
    }

    public int getDescriptorIndex(){
        return this.descriptorIndex;
    }

    public TagUTF8 getTagDescriptor(final ConstantPool pool){
        return (TagUTF8) pool.getEntry(this.descriptorIndex);
    }

    @Override
    public int getPoolTagId() {
        return PoolTag.TAG_METHOD_TYPE;
    }

    @Override
    public int getPoolTagBitCount() {
        return 16;
    }

    @Override
    public String getPoolTagName() {
        return "Method Type";
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return Descriptor.decode(this.getTagDescriptor(pool).getValue());
    }
}
