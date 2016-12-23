package project.filetype.classtype.member;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagUTF8;
import project.filetype.classtype.member.attributes.AttributeInfo;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public abstract class MemberInfo {

    private final int accessFlags;
    private final int nameIndex, descriptorIndex;

    private final AttributeInfo[] attributes;

    MemberInfo(final DataInputStream dis, final ConstantPool pool) throws IOException {
        this.accessFlags = dis.readUnsignedShort();
        this.nameIndex = dis.readUnsignedShort();
        this.descriptorIndex = dis.readUnsignedShort();
        this.attributes = new AttributeInfo[dis.readUnsignedShort()];
        for(int i = 0; i < this.attributes.length; i++){
            this.attributes[i] = AttributeInfo.create(dis, pool);
        }
    }

    public abstract String getAccessFlagsString();

    public int getAccessFlags(){
        return this.accessFlags;
    }

    public int getNameIndex(){
        return this.nameIndex;
    }

    public int getDescriptorIndex(){
        return this.descriptorIndex;
    }

    public AttributeInfo[] getAttributes(){
        return this.attributes;
    }

    public TagUTF8 getTagName(final ConstantPool pool){
        return (TagUTF8) pool.getEntry(this.nameIndex);
    }

    public TagUTF8 getTagDescriptor(final ConstantPool pool){
        return (TagUTF8) pool.getEntry(this.descriptorIndex);
    }
}
