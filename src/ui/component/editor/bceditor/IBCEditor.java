package ui.component.editor.bceditor;

import project.filetype.ClassType;
import ui.Canvas;
import ui.component.*;
import ui.component.editor.IEditor;
import ui.component.editor.bceditor.subeditor.IBCAttributeEditor;
import ui.component.editor.bceditor.subeditor.IBCPoolSubEditor;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by Ryan Thomson on 27/12/2016.
 */
public class IBCEditor extends IEditor {

    public static final Font EDITOR_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    public static final int SUB_EDITOR_DEFAULT_SIZE = 475;

    private final ClassType classType;

    private final IBCTextEditor textEditor;
    private final IBCPoolSubEditor poolEditor;
    private final IBCAttributeEditor attributeEditor;

    private final IToolbar toolbar;

    public IBCEditor(final ClassType classType) {
        this.classType = classType;

        super.setLayout(new BorderLayout(0, 0));
        super.setBackground(IComponent.DEFAULT_BACKGROUND_DARK);

        this.textEditor = new IBCTextEditor(classType);
        {
            this.textEditor.populate();
        }
        super.add(this.textEditor, BorderLayout.CENTER);

        final IToolbar toolbar = new IToolbar(IOrientation.EAST);
        {
            toolbar.setContentSize(IBCEditor.SUB_EDITOR_DEFAULT_SIZE);
            toolbar.getButtonPanel().setBorder(new IBorder(2, 0, 1, 2));

            final IButton poolButton = new IButton("Constant Pool").setToggle(true);
            this.poolEditor = new IBCPoolSubEditor(classType.getConstantPool());
            poolButton.addEvent(this.poolEditor::populate);
            toolbar.addTab(new ITab(poolButton, this.poolEditor, true), true);
            poolButton.click();

            final IButton attributeButton = new IButton("Attributes").setToggle(true);
            this.attributeEditor = new IBCAttributeEditor();
            attributeButton.addEvent(this.attributeEditor::populate);
            toolbar.addTab(new ITab(attributeButton, this.attributeEditor, false), false);
            //attributeButton.click();

            toolbar.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(final ComponentEvent e) {
                    Canvas.getFileViewer().repaint();
                }
            });
        }
        this.toolbar = toolbar;
        super.add(toolbar, toolbar.getOrientation().getBorder());
    }

    public IToolbar getToolbar() {
        return this.toolbar;
    }

    public IBCPoolSubEditor getPoolEditor() {
        return this.poolEditor;
    }

    public static String formatBranch(final int pc) {
        return "#" + pc;
    }

    public static String formatIndexPool(final int index) {
        return "<" + index + ">";
    }

    public static String formatIndexLocal(final int index) {
        return String.valueOf(index);
    }
}
