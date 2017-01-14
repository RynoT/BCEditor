package project.filetype.classtype.member.attributes;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagClass;
import project.property.PPoolEntry;
import project.property.Property;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _Exceptions extends AttributeInfo {

    private final int[] exceptionIndices;

    _Exceptions(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        this.exceptionIndices = new int[dis.readUnsignedShort()];
        assert (length == this.exceptionIndices.length * 2 + 2);
        for(int i = 0; i < this.exceptionIndices.length; i++){
            this.exceptionIndices[i] = dis.readUnsignedShort();
        }
    }

    public int getExceptionCount(){
        return this.exceptionIndices.length;
    }

    public int[] getExceptionIndices(){
        return this.exceptionIndices;
    }

    public TagClass getTagException(final ConstantPool pool, final int index){
        assert(index >= 0 && index < this.exceptionIndices.length);
        return (TagClass) pool.getEntry(this.exceptionIndices[index]);
    }

    @Override
    public Property[] getProperties() {
        final Property[] properties = new Property[this.exceptionIndices.length];
        for(int i = 0; i < properties.length; i++){
            properties[i] = new PPoolEntry(this.exceptionIndices[i]);
        }
        return properties;
    }
}
