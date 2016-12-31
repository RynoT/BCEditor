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

    private final LineRenderer renderer;
    private final IScrollPanel scrollPanel;

    private final List<Line> lines = new ArrayList<>();

    public IBCTextEditor(){
        super.setOpaque(false);
        super.setLayout(new BorderLayout(0, 0));

        super.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

        this.renderer = new LineRenderer();
        final IScrollPanel scrollPanel = new IScrollPanel(this.renderer, true, true);
        {
            scrollPanel.setOpaque(false);
        }
        this.scrollPanel = scrollPanel;
        super.add(scrollPanel, BorderLayout.CENTER);
    }

    public void populate(final ClassType type){
        this.lines.clear();

        this.lines.add(new DefaultLine(ClassFormat.format(type)));
        this.lines.add(new EmptyLine());

        for(final FieldInfo field : type.getFields()){
            this.lines.add(new FieldLine(field, type.getConstantPool(), 1));
        }
        if(type.getFields().length > 0) {
            this.lines.add(new EmptyLine());
        }
        for(final MethodInfo method : type.getMethods()){
            final MethodLine methodLine = new MethodLine(method, type.getConstantPool(), 1);
            this.lines.add(methodLine);

            final _Code code = (_Code) AttributeInfo.findFirst(AttributeInfo.CODE, method.getAttributes(), type.getConstantPool());
            if(code != null) {
                for(final Instruction instruction : ClassFormat.format(code.getRawCode())){
                    this.lines.add(new InstructionLine(instruction, methodLine, type.getConstantPool(), 2));
                }
                this.lines.add(new DefaultLine("}", 1));
            }
            this.lines.add(new EmptyLine());
        }

        this.lines.add(new DefaultLine("}"));
        this.lines.add(new EmptyLine());

        for(final Line line : this.lines) {
            line.stylize();
        }
        this.renderer.updateDimensions();
        this.renderer.repaint();
    }

    private class LineRenderer extends JPanel {

        private LineRenderer(){
            super.setOpaque(false);
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

            final int textHeight = g.getFontMetrics().getHeight() / 2;
            final List<Line> lines = IBCTextEditor.this.lines;
            for(int i = 0; i < lines.size(); i++){
                lines.get(i).render(g2d, IBCTextEditor.LINE_DEFAULT_INSET,
                        IBCTextEditor.LINE_HEIGHT * (i + 1) - (IBCTextEditor.LINE_HEIGHT - textHeight) / 2);
                //g2d.fillRect(0, IBCTextEditor.LINE_HEIGHT * (i + 1) - (IBCTextEditor.LINE_HEIGHT - textHeight) / 2, 100, 2);
            }
        }
    }

}
