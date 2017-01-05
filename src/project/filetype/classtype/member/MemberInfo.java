package project.filetype.classtype.member;

import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagUTF8;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.property.PAccessFlags;
import project.property.Property;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public abstract class MemberInfo {

    private final int accessFlags;
    private final int nameIndex, descriptorIndex;
    private final AccessFlags.Type type;

    private final AttributeInfo[] attributes;

    private List<Property> properties = null;

    MemberInfo(final DataInputStream dis, final ConstantPool pool, final AccessFlags.Type type) throws IOException {
        this.type = type;
        this.accessFlags = dis.readUnsignedShort();
        this.nameIndex = dis.readUnsignedShort();
        this.descriptorIndex = dis.readUnsignedShort();
        this.attributes = new AttributeInfo[dis.readUnsignedShort()];
        for(int i = 0; i < this.attributes.length; i++) {
            this.attributes[i] = AttributeInfo.create(dis, pool);
        }
    }

    public int getAccessFlags() {
        return this.accessFlags;
    }

    public int getNameIndex() {
        return this.nameIndex;
    }

    public int getDescriptorIndex() {
        return this.descriptorIndex;
    }

    public int getAttributeCount() {
        return this.attributes.length;
    }

    public AttributeInfo[] getAttributes() {
        return this.attributes;
    }

    public String getAccessFlagsString() {
        return AccessFlags.decode(this.accessFlags, this.type);
    }

    public TagUTF8 getTagName(final ConstantPool pool) {
        return (TagUTF8) pool.getEntry(this.nameIndex);
    }

    public TagUTF8 getTagDescriptor(final ConstantPool pool) {
        return (TagUTF8) pool.getEntry(this.descriptorIndex);
    }

    public List<Property> getProperties(){
        if(this.properties == null){
            this.properties = new ArrayList<>();
            this.properties.add(new PAccessFlags(this.accessFlags, this.type));
        }
        return this.properties;
    }
}
