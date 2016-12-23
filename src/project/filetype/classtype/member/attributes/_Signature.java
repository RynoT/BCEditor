package project.filetype.classtype.member.attributes;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagUTF8;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _Signature extends AttributeInfo {

    private final int signatureIndex;

    _Signature(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        assert (length == 2);
        this.signatureIndex = dis.readUnsignedShort();
    }

    public int getSignatureIndex(){
        return this.signatureIndex;
    }

    public TagUTF8 getTagSignature(final ConstantPool pool){
        return (TagUTF8) pool.getEntry(this.signatureIndex);
    }
}
