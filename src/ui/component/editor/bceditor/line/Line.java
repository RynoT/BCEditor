package ui.component.editor.bceditor.line;

import ui.component.editor.bceditor.IBCEditor;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ryan Thomson on 30/12/2016.
 */
public abstract class Line {

    public static final int INDENT_PIXEL_OFFSET = 28;

    static final Map<String, Color> KEYWORD_COLOR_MAP = new HashMap<>(40);
    static final Map<String, Color> SYMBOL_COLOR_MAP = new HashMap<>(2);

    private String string;
    protected AttributedString attributes;
    protected int indent = 0;

    Line(final int indent){
        this("", indent);
    }

    Line(final String string, final int indent){
        assert(string != null);

        this.setString(string);
        this.indent = indent;
    }

    public abstract void stylize();

    public String getString(){
        assert(this.string != null);
        return this.string;
    }

    public void setString(final String string){
        assert(string != null);
        this.string = string;
        this.attributes = new AttributedString(string);
        if(string.length() > 0) {
            this.attributes.addAttribute(TextAttribute.FONT, IBCEditor.EDITOR_FONT);
        }
    }

    public final void render(final Graphics2D g2d, final int x, final int y){
        assert (this.string != null);
        g2d.drawString(this.attributes.getIterator(), x + this.indent * Line.INDENT_PIXEL_OFFSET, y);
    }

    static void stylize(final String string, final AttributedString attributes){
        int lastIndex = 0;
        for(int i = 0; i < string.length(); i++){
            final char c = string.charAt(i);
            final boolean end = i == string.length() - 1;
            if(Line.SYMBOL_COLOR_MAP.containsKey(String.valueOf(c))){
                attributes.addAttribute(TextAttribute.FOREGROUND, Line.SYMBOL_COLOR_MAP.get(String.valueOf(c)), i, i + 1);
            }
            if(!end && (c != ' ')){
                continue;
            }
            final String substr = string.substring(lastIndex, end ? i + 1 : i);
            if(Line.KEYWORD_COLOR_MAP.containsKey(substr)){
                attributes.addAttribute(TextAttribute.FOREGROUND, Line.KEYWORD_COLOR_MAP.get(substr), lastIndex, i + 1);
            }
            lastIndex = i + 1;
        }
    }

    static {
        final Color orange = new Color(204, 120, 50);
        for(final String next : new String[]{
                "public", "private", "protected",
                "final", "static", "volatile", "transient", "synchronized",
                "class", "enum", "abstract", "interface",
                "void", "synthetic", "new", "this", "super", "throws", "extends", "implements",
                "boolean", "byte", "char", "short", "int", "float", "long", "double", "return"
        }){
            Line.KEYWORD_COLOR_MAP.put(next, orange);
        }
        Line.SYMBOL_COLOR_MAP.put(",", orange);
        Line.SYMBOL_COLOR_MAP.put(";", orange);
    }
}
