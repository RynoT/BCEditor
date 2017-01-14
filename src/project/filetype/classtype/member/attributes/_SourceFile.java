package project.filetype.classtype.member.attributes;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagUTF8;
import project.property.PPoolEntry;
import project.property.Property;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _SourceFile extends AttributeInfo {

    private final int sourceIndex;

    _SourceFile(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        assert (length == 2);
        this.sourceIndex = dis.readUnsignedShort();
    }

    public int getSourceIndex() {
        return this.sourceIndex;
    }

    public TagUTF8 getTagSource(final ConstantPool pool) {
        return (TagUTF8) pool.getEntry(this.sourceIndex);
    }

    @Override
    public Property[] getProperties() {
        return new Property[]{ new PPoolEntry(this.sourceIndex) };
    }
}
