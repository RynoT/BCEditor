package ui.component.editor.bceditor.line;

import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.Descriptor;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._Exceptions;
import project.filetype.classtype.member.attributes._Signature;
import project.property.Property;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.List;

/**
 * Created by Ryan Thomson on 30/12/2016.
 */
public class MethodLine extends Line {

    public static final Color NAME_COLOR = new Color(255, 200, 100);

    private final MethodInfo method;
    private final ClassLine classLine;

    private Set<String> genericNames = null;

    public MethodLine(final MethodInfo method, final ClassLine classLine, final int indent) {
        super(indent);

        assert method != null && classLine != null;
        this.method = method;
        this.classLine = classLine;
    }

    public Set<String> getGenericNames(){
        return this.genericNames;
    }

    @Override
    protected void addChildren(final List<Line> lines, final int index) {
        assert super.children == null : "Remove children before adding";

        super.children = new ArrayList<>();
        final List<Property> properties = this.method.getProperties();
        for(final Property property : properties){
            super.children.add(new PropertyLine(property, this));
        }
        lines.addAll(index, super.children);
    }

    @Override
    public void update(final ConstantPool pool) {
        int idxAccess, idxGeneric = -1, idxType, idxName, idxParameters, idxThrows = -1;

        assert this.classLine != null && this.classLine.getGenericNames() != null;
        this.genericNames = new HashSet<>();
        if(this.classLine.getGenericNames().size() > 0){
            //inherit all generic types from class
            this.genericNames.addAll(this.classLine.getGenericNames());
        }

        final StringBuilder sb = new StringBuilder();
        if(this.method.getAccessFlags() != 0) {
            sb.append(this.method.getAccessFlagsString()).append(" ");
        }
        idxAccess = sb.length();

        // Decode the descriptor from either the method or signature attribute (if present)
        String descriptor;
        final _Signature signatureAttribute = (_Signature) AttributeInfo.findFirst(AttributeInfo.SIGNATURE, this.method.getAttributes(), pool);
        if(signatureAttribute != null) {
            descriptor = Descriptor.decode(signatureAttribute.getTagSignature(pool).getValue());
        } else {
            descriptor = Descriptor.decode(this.method.getTagDescriptor(pool).getValue());
        }
        descriptor = Descriptor.hideObjectClass(descriptor);

        assert descriptor.indexOf('(') != -1 && descriptor.indexOf(')') != -1 : "Descriptor isn't for method?";
        if(descriptor.charAt(0) == '<') {
            final String descSubstring = descriptor.substring(0, descriptor.indexOf('('));
            sb.append(descSubstring).append(" ");
            idxGeneric = sb.length();

            Line.decodeGenericNames(descSubstring, this.genericNames, 0, descSubstring.length());
        }
        sb.append(descriptor.substring(descriptor.indexOf(')') + 1)).append(" ");
        idxType = sb.length();
        sb.append(this.method.getTagName(pool).getValue());
        idxName = sb.length();

        if(AccessFlags.containsFlag(this.method.getAccessFlags(), AccessFlags.ACC_VARARGS)) {
            descriptor = descriptor.replaceFirst("\\[]\\)", "...)");
        }
        sb.append(descriptor.substring(descriptor.indexOf('('), descriptor.indexOf(')') + 1));
        idxParameters = sb.length();

        final _Exceptions exceptionsAttribute = (_Exceptions) AttributeInfo.findFirst(AttributeInfo.EXCEPTIONS, this.method.getAttributes(), pool);
        if(exceptionsAttribute != null) {
            sb.append(" throws ");
            idxThrows = sb.length();
            for(int i = 0; i < exceptionsAttribute.getExceptionCount(); i++) {
                sb.append(exceptionsAttribute.getTagException(pool, i).getContentString(pool));
                if(i < exceptionsAttribute.getExceptionCount() - 1) {
                    sb.append(", ");
                }
            }
        }
        if(AccessFlags.containsFlag(this.method.getAccessFlags(), AccessFlags.ACC_ABSTRACT)) {
            sb.append(";");
        } else {
            sb.append(" {");
        }

        final String str = sb.toString();
        super.setString(str);

        if(idxAccess != 0) {
            super.attributes.addAttribute(TextAttribute.FOREGROUND, Line.ORANGE_COLOR, 0, idxAccess); //access flags
        }
        if(idxGeneric != -1) {
            Line.colorGenerics(str, super.attributes, genericNames, idxAccess, idxGeneric); //generics
        }

        Line.colorDefault(str, super.attributes, idxGeneric == -1 ? idxAccess : idxGeneric, idxType); //return type
        if(this.genericNames != null) {
            Line.colorGenerics(str, super.attributes, this.genericNames, idxGeneric == -1 ? idxAccess : idxGeneric, idxType); //return type
        }

        super.attributes.addAttribute(TextAttribute.FOREGROUND, MethodLine.NAME_COLOR, idxType, idxName); //name

        Line.colorParameters(str, super.attributes, genericNames, idxName, idxParameters); //parameters

        if(idxThrows != -1) {
            super.attributes.addAttribute(TextAttribute.FOREGROUND, Line.ORANGE_COLOR, idxParameters, idxThrows); //throws

            Line.colorSymbols(str, super.attributes, idxThrows, str.length());
        }
    }
}
