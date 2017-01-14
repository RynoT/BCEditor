package project.filetype.classtype.member.attributes;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagUTF8;
import project.property.PPoolEntry;
import project.property.Property;

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

    public int getSignatureIndex() {
        return this.signatureIndex;
    }

    public TagUTF8 getTagSignature(final ConstantPool pool) {
        return (TagUTF8) pool.getEntry(this.signatureIndex);
    }

    @Override
    public Property[] getProperties() {
        return new Property[]{ new PPoolEntry(this.signatureIndex) };
    }

    //    @Override
//    public Pair<String, AttributedString> getContentString(final ConstantPool pool) {
//        int idxIndexStart, idxIndexEnd, idxStringStart, idxStringEnd;
//
//        final StringBuilder sb = new StringBuilder();
//        sb.append("SignatureIndex(");
//        {
//            idxIndexStart = sb.length();
//            sb.append(IBCEditor.formatIndexPool(this.signatureIndex));
//            idxIndexEnd = sb.length();
//
//            sb.append(", ");
//
//            idxStringStart = sb.length();
//            sb.append("\"").append(this.getTagSignature(pool).getValue()).append("\"");
//            idxStringEnd = sb.length();
//        }
//        sb.append(")");
//
//        final String string = sb.toString();
//        final AttributedString attributes = new AttributedString(string);
//
//        attributes.addAttribute(TextAttribute.FOREGROUND, IBCTextEditor.ATTRIBUTE_COLOR);
//        attributes.addAttribute(TextAttribute.FOREGROUND, IBCTextEditor.INDEX_POOL_COLOR, idxIndexStart, idxIndexEnd);
//        attributes.addAttribute(TextAttribute.FOREGROUND, IBCTextEditor.STRING_COLOR, idxStringStart, idxStringEnd);
//
//        return Pair.create(string, attributes);
//    }
}
