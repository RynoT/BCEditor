package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagFloat extends PoolTag {

    private final float value;

    public TagFloat(final DataInputStream dis) throws IOException {
        this.value = dis.readFloat();
    }

    public float getValue(){
        return this.value;
    }

    @Override
    public int getPoolTagId() {
        return PoolTag.TAG_FLOAT;
    }

    @Override
    public int getPoolTagBitCount() {
        return 32;
    }

    @Override
    public String getPoolTagName() {
        return "Float";
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return String.valueOf(this.value);
    }
}
