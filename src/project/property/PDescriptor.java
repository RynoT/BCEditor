package project.property;

import project.filetype.classtype.constantpool.ConstantPool;

/**
 * Created by Ryan Thomson on 05/01/2017.
 */
public class PDescriptor extends Property {

    public static final String NAME = ".Descriptor";

    private final PPoolEntry descriptorEntry;

    public PDescriptor(final int descriptorIndex){
        this.descriptorEntry = new PPoolEntry(descriptorIndex);
    }

    @Override
    public Property[] getChildProperties() {
        return new Property[]{ this.descriptorEntry };
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return PDescriptor.NAME + "[" + this.descriptorEntry.getContentString(pool) + "]";
    }
}
