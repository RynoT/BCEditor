package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;
import project.filetype.classtype.member.attributes._BootstrapMethods;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagInvokeDynamic extends PoolTag {

    private final int bootstrapMethodIndex, nameTypeIndex;

    public TagInvokeDynamic(final DataInputStream dis) throws IOException {
        this.bootstrapMethodIndex = dis.readUnsignedShort();
        this.nameTypeIndex = dis.readUnsignedShort();
    }

    public int getBootstrapMethodIndex(){
        return this.bootstrapMethodIndex;
    }

    public int getNameAndTypeIndex(){
        return this.nameTypeIndex;
    }

    public _BootstrapMethods.BootstrapMethod getBootstrapMethod(final _BootstrapMethods methods){
        return methods.getBootstrapMethods()[this.bootstrapMethodIndex];
    }

    public TagNameAndType getTagNameAndType(final ConstantPool pool){
        return (TagNameAndType) pool.getEntry(this.nameTypeIndex);
    }

    @Override
    public int getPoolTagId() {
        return PoolTag.TAG_INVOKE_DYNAMIC;
    }

    @Override
    public int getPoolTagBitCount() {
        return 32;
    }

    @Override
    public String getPoolTagName() {
        return "Invoke Dynamic";
    }

    @Override
    public PoolTag[] getLinkedTags(final ConstantPool pool) {
        return new PoolTag[]{ this.getTagNameAndType(pool) };
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        // We don't have access to the class attributes here so we can't get the bootstrap method.
        // All we can do is tell the user what index the bootstrap method is.
        return "(bootstrap #" + this.bootstrapMethodIndex + ") " + this.getTagNameAndType(pool).getContentString(pool);
    }
}
