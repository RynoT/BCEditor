package project.property;

import project.filetype.classtype.constantpool.ConstantPool;

/**
 * Created by Ryan Thomson on 05/01/2017.
 */
public class PName extends Property {

    public static final String NAME = ".Name";

    private final PPoolEntry nameEntry;

    public PName(final int nameIndex){
        this.nameEntry = new PPoolEntry(nameIndex);
    }

    @Override
    public Property[] getChildProperties() {
        return new Property[]{ this.nameEntry };
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return PName.NAME + "[" + this.nameEntry.getContentString(pool) + "]";
    }
}
