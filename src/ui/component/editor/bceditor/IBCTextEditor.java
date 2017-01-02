package ui.component.editor.bceditor;

import project.filetype.ClassType;
import project.filetype.classtype.ClassFormat;
import project.filetype.classtype.member.FieldInfo;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._Code;
import project.filetype.classtype.opcode.Instruction;
import ui.component.IComponent;
import ui.component.editor.IEditor;
import ui.component.IScrollPanel;
import ui.component.editor.bceditor.line.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan Thomson on 29/12/2016.
 */
public class IBCTextEditor extends IEditor {

    public static final int LINE_HEIGHT = 16;
    public static final int LINE_DEFAULT_INSET = 5;

    public static final int SIDE_BAR_TEXT_PADDING = 6;

    public static final Color SIDE_BAR_FOREGROUND = new Color(135, 135, 135);
    public static final BasicStroke SIDE_BAR_STROKE = new BasicStroke(1, BasicStroke
            .CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, new float[]{1f, 2f}, 0.0f);

    private final SideBar sideBar;
    private final LineRenderer lineRenderer;
    private final IScrollPanel scrollPanel;

    private final List<Line> lines = new ArrayList<>();

    public IBCTextEditor(){
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

    public void populate(final ClassType type){
        this.lines.clear();

        //this.lines.add(new DefaultLine(ClassFormat.format(type)));
        this.lines.add(new ClassLine(type, 0));
        this.lines.add(new EmptyLine());

        for(final FieldInfo field : type.getFields()){
            this.lines.add(new FieldLine(field, type.getConstantPool(), 1));
        }
        if(type.getFields().length > 0) {
            this.lines.add(new EmptyLine());
        }
        int maxPc = 0;
        for(final MethodInfo method : type.getMethods()){
            final MethodLine methodLine = new MethodLine(method, type.getConstantPool(), 1);
            this.lines.add(methodLine);

            final _Code code = (_Code) AttributeInfo.findFirst(AttributeInfo.CODE, method.getAttributes(), type.getConstantPool());
            if(code != null) {
                final List<Instruction> instructions = ClassFormat.format(code.getRawCode());
                for(final Instruction instruction : instructions){
                    this.lines.add(new InstructionLine(instruction, methodLine, type.getConstantPool(), 2));
                }
                this.lines.add(new DefaultLine("}", 1));

                maxPc = Math.max(maxPc, instructions.get(instructions.size() - 1).getPc());
            }
            this.lines.add(new EmptyLine());
        }
        if(maxPc > this.sideBar.maxPc){
            this.sideBar.maxPc = maxPc;

            final int width = this.sideBar.getFontMetrics(this.sideBar.getFont()).stringWidth(
                    String.valueOf(maxPc)) + IBCTextEditor.SIDE_BAR_TEXT_PADDING * 2;
            this.sideBar.setPreferredSize(new Dimension(width, Integer.MAX_VALUE));
        }

        this.lines.add(new DefaultLine("}"));
        this.lines.add(new EmptyLine());

        for(final Line line : this.lines) {
            line.update();
        }
        this.lineRenderer.updateDimensions();
        this.lineRenderer.repaint();
    }

    private class SideBar extends JPanel {

        private int maxPc = 0;

        private SideBar(){
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
            for(int i = min; i < Math.min(lines.size(), max); i++){
                if(!(lines.get(i) instanceof InstructionLine)){
                    continue;
                }
                final int pc = ((InstructionLine) lines.get(i)).getInstruction().getPc();
                final int y = IBCTextEditor.LINE_HEIGHT * (i + 1) - (IBCTextEditor.LINE_HEIGHT - metrics.getHeight()) / 2;
                g2d.drawString(String.valueOf(pc), super.getWidth() / 2 - metrics.stringWidth(String.valueOf(pc)) / 2, y - viewPosition.y);
            }
        }
    }

    private class LineRenderer extends JPanel {

        private LineRenderer(){
            super.setOpaque(false);
            super.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        }

        private void updateDimensions(){
            super.setPreferredSize(new Dimension(2000,
                    IBCTextEditor.this.lines.size() * IBCTextEditor.LINE_HEIGHT));

            super.revalidate();
            super.repaint();
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);

            final Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(IBCEditor.EDITOR_FONT);
            g2d.setColor(IComponent.DEFAULT_FOREGROUND);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final Point viewPosition = IBCTextEditor.this.scrollPanel.getViewport().getViewPosition();
            final int min = viewPosition.y / IBCTextEditor.LINE_HEIGHT;
            final int max = (viewPosition.y + super.getHeight()) / IBCTextEditor.LINE_HEIGHT;

            final int textHeight = g.getFontMetrics().getHeight() / 2;
            final List<Line> lines = IBCTextEditor.this.lines;
            for(int i = min; i < Math.min(lines.size(), max); i++){
                lines.get(i).render(g2d, IBCTextEditor.LINE_DEFAULT_INSET,
                        IBCTextEditor.LINE_HEIGHT * (i + 1) - (IBCTextEditor.LINE_HEIGHT - textHeight) / 2);
                //g2d.fillRect(0, IBCTextEditor.LINE_HEIGHT * (i + 1) - (IBCTextEditor.LINE_HEIGHT - textHeight) / 2, 100, 2);
            }
        }
    }

}
