package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagRef extends PoolTag {

    public static final String FIELD_NAME = "Field ref";
    public static final String METHOD_NAME = "Method ref";
    public static final String INTERFACE_METHOD_NAME = "Interface ref";

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
    public int getPoolTagId() {
        switch(this.type){
            case FIELD:
                return PoolTag.TAG_FIELD_REF;
            case METHOD:
                return PoolTag.TAG_METHOD_REF;
            case INTERFACE_METHOD:
                return PoolTag.TAG_IM_REF;
        }
        assert (false);
        return -1;
    }

    @Override
    public int getPoolTagBitCount() {
        return 32;
    }

    @Override
    public String getPoolTagName() {
        switch(this.type){
            case FIELD:
                return TagRef.FIELD_NAME;
            case METHOD:
                return TagRef.METHOD_NAME;
            case INTERFACE_METHOD:
                return TagRef.INTERFACE_METHOD_NAME;
        }
        assert (false);
        return null;
    }

    @Override
    public PoolTag[] getLinkedTags(final ConstantPool pool) {
        return new PoolTag[]{ this.getTagClass(pool), this.getTagNameAndType(pool) };
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
