package project.filetype.classtype.member.attributes;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;
import project.property.PPoolEntry;
import project.property.Property;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _ConstantValue extends AttributeInfo {

    private final int constantIndex;

    _ConstantValue(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        assert(length == 2);
        this.constantIndex = dis.readUnsignedShort();
    }

    public int getConstantIndex(){
        return this.constantIndex;
    }

    public PoolTag getTagConstant(final ConstantPool pool){
        return pool.getEntry(this.constantIndex); //we don't know what type of constant this is
    }

    @Override
    public Property[] getProperties() {
        return new Property[]{ new PPoolEntry(this.constantIndex)};
    }
}
