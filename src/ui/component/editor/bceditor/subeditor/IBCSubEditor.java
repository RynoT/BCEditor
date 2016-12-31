package ui.component.editor.bceditor.subeditor;

import ui.component.editor.IEditor;

import java.awt.*;

/**
 * Created by Ryan Thomson on 31/12/2016.
 */
public abstract class IBCSubEditor extends IEditor {

    public static final int TITLE_HEIGHT = 50;
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Color TITLE_TEXT_COLOR = new Color(130, 50, 0);

    public abstract Component getTitlePanel();
}
