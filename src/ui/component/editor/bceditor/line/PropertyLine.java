package ui.component.editor.bceditor.line;

import project.filetype.classtype.constantpool.ConstantPool;
import project.property.Property;

/**
 * Created by Ryan Thomson on 03/01/2017.
 */
public class PropertyLine extends Line {

    private final Property property;
    private final Line parent;

    public PropertyLine(final Property property, final Line parent) {
        super(parent.getIndent());

        this.property = property;
        this.parent = parent;
    }

    public Line getParent() {
        return this.parent;
    }

    public Property getProperty(){
        return this.property;
    }

    @Override
    public void update(final ConstantPool pool) {
//        final AttributedString attributes;
//
//        final StringBuilder sb = new StringBuilder();
//        sb.append(".").append(this.attribute.getTagName(this.pool).getValue())
//                .append(IBCEditor.formatIndexPool(this.attribute.getNameIndex()));
//
//        sb.append("[");
//        {
//            final Pair<String, AttributedString> content = this.attribute.getContentString(this.pool);
//            sb.append(content.getLeft());
//            attributes = content.getRight();
//        }
//        sb.append("]");
//
//        super.setString(sb.toString(), attributes);
    }
}
