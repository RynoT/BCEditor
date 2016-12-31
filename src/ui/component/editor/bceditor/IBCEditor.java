package ui.component.editor.bceditor;

import project.filetype.ClassType;
import ui.Canvas;
import ui.component.*;
import ui.component.editor.IEditor;
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

    private final IToolbar toolbar;

    public IBCEditor(final ClassType classType) {
        this.classType = classType;

        super.setBackground(IComponent.DEFAULT_BACKGROUND_DARK);

        super.setLayout(new BorderLayout(0, 0));

        this.textEditor = new IBCTextEditor();
        {
            this.textEditor.populate(classType);
        }
        super.add(this.textEditor, BorderLayout.CENTER);

        final IToolbar toolbar = new IToolbar(IOrientation.EAST);
        {
            toolbar.setContentSize(IBCEditor.SUB_EDITOR_DEFAULT_SIZE);
            toolbar.getButtonPanel().setBorder(new IBorder(2, 0, 1, 2));

            this.poolEditor = new IBCPoolSubEditor();
            {
                this.poolEditor.populate(classType.getConstantPool());
            }

            final IButton poolButton = new IButton("Constant Pool").setToggle(true);
            toolbar.addTab(new ITab(poolButton, this.poolEditor, true), true);
            poolButton.click();

            toolbar.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(final ComponentEvent e) {
                    final IComponent content = toolbar.getUpperContent();
                    if(content != null){
                        Canvas.getFileViewer().repaint();
                    }
                }
            });
        }
        this.toolbar = toolbar;
        super.add(toolbar, toolbar.getOrientation().getBorder());
    }

    public IToolbar getToolbar(){
        return this.toolbar;
    }
}
