package project.filetype.classtype.member.attributes;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _Default extends AttributeInfo {

    private final byte[] raw;

    _Default(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        // We do not support this attribute. Just store the bytes raw so we can continue loading.
        this.raw = new byte[length];
        dis.readFully(this.raw);
    }

    public byte[] getRawBytes(){
        return this.raw;
    }
}
