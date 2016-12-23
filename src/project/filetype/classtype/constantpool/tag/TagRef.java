package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagRef extends PoolTag {

    private final int classIndex, nameTypeIndex;
    private final TagRefType type;

    public TagRef(final DataInputStream dis, final TagRefType type) throws IOException {
        this.type = type;

        this.classIndex = dis.readUnsignedShort();
        this.nameTypeIndex = dis.readUnsignedShort();
    }

    public TagRefType getType(){
        return this.type;
    }

    public int getClassIndex(){
        return this.classIndex;
    }

    public int getNameAndTypeIndex(){
        return this.nameTypeIndex;
    }

    public TagClass getTagClass(final ConstantPool pool){
        return (TagClass) pool.getEntry(this.classIndex);
    }

    public TagNameAndType getTagNameAndType(final ConstantPool pool){
        return (TagNameAndType) pool.getEntry(this.nameTypeIndex);
    }

    @Override
    public int getTagId() {
        switch(this.type){
            case FIELD:
                return PoolTag.TAG_FIELD_REF;
            case METHOD:
                return PoolTag.TAG_METHOD_REF;
            case INTERFACE_METHOD:
                return PoolTag.TAG_IM_REF;
        }
        return -1;
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        final String nameType = this.getTagNameAndType(pool).getContentString(pool);
        final int index = nameType.indexOf(' ');
        assert(index != -1);
        return nameType.substring(0, index + 1) + this.getTagClass(pool).getContentString(pool) + "." + nameType.substring(index + 1);
    }

    public enum TagRefType {
        FIELD, METHOD, INTERFACE_METHOD
    }
}
