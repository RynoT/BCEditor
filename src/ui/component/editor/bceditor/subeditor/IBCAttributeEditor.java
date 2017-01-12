package ui.component.editor.bceditor.subeditor;

import ui.component.IBorder;
import ui.component.IComponent;
import ui.component.ILabel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ryan Thomson on 09/01/2017.
 */
public class IBCAttributeEditor extends IBCSubEditor {

    public IBCAttributeEditor(){
        super("Attributes");

        final JPanel panel = new JPanel();
        {
            panel.setLayout(new BorderLayout(0, 0));
            panel.setBackground(Color.ORANGE);
            panel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }
        super.add(panel, BorderLayout.CENTER);
    }

    @Override
    public void populate() {

    }
}
