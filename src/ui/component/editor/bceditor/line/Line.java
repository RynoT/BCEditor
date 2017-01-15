package ui.component.editor.bceditor.line;

import project.filetype.classtype.Descriptor;
import project.filetype.classtype.constantpool.ConstantPool;
import ui.component.editor.bceditor.IBCEditor;
import ui.component.editor.bceditor.IBCTextEditor;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.*;
import java.util.List;

/**
 * Created by Ryan Thomson on 30/12/2016.
 */
public abstract class Line {

    public static final int INDENT_PIXEL_OFFSET = 28;

    public static final Color ORANGE_COLOR = new Color(204, 120, 50);
    public static final Color GENERIC_COLOR = new Color(80, 120, 120);

    static final Map<String, Color> KEYWORD_COLOR_MAP = new HashMap<>(40);
    static final Map<String, Color> SYMBOL_COLOR_MAP = new HashMap<>(2);

    private String string;
    protected AttributedString attributes;
    protected int indent = 0;

    private boolean expandHover = false;
    private final boolean expandable;
    protected final List<Line> children;

    Line(final int indent) {
        this(indent, false);
    }

    Line(final int indent, final boolean expandable) {
        this("", indent, expandable);
    }

    Line(final String string, final int indent) {
        this(string, indent, false);
    }

    Line(final String string, final int indent, final boolean expandable) {
        assert (string != null);

        this.setString(string);
        this.indent = indent;
        this.expandable = expandable;

        this.children = expandable ? new ArrayList<>() : null;
    }

    public void onActivate(final IBCTextEditor textEditor, final int caretIndex) { }

    public abstract void update(final ConstantPool pool);

    public int getIndent() {
        return this.indent;
    }

    public String getString() {
        assert this.string != null;
        return this.string;
    }

    public boolean isExpanded() {
        return this.children.size() > 0;
    }

    public boolean isExpandable() {
        return this.expandable;
    }

    public boolean isExpandHovered() {
        return this.expandHover;
    }

    public void setExpandHover(final boolean hover) {
        this.expandHover = hover;
    }

    public int getChildCount() {
        return this.expandable ? this.children.size() : 0;
    }

    public int getWidth(final FontMetrics metrics) {
        return metrics.stringWidth(this.string) + this.indent * Line.INDENT_PIXEL_OFFSET;
    }

    public void setString(final String string) {
        this.setString(string, new AttributedString(string));
    }

    protected void setString(final String string, final AttributedString attributes) {
        assert string != null && attributes != null;
        this.string = string;
        this.attributes = attributes;
        if(string.length() > 0) {
            this.attributes.addAttribute(TextAttribute.FONT, IBCEditor.EDITOR_FONT);
        }
    }

    public void expandChildren(final IBCTextEditor textEditor, final int index) {
        assert !this.expandable; //should be overwritten if expandable
    }

    public final void collapseChildren(final IBCTextEditor textEditor) {
        if(this.children == null) {
            return;
        }
        textEditor.removeLines(this.children);
        this.children.clear();
    }

    public final void render(final Graphics2D g2d, final int x, final int y) {
        assert this.string != null;
        g2d.setColor(IBCTextEditor.DEFAULT_FOREGROUND);
        g2d.drawString(this.attributes.getIterator(), x + this.indent * Line.INDENT_PIXEL_OFFSET, y);
    }

    static int getStringOffset(final String string, final int begin) {
        assert string.charAt(begin) == '\'' : "Begin index must be at the start of string declaration (')";
        boolean done = false;
        for(int i = begin + 1; i < string.length(); i++) {
            final char c = string.charAt(i);
            if(c == '\'') {
                done = true;
            } else if(done) {
                if(c == ')') {
                    return i - begin - 1;
                }
                done = false;
            }
        }
        return -1;
    }

    static void colorSymbols(final String string, final AttributedString attributes, final int begin, final int end) {
        for(int i = begin; i < end; i++) {
            final String c = String.valueOf(string.charAt(i));
            if(Line.SYMBOL_COLOR_MAP.containsKey(c)) {
                attributes.addAttribute(TextAttribute.FOREGROUND, Line.SYMBOL_COLOR_MAP.get(c), i, i + 1);
            }
        }
    }

    static void colorDefault(final String string, final AttributedString attributes, final int begin, final int end) {
        int lastIndex = begin;
        for(int i = begin; i < end; i++) {
            final char c = string.charAt(i);
            final boolean isEnd = i == end - 1;
            if(!isEnd && (c != ' ' && c != '[')) {
                continue;
            }
            int index = i;
            String substr = string.substring(lastIndex, index);
            if(substr.endsWith(".")) { //accommodate for varargs
                for(int k = substr.length() - 1; k >= 0; k--) {
                    if(substr.charAt(k) == '.') {
                        continue;
                    }
                    index -= substr.length() - k - 1;
                    substr = substr.substring(0, k + 1);
                    break;
                }
            }
            if(Line.KEYWORD_COLOR_MAP.containsKey(substr)) {
                attributes.addAttribute(TextAttribute.FOREGROUND, Line.KEYWORD_COLOR_MAP.get(substr), lastIndex, index);
            }
            lastIndex = i + 1;
        }
    }

    static void colorParameters(final String string, final AttributedString attributes, final Set<String> genericNames, final int begin, final int end) {
        int idx = -1;
        for(int i = begin; i < end; i++) {
            final char c = string.charAt(i);
            if(c == '(') {
                continue;
            }
            if(c == ',' || c == ')') {
                if(idx != -1) {
                    if(genericNames != null) {
                        final String substr = string.substring(idx, i);
                        if(genericNames.contains(substr)) {
                            attributes.addAttribute(TextAttribute.FOREGROUND, Line.GENERIC_COLOR, idx, i);
                        }
                    }
                    Line.colorDefault(string, attributes, idx, i + 1);
                    idx = -1;
                }
                if(c == ',') {
                    attributes.addAttribute(TextAttribute.FOREGROUND, Line.ORANGE_COLOR, i, i + 1);
                }
            } else if(idx == -1) {
                if(c == ' ') {
                    continue;
                }
                idx = i;
            } else if(c == '<') {
                final int start = i;
                i = Descriptor.getBracketOffset(string, '>', i, end);
                if(genericNames != null) {
                    Line.colorGenerics(string, attributes, genericNames, start, i + 1);
                }
                idx = -1;
            }
        }
    }

    static void decodeGenericNames(final String string, final Set<String> names, final int begin, final int end) {
        int idx = -1;
        boolean complete = false;
        for(int i = begin, depth = 0; i < end; i++) {
            final char c = string.charAt(i);
            if(c == '<') {
                depth++;
                complete = false;
                continue;
            }
            if(c == '>') {
                depth--;
            }
            if(idx != -1 && (c == ' ' || c == ',' || c == '>')) {
                names.add(string.substring(idx, i));

                idx = -1;
                complete = true;
            } else if(!complete && depth > 0 && idx == -1 && c != ' ') {
                idx = i;
            } else if(c == ',') {
                complete = false;
            }
        }
    }

    static void colorGenerics(final String string, final AttributedString attributes, final Set<String> names, final int begin, final int end) {
        assert names != null;
        if(names.size() == 0) {
            return;
        }
        int idx = -1;
        for(int i = begin; i < end; i++) {
            final char c = string.charAt(i);
            if(c == '<') {
                idx = -1;
                continue;
            }
            if(c == ',') {
                attributes.addAttribute(TextAttribute.FOREGROUND, Line.ORANGE_COLOR, i, i + 1);
            }
            if(idx == -1) {
                if(c == ' ') {
                    continue;
                }
                idx = i;
            } else if(c == ' ' || c == '>' || c == ',') {
                final String substr = string.substring(idx, i);
                if(names.contains(substr)) {
                    attributes.addAttribute(TextAttribute.FOREGROUND, Line.GENERIC_COLOR, idx, i);
                } else if(substr.equals("extends") || substr.equals("super")) {
                    attributes.addAttribute(TextAttribute.FOREGROUND, Line.ORANGE_COLOR, idx, i);
                }
                idx = -1;
            }
        }
    }

    static {
        for(final String next : new String[]{
                "public", "private", "protected",
                "final", "static", "volatile", "transient", "synchronized",
                "class", "enum", "abstract", "interface",
                "void", "synthetic", "new", "this", "super", "throws", "extends", "implements",
                "boolean", "byte", "char", "short", "int", "float", "long", "double", "return"
        }) {
            Line.KEYWORD_COLOR_MAP.put(next, Line.ORANGE_COLOR);
        }
        Line.SYMBOL_COLOR_MAP.put(",", Line.ORANGE_COLOR);
        Line.SYMBOL_COLOR_MAP.put(";", Line.ORANGE_COLOR);
    }
}
