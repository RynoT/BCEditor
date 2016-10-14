package ui.component;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class IMenuBar extends IComponent {

    public static final int DEFAULT_HEIGHT = 24;

    public IMenuBar(){
        super.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        //super.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        super.setBackground(Color.RED);
        this.setHeight(IMenuBar.DEFAULT_HEIGHT);
    }

    @Override
    protected void setDimensions() {
    }

    public void setHeight(final int height){
        super.setPreferredSize(new Dimension(Integer.MAX_VALUE, height));
        super.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        super.setMinimumSize(new Dimension(0, height));
    }
}
