package ui.component.editor.bceditor.line;

import project.filetype.classtype.ClassFormat;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.FieldInfo;

import java.awt.*;
import java.awt.font.TextAttribute;

/**
 * Created by Ryan Thomson on 30/12/2016.
 */
public class FieldLine extends Line {

    private static final Color NAME_COLOR = new Color(150, 120, 170);

    private final FieldInfo field;
    private final ConstantPool pool;

    public FieldLine(final FieldInfo field, final ConstantPool pool, final int indent) {
        super(indent);

        this.field = field;
        this.pool = pool;
        super.setString(ClassFormat.format(field, pool));
    }

    @Override
    public void stylize() {
        final String string = super.getString();
        Line.stylize(string, super.attributes);

        final int index = string.lastIndexOf(' ') + 1;
        super.attributes.addAttribute(TextAttribute.FOREGROUND, FieldLine.NAME_COLOR, index, string.length() - 1);
    }
}
