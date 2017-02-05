package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.Descriptor;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagClass extends PoolTag {

    public static final String NAME = "Class ref";

    private final int nameIndex;

    public TagClass(final DataInputStream dis) throws IOException {
        this.nameIndex = dis.readUnsignedShort();
    }

    public int getNameIndex(){
        return this.nameIndex;
    }

    public TagUTF8 getTagName(final ConstantPool pool){
        return (TagUTF8) pool.getEntry(this.nameIndex);
    }

    @Override
    public int getPoolTagId() {
        return PoolTag.TAG_CLASS;
    }

    @Override
    public int getPoolTagBitCount() {
        return 16;
    }

    @Override
    public String getPoolTagName() {
        return TagClass.NAME;
    }

    @Override
    public PoolTag[] getLinkedTags(final ConstantPool pool) {
        return new PoolTag[]{ this.getTagName(pool) };
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        String text = this.getTagName(pool).getValue();
        if(text.length() > 0 && text.charAt(0) == '['){
            text = Descriptor.decode(text);
        }
        return text.replace('/', '.');
    }
}
