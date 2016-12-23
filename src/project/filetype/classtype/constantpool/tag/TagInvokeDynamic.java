package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

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

    @Override
    public int getTagId() {
        return PoolTag.TAG_INVOKE_DYNAMIC;
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return "invoke dynamic";
    }
}
