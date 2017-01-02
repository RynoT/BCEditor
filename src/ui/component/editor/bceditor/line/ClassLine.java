package ui.component.editor.bceditor.line;

import project.filetype.ClassType;
import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.Descriptor;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._Signature;

import java.awt.font.TextAttribute;

/**
 * Created by Ryan Thomson on 02/01/2017.
 */
public class ClassLine extends Line {

    private final ClassType type;

    public ClassLine(final ClassType type, final int indent) {
        super(indent);

        this.type = type;
    }

    @Override
    public void update() {
        assert this.type != null;

        int idxAccess, idxName, idxExtend, idxImplement = -1;

        final ConstantPool pool = this.type.getConstantPool();
        final StringBuilder sb = new StringBuilder();
        if(this.type.getAccessFlags() != 0 && (this.type.getAccessFlags() | AccessFlags.ACC_SUPER.mask()) != AccessFlags.ACC_SUPER.mask()) {
            sb.append(this.type.getAccessFlagsString()).append(" ");
        }
        if(!this.type.isEnum() && !this.type.isInterface()) {
            sb.append("class ");
        }
        idxAccess = sb.length();
        sb.append(this.type.getName());
        idxName = sb.length();
        final _Signature signatureAttribute = (_Signature) AttributeInfo.findFirst(AttributeInfo.SIGNATURE, this.type.getAttributes(), pool);
        if(signatureAttribute != null) {
            final String signature = signatureAttribute.getTagSignature(pool).getValue();
            System.out.println(signature);
            int offset = 0;
            if(signature.charAt(0) == '<') {
                assert signature.indexOf('>') != -1;

                offset = Descriptor.getOffset(signature, '>', 0, signature.length()) + 1;
                sb.append(Descriptor.hideObjectClass(Descriptor.decode(signature.substring(0, offset))));
                idxName = sb.length();
            }
            final String inherit = Descriptor.decode(signature.substring(offset));
            offset = Descriptor.getOffset(inherit, ',', 0, inherit.length());
            if(offset != -1) {
                sb.append(Descriptor.hideObjectClass(" extends " + inherit.substring(0, offset)));
                idxExtend = sb.length();

                sb.append(" implements");
                idxImplement = sb.length();
                sb.append(inherit.substring(offset + 1));
            } else {
                sb.append(Descriptor.hideObjectClass(" extends " + inherit));
                idxExtend = sb.length();
            }
        } else {
            final String superclass = this.type.getTagSuperClass().getContentString(pool);
            if(!superclass.equals("java.lang.Object")) {
                sb.append(" extends ").append(superclass);
            }
            idxExtend = sb.length();
            final int[] interfaces = this.type.getInterfaceIndices();
            if(interfaces.length > 0) {
                sb.append(" implements ");
                idxImplement = sb.length();
                for(int i = 0; i < interfaces.length; i++) {
                    sb.append(this.type.getTagInterface(i).getContentString(pool));
                    if(i < interfaces.length - 1) {
                        sb.append(", ");
                    }
                }
            }
        }
        sb.append(" {");

        final String str = sb.toString();
        super.setString(str);

        if(idxAccess != 0){
            super.attributes.addAttribute(TextAttribute.FOREGROUND, Line.ORANGE_COLOR, 0, idxAccess);
        }
        if(idxExtend != idxName) {
            super.attributes.addAttribute(TextAttribute.FOREGROUND, Line.ORANGE_COLOR, idxName, str.indexOf(' ', idxName + 1)); //color 'extends'
        }
        if(idxImplement != -1 && idxImplement != idxExtend){
            super.attributes.addAttribute(TextAttribute.FOREGROUND, Line.ORANGE_COLOR, idxExtend, idxImplement);
        }
    }
}
