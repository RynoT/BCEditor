package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.Descriptor;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagNameAndType extends PoolTag {

    public static final String NAME = "Name and Type";

    private final int nameIndex, descriptorIndex;

    public TagNameAndType(final DataInputStream dis) throws IOException {
        this.nameIndex = dis.readUnsignedShort();
        this.descriptorIndex = dis.readUnsignedShort();
    }

    public int getNameIndex(){
        return this.nameIndex;
    }

    public int getDescriptorIndex() {
        return this.descriptorIndex;
    }

    public TagUTF8 getTagName(final ConstantPool pool){
        return (TagUTF8) pool.getEntry(this.nameIndex);
    }

    public TagUTF8 getTagDescriptor(final ConstantPool pool){
        return (TagUTF8) pool.getEntry(this.descriptorIndex);
    }

    @Override
    public int getPoolTagId() {
        return PoolTag.TAG_NAME_AND_TYPE;
    }

    @Override
    public int getPoolTagBitCount() {
        return 32;
    }

    @Override
    public String getPoolTagName() {
        return TagNameAndType.NAME;
    }

    @Override
    public PoolTag[] getLinkedTags(final ConstantPool pool) {
        return new PoolTag[]{ this.getTagName(pool), this.getTagDescriptor(pool) };
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        final String name = this.getTagName(pool).getValue();
        final String descriptor = Descriptor.decode(this.getTagDescriptor(pool).getValue());
        final int index = descriptor.indexOf(')');
        if(index != -1) {
            final String returnType = descriptor.substring(index + 1);
            return returnType + " " + name + descriptor.substring(0, index + 1);
        }
        return descriptor + " " + name;
    }
}
