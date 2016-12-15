package ui.component;

import java.awt.*;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class IMenuBar extends IComponent {

    public static final int DEFAULT_HEIGHT = 26;

    public IMenuBar(){
        super.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

        this.setHeight(IMenuBar.DEFAULT_HEIGHT);
        super.setBackground(IComponent.DEFAULT_BACKGROUND);

        super.setBorder(new IBorder(0, 0, 2,0));
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
