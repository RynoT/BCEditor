package ui.component.bceditor.text;

import project.filetype.classtype.ClassFormat;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.MethodInfo;

/**
 * Created by Ryan Thomson on 30/12/2016.
 */
public class MethodLine extends Line {

    private final MethodInfo method;
    private final ConstantPool pool;

    public MethodLine(final MethodInfo method, final ConstantPool pool, final int indent) {
        super(indent);

        this.method = method;
        this.pool = pool;
        super.setString(ClassFormat.format(method, pool));
    }

    @Override
    public void stylize() {
        Line.stylize(super.getString(), super.attributes);
    }
}
