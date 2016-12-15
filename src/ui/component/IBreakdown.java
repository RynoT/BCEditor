package ui.component;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ryan Thomson on 15/12/2016.
 */
public class IBreakdown extends IComponent {

    public IBreakdown(){
        super.setBackground(Color.PINK);
        super.setLayout(new BorderLayout(0, 0));

        final JPanel content = new JPanel();
        {
            content.setBackground(Color.MAGENTA);
        }
        super.add(content, BorderLayout.CENTER);
    }
}
