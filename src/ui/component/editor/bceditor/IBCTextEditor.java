package ui.component.editor.bceditor;

import project.filetype.ClassType;
import project.filetype.classtype.ClassFormat;
import project.filetype.classtype.member.FieldInfo;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._Code;
import project.filetype.classtype.opcode.Instruction;
import ui.component.IComponent;
import ui.component.IScrollPanel;
import ui.component.editor.IEditor;
import ui.component.editor.bceditor.line.*;
import util.AssetManager;
import util.async.Async;
import util.async.AsyncEvent;
import util.async.AsyncType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan Thomson on 29/12/2016.
 */
public class IBCTextEditor extends IEditor {

    public static final int LINE_HEIGHT = 16;
    public static final int LINE_DEFAULT_INSET = 5;
    public static final int CARET_WIDTH = 2;
    public static final int SIDE_BAR_TEXT_PADDING = 6;
    public static final int HORIZONTAL_SCROLL_OFFSET = 10;

    public static final Color LINE_ACTIVE_COLOR = new Color(55, 55, 55);

    public static final Color BRANCH_COLOR = new Color(83, 130, 154);
    public static final Color INDEX_POOL_COLOR = new Color(123, 170, 184);
    public static final Color INDEX_LOCAL_COLOR = new Color(152, 118, 170);

    public static final Color FIELD_NAME_COLOR = new Color(150, 120, 170);
    public static final Color CONSTANT_COLOR = new Color(92, 160, 173);
    public static final Color STRING_COLOR = new Color(100, 135, 80);
    public static final Color PROPERTY_COLOR = new Color(180, 188, 55);

    public static final Color SIDE_BAR_FOREGROUND = new Color(135, 135, 135);
    public static final BasicStroke SIDE_BAR_STROKE = new BasicStroke(1, BasicStroke
            .CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, new float[]{ 1f, 2f }, 0.0f);

    private final ClassType type;

    private final SideBar sideBar;
    private final LineRenderer lineRenderer;
    private final IScrollPanel scrollPanel;

    private final List<Line> lines = new ArrayList<>();

    private Line active = null;

    public IBCTextEditor(final ClassType type) {
        assert type != null;
        this.type = type;

        super.setOpaque(false);
        super.setLayout(new BorderLayout(0, 0));

        super.add(this.sideBar = new SideBar(), BorderLayout.WEST);

        this.lineRenderer = new LineRenderer();
        final IScrollPanel scrollPanel = new IScrollPanel(this.lineRenderer, true, true);
        {
            scrollPanel.setOpaque(false);
            scrollPanel.getViewport().addChangeListener(e -> IBCTextEditor.this.sideBar.repaint());
        }
        this.scrollPanel = scrollPanel;
        super.add(scrollPanel, BorderLayout.CENTER);
    }

    public IBCEditor getEditor() {
        assert super.getParent() != null && super.getParent() instanceof IBCEditor;
        return (IBCEditor) super.getParent();
    }

    public void setActiveLine(final Line line) {
        this.active = line;
        this.lineRenderer.repaint();

        assert this.lineRenderer.caretPosition != -1;
        line.onActivate(this, this.lineRenderer.caretPosition);

        if(line instanceof InstructionLine) {
            final InstructionLine instruction = (InstructionLine) line;
        }
    }

    public void addLines(final List<Line> lines, final int index) {
        Async.submit(() -> {
            this.lines.addAll(index, lines);

            final FontMetrics metrics = this.lineRenderer.getFontMetrics(this.lineRenderer.getFont());
            int maxWidth = IBCTextEditor.this.lineRenderer.maxLineWidth;
            for(final Line line : lines) {
                line.update(this.type.getConstantPool());
                maxWidth = Math.max(IBCTextEditor.LINE_DEFAULT_INSET + line
                        .getWidth(metrics) + IBCTextEditor.HORIZONTAL_SCROLL_OFFSET, maxWidth);
            }
            if(maxWidth != this.lineRenderer.maxLineWidth) {
                this.lineRenderer.maxLineWidth = maxWidth;
            }
            this.lineRenderer.updateDimensions();
            this.lineRenderer.repaint();
            this.sideBar.repaint();
        }, AsyncType.MULTI);
    }

    public void removeLines(final List<Line> lines) {
        this.lines.removeAll(lines);
        this.lineRenderer.repaint();
        this.sideBar.repaint();
    }

    public void populate() {
        final List<Line> lines = new ArrayList<>();
        final ClassLine classLine = new ClassLine(this.type, 0);
        lines.add(classLine);
        lines.add(new EmptyLine());

        for(final FieldInfo field : this.type.getFields()) {
            lines.add(new FieldLine(field, 1));
        }
        if(this.type.getFieldCount() > 0) {
            lines.add(new EmptyLine());
        }
        int maxPc = 0;
        for(final MethodInfo method : this.type.getMethods()) {
            final MethodLine methodLine = new MethodLine(method, classLine, 1);
            lines.add(methodLine);

            final _Code code = (_Code) AttributeInfo.findFirst(AttributeInfo.CODE, method.getAttributes(), this.type.getConstantPool());
            if(code != null) {
                final List<Instruction> instructions = ClassFormat.format(code.getRawCode());
                for(final Instruction instruction : instructions) {
                    lines.add(new InstructionLine(instruction, methodLine, 2));
                }
                lines.add(new DefaultLine("}", 1));

                maxPc = Math.max(maxPc, instructions.get(instructions.size() - 1).getPc());
            }
            lines.add(new EmptyLine());
        }
        if(maxPc > this.sideBar.maxPc) {
            this.sideBar.maxPc = maxPc;

            final int width = this.sideBar.getFontMetrics(this.sideBar.getFont()).stringWidth(
                    String.valueOf(maxPc)) + IBCTextEditor.SIDE_BAR_TEXT_PADDING * 2;
            this.sideBar.setPreferredSize(new Dimension(width, Integer.MAX_VALUE));
        }

        lines.add(new DefaultLine("}"));
        lines.add(new EmptyLine());

        this.lines.clear();
        this.addLines(lines, 0);
    }

    private class SideBar extends JPanel {

        private int maxPc = 0;

        private SideBar() {
            super.setFont(IComponent.DEFAULT_FONT);
            super.setBackground(IComponent.DEFAULT_BACKGROUND_INTERMEDIATE);
            super.setPreferredSize(new Dimension(30, Integer.MAX_VALUE));
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);

            final Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(IBCTextEditor.SIDE_BAR_STROKE);
            g2d.setColor(IComponent.DEFAULT_HIGHLIGHT_LIGHT);
            g2d.drawLine(super.getWidth() - 1, 0, super.getWidth() - 1, super.getHeight() - 1);

            final FontMetrics metrics = g2d.getFontMetrics();
            final Point viewPosition = IBCTextEditor.this.scrollPanel.getViewport().getViewPosition();
            final List<Line> lines = IBCTextEditor.this.lines;

            final int min = viewPosition.y / IBCTextEditor.LINE_HEIGHT;
            final int max = (viewPosition.y + super.getHeight()) / IBCTextEditor.LINE_HEIGHT;

            g2d.setFont(super.getFont());
            g2d.setColor(IBCTextEditor.SIDE_BAR_FOREGROUND);
            for(int i = min; i < Math.min(lines.size(), max); i++) {
                if(!(lines.get(i) instanceof InstructionLine)) {
                    continue;
                }
                final int pc = ((InstructionLine) lines.get(i)).getInstruction().getPc();
                final int y = IBCTextEditor.LINE_HEIGHT * (i + 1) - (IBCTextEditor.LINE_HEIGHT - metrics.getHeight()) / 2;
                g2d.drawString(String.valueOf(pc), super.getWidth() / 2 - metrics.stringWidth(String.valueOf(pc)) / 2, y - viewPosition.y);
            }
        }
    }

    private class LineRenderer extends JPanel {

        private final int charWidth;
        private int maxLineWidth = 0;

        private int caretPosition = -1;

        private Line hoveredLine;
        private BufferedImage expand, expandHover, collapse;

        private LineRenderer() {
            super.setOpaque(false);
            super.setFont(IBCEditor.EDITOR_FONT);
            super.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

            this.charWidth = super.getFontMetrics(super.getFont()).charWidth(' '); //font is monospaced

            final MouseAdapter adapter = new MouseAdapter() {
                @Override
                public void mousePressed(final MouseEvent e) {
                    final Line line = this.getLine(e.getPoint());
                    if(line.isExpandable() && line == LineRenderer.this.hoveredLine && line.isExpandHovered()) {
                        if(line.isExpanded()) {
                            line.setExpandHover(false);
                            line.collapseChildren(IBCTextEditor.this);
                            LineRenderer.super.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                        } else {
                            line.expandChildren(IBCTextEditor.this, e.getY() / IBCTextEditor.LINE_HEIGHT);
                        }
                        LineRenderer.super.repaint();
                    } else {
                        final int position = (e.getX() - line.getIndent() * Line.INDENT_PIXEL_OFFSET) / LineRenderer.this.charWidth; //font is monospaced
                        LineRenderer.this.caretPosition = Math.max(0, Math.min(position, line.getString().length()));
                        IBCTextEditor.this.setActiveLine(line);
                    }
                }

                @Override
                public void mouseMoved(final MouseEvent e) {
                    final Line line = this.getLine(e.getPoint()), hover = LineRenderer.this.hoveredLine;

                    boolean repaint = false;
                    if(hover != line) {
                        if(hover != null && hover.isExpandHovered()) {
                            repaint = true;
                            hover.setExpandHover(false);
                        }
                        LineRenderer.this.hoveredLine = line;
                    }
                    if(line.isExpandable()) {
                        final boolean expand = LineRenderer.this.isMouseOverExpand(line, e.getPoint());
                        if(line.isExpandHovered() != expand) {
                            line.setExpandHover(expand);
                            repaint = true;
                        }
                    }
                    if(repaint) {
                        LineRenderer.super.repaint();
                        LineRenderer.super.setCursor(line.isExpandHovered() ? Cursor.getPredefinedCursor(Cursor
                                .DEFAULT_CURSOR) : Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                    }
                }

                private Line getLine(final Point point) {
                    return IBCTextEditor.this.lines.get(point.y / IBCTextEditor.LINE_HEIGHT);
                }
            };
            super.addMouseListener(adapter);
            super.addMouseMotionListener(adapter);
            super.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(final KeyEvent e) {
                    final Line line = IBCTextEditor.this.active;
                    if(line == null) {
                        return;
                    }
                    System.out.println(String.valueOf(e.getKeyChar()));
                    line.setString(line.getString() + String.valueOf(e.getKeyChar()));
                }
            });
            AssetManager.loadImage(AssetManager.EXPAND_SMALL_ICON, new AsyncEvent<BufferedImage>() {
                @Override
                public void onComplete(final BufferedImage item) {
                    LineRenderer.this.expand = item;
                    LineRenderer.this.repaint();
                }
            });
            AssetManager.loadImage(AssetManager.EXPAND_SMALL_HOVER_ICON, new AsyncEvent<BufferedImage>() {
                @Override
                public void onComplete(final BufferedImage item) {
                    LineRenderer.this.expandHover = item;
                }
            });
            AssetManager.loadImage(AssetManager.COLLAPSE_SMALL_ICON, new AsyncEvent<BufferedImage>() {
                @Override
                public void onComplete(final BufferedImage item) {
                    LineRenderer.this.collapse = item;
                    LineRenderer.this.repaint();
                }
            });
        }

        private void updateDimensions() {
            super.setPreferredSize(new Dimension(this.maxLineWidth,
                    IBCTextEditor.this.lines.size() * IBCTextEditor.LINE_HEIGHT));

            super.revalidate();
            super.repaint();
        }

        private boolean isMouseOverExpand(final Line line, final Point point) {
            if(!line.isExpandable() || this.expand == null) {
                return false;
            }
            if(IBCTextEditor.this.lines.get(point.y / IBCTextEditor.LINE_HEIGHT) != line) {
                return false;
            }
            final int right = IBCTextEditor.LINE_DEFAULT_INSET + line.getIndent() * Line.INDENT_PIXEL_OFFSET;
            return point.x > right - this.expand.getWidth() && point.x < right;
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);

            final Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(super.getFont());
            g2d.setColor(IComponent.DEFAULT_FOREGROUND);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final Point viewPosition = IBCTextEditor.this.scrollPanel.getViewport().getViewPosition();
            final int min = viewPosition.y / IBCTextEditor.LINE_HEIGHT;
            final int max = (viewPosition.y + super.getHeight()) / IBCTextEditor.LINE_HEIGHT;

            final int textHeight = g.getFontMetrics().getHeight() / 2;
            final List<Line> lines = IBCTextEditor.this.lines;
            for(int i = min; i < Math.min(lines.size(), max); i++) {
                final Line line = lines.get(i);
                if(IBCTextEditor.this.active == line) {
                    g2d.setColor(IBCTextEditor.LINE_ACTIVE_COLOR);
                    g2d.fillRect(0, i * IBCTextEditor.LINE_HEIGHT, super.getWidth(), IBCTextEditor.LINE_HEIGHT);
                    g2d.setColor(IComponent.DEFAULT_FOREGROUND);
                    if(this.caretPosition != -1) {
                        final int xIndentOffset = line.getIndent() * Line.INDENT_PIXEL_OFFSET;
                        g2d.fillRect(xIndentOffset + this.caretPosition * this.charWidth + this.charWidth - IBCTextEditor.CARET_WIDTH / 2,
                                i * IBCTextEditor.LINE_HEIGHT, IBCTextEditor.CARET_WIDTH, IBCTextEditor.LINE_HEIGHT);
                    }
                }
                if(line.isExpandable()) {
                    final BufferedImage image = line.isExpanded() ? this.collapse : (line.isExpandHovered() ? this.expandHover : this.expand);
                    if(image != null) {
                        int x = IBCTextEditor.LINE_DEFAULT_INSET + line.getIndent() * Line.INDENT_PIXEL_OFFSET - image.getWidth();
                        final int y = i * IBCTextEditor.LINE_HEIGHT;
                        g2d.drawImage(image, x, y, null);

                        if(line.isExpanded()) {
                            x += image.getWidth() / 2;
                            g2d.setColor(IBCTextEditor.PROPERTY_COLOR);
                            g2d.drawLine(x, y, x, y - line.getChildCount() * IBCTextEditor.LINE_HEIGHT + IBCTextEditor.LINE_HEIGHT / 2);
                            for(int c = 0; c < line.getChildCount(); c++) {
                                final int j = y - c * IBCTextEditor.LINE_HEIGHT - IBCTextEditor.LINE_HEIGHT / 2;
                                g2d.drawLine(x, j, x + image.getWidth() / 4, j);
                            }
                        }
                    }
                }
                line.render(g2d, IBCTextEditor.LINE_DEFAULT_INSET, IBCTextEditor.LINE_HEIGHT
                        * (i + 1) - (IBCTextEditor.LINE_HEIGHT - textHeight) / 2);
                //g2d.fillRect(0, IBCTextEditor.LINE_HEIGHT * (i + 1) - (IBCTextEditor.LINE_HEIGHT - textHeight) / 2, 100, 2);
            }
        }
    }

}
