package project.filetype.classtype.member.attributes;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _SourceDebugExtension extends AttributeInfo {

    private final byte[] raw;

    _SourceDebugExtension(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        this.raw = new byte[length];
    }

    public byte[] getRawBytes(){
        return this.raw;
    }

    public String getString(){
        return new String(this.raw);
    }
}
