package ui.component.editor.bceditor.subeditor;

import ui.component.IBorder;
import ui.component.IComponent;
import ui.component.ILabel;
import ui.component.editor.IEditor;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ryan Thomson on 31/12/2016.
 */
public abstract class IBCSubEditor extends IEditor {

    public static final int TITLE_HEIGHT = 50;
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Color TITLE_TEXT_COLOR = new Color(130, 50, 0);

    protected IBCSubEditor(final String title){
        super.setFont(IComponent.DEFAULT_FONT);
        super.setLayout(new BorderLayout(0, 0));
        super.setBackground(IComponent.DEFAULT_BACKGROUND_INTERMEDIATE);

        final JPanel titlePanel = new JPanel();
        {
            titlePanel.setLayout(new BorderLayout(0, 0));
            titlePanel.setBackground(IComponent.DEFAULT_BACKGROUND_HIGHLIGHT);
            titlePanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, IBCSubEditor.TITLE_HEIGHT));
            titlePanel.setBorder(new IBorder(0, 1, 1, 0));

            final ILabel label = new ILabel(title);
            label.setFont(IBCSubEditor.TITLE_FONT);
            label.setColor(IBCSubEditor.TITLE_TEXT_COLOR);
            titlePanel.add(label, BorderLayout.CENTER);
        }
        super.add(titlePanel, BorderLayout.NORTH);
    }

    public abstract void populate();

}
