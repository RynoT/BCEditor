package ui.component.editor.bceditor.line;

import project.filetype.classtype.constantpool.ConstantPool;

/**
 * Created by Ryan Thomson on 30/12/2016.
 */
public class DefaultLine extends Line {

    public DefaultLine(final String string) {
        this(string, 0);
    }

    public DefaultLine(final String string, final int indent) {
        super(string, indent);
    }

    @Override
    public void update(final ConstantPool pool) {
        Line.colorDefault(super.getString(), super.attributes, 0, super.getString().length());
    }
}
