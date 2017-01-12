package ui.component.editor.bceditor.line;

import project.filetype.classtype.constantpool.ConstantPool;
import project.property.Property;
import ui.component.editor.bceditor.IBCTextEditor;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.List;

/**
 * Created by Ryan Thomson on 03/01/2017.
 */
public class PropertyLine extends Line {

    private final Line parent;
    private final Property property;

    public PropertyLine(final Property property, final Line parent) {
        super(parent.getIndent());

        this.parent = parent;
        this.property = property;
    }

    public Line getParent() {
        return this.parent;
    }

    public Property getProperty() {
        return this.property;
    }

    @Override
    public void addChildren(final List<Line> lines, final int index) {
        assert false;
    }

    @Override
    public void update(final ConstantPool pool) {
        final String string = this.property.getContentString(pool);
        super.setString(string);

        super.attributes.addAttribute(TextAttribute.FOREGROUND, IBCTextEditor.ATTRIBUTE_COLOR);

        //id: -1 = undefined, 0 = property name, 1 = inner name, 2 = constant, 3 = string
        for(int i = 0, start = -1, id = -1, inner = 0; i < string.length(); i++) {
            final char c = string.charAt(i);
            boolean trigger = false;
            switch(c) {
                case ' ':
                    continue;
                case '.':
                    id = 0;
                    start = i;
                    continue;
                case '[':
                    start = -1;
                    continue;
                case ')':
                    inner--;
                    break;
                case '(':
                    inner++;
                case ',':
                case ']':
                    trigger = true;
                    break;
                case '\'':
                    id = 3;
                    start = i;

                    boolean done = false;
                    for(++i; i < string.length(); i++){
                        final char c2 = string.charAt(i);
                        if(c2 == '\''){
                            done = true;
                        } else if(done){
                            if(c2 == ')'){
                                break;
                            }
                            done = false;
                        }
                    }
                    trigger = true;
                    break;
            }
            if(trigger) {
                if(start == -1){
                    continue;
                }
                final Color color;
                switch(id) {
                    case 0: //property name
                        assert false; //we don't do anything with the name so this should never be called
                    case 1:
                        color = IBCTextEditor.INDEX_LOCAL_COLOR;
                        break;
                    case 2:
                        color = IBCTextEditor.CONSTANT_COLOR;
                        break;
                    case 3:
                        color = IBCTextEditor.STRING_COLOR;
                        break;
                    default:
                        color = null;
                        assert false;
                }
                super.attributes.addAttribute(TextAttribute.FOREGROUND, color, start, i);

                start = -1;
            } else if(start == -1) {
                if(Character.isDigit(c)) {
                    id = 2;
                } else {
                    id = inner == 0 ? 1 : 3;
                }
                start = i;
            }
        }
    }
}
