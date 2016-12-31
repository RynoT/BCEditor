package project.filetype.classtype.constantpool.tag;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class TagDouble extends PoolTag {

    private final double value;

    public TagDouble(final DataInputStream dis) throws IOException {
        this.value = dis.readDouble();
    }

    public double getValue(){
        return this.value;
    }

    @Override
    public int getPoolTagId() {
        return PoolTag.TAG_DOUBLE;
    }

    @Override
    public int getPoolTagBitCount() {
        return 64;
    }

    @Override
    public String getPoolTagName() {
        return "Double";
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        return String.valueOf(this.value);
    }
}
