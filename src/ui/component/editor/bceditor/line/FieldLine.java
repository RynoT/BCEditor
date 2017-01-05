package ui.component.editor.bceditor.line;

import project.filetype.classtype.Descriptor;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.FieldInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._ConstantValue;
import project.filetype.classtype.member.attributes._Signature;

import java.awt.*;
import java.awt.font.TextAttribute;

/**
 * Created by Ryan Thomson on 30/12/2016.
 */
public class FieldLine extends Line {

    private static final Color NAME_COLOR = new Color(150, 120, 170);
    private static final Color CONSTANT_COLOR = new Color(92, 160, 173);

    private final FieldInfo field;

    public FieldLine(final FieldInfo field, final int indent) {
        super(indent);

        assert field != null;
        this.field = field;
    }

    public FieldInfo getField(){
        return this.field;
    }

    @Override
    public void update(final ConstantPool pool) {
        int idxAccess, idxType, idxName, idxValue = -1;

        final StringBuilder sb = new StringBuilder();
        sb.append(this.field.getAccessFlagsString()).append(" ");
        idxAccess = sb.length();

        final _Signature signature = (_Signature) AttributeInfo.findFirst(AttributeInfo.SIGNATURE, this.field.getAttributes(), pool);
        if(signature != null) {
            sb.append(Descriptor.decode(signature.getTagSignature(pool).getValue())).append(" ");
        } else {
            sb.append(Descriptor.decode(this.field.getTagDescriptor(pool).getValue())).append(" ");
        }
        idxType = sb.length();
        sb.append(this.field.getTagName(pool).getValue());
        idxName = sb.length();

        final _ConstantValue constantValue = (_ConstantValue) AttributeInfo.findFirst(AttributeInfo.CONSTANT_VALUE, this.field.getAttributes(), pool);
        if(constantValue != null) {
            sb.append(" = ");
            idxValue = sb.length();
            sb.append(constantValue.getTagConstant(pool).getContentString(pool));
        }
        sb.append(";");

        final String str = sb.toString();
        super.setString(str);

        if(idxAccess != 0) {
            super.attributes.addAttribute(TextAttribute.FOREGROUND, Line.ORANGE_COLOR, 0, idxAccess);
        }
        super.attributes.addAttribute(TextAttribute.FOREGROUND, FieldLine.NAME_COLOR, idxType, idxName);
        if(idxValue != -1){
            super.attributes.addAttribute(TextAttribute.FOREGROUND, FieldLine.CONSTANT_COLOR, idxValue, sb.length() - 1);
        }
        Line.colorDefault(str, super.attributes, idxAccess, idxType);
        Line.colorSymbols(str, super.attributes, str.length() - 1, str.length());
    }
}
