package project.filetype.classtype.member.attributes;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagNameAndType;
import project.filetype.classtype.constantpool.tag.TagUTF8;
import project.property.PPoolEntry;
import project.property.Property;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _EnclosingMethod extends AttributeInfo {

    private final int classIndex, methodIndex;

    _EnclosingMethod(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        assert (length == 4);
        this.classIndex = dis.readUnsignedShort();
        this.methodIndex = dis.readUnsignedShort();
    }

    public int getClassIndex() {
        return this.classIndex;
    }

    public int getMethodIndex() {
        return this.methodIndex;
    }

    public TagUTF8 getTagClass(final ConstantPool pool){
        return (TagUTF8) pool.getEntry(this.classIndex);
    }

    public TagNameAndType getTagMethod(final ConstantPool pool){
        return (TagNameAndType) pool.getEntry(this.methodIndex);
    }

    @Override
    public Property[] getProperties() {
        return new Property[]{ new PPoolEntry(this.classIndex), new PPoolEntry(this.methodIndex) };
    }
}
