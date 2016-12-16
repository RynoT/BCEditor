package ui.component;

import java.awt.*;

/**
 * Created by Ryan Thomson on 15/12/2016.
 */
public class ISeparator extends IComponent {

    public static final int SEPARATOR_SIZE = 5;
    public static final Color SEPARATOR_COLOR = new Color(80, 80, 80);

    private final IOrientation orientation;

    public ISeparator(final IOrientation orientation){
        //super.setBackground(ISeparator.SEPARATOR_COLOR);
        this.orientation = orientation;
        super.setOpaque(false);

        if(orientation.isHorizontal()){
            super.setPreferredSize(new Dimension(Integer.MAX_VALUE, ISeparator.SEPARATOR_SIZE));
            super.setMaximumSize(super.getPreferredSize());
            super.setMinimumSize(new Dimension(0, ISeparator.SEPARATOR_SIZE));
        } else {
            super.setPreferredSize(new Dimension(ISeparator.SEPARATOR_SIZE, Integer.MAX_VALUE));
            super.setMaximumSize(super.getPreferredSize());
            super.setMinimumSize(new Dimension(ISeparator.SEPARATOR_SIZE,0));
        }
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        g.setColor(ISeparator.SEPARATOR_COLOR);
        if(this.orientation.isHorizontal()){
            g.drawLine(0, super.getHeight() / 2, super.getWidth() - 1, super.getHeight() / 2);
        } else {
            g.drawLine(super.getWidth() / 2, 0, super.getWidth() / 2, super.getHeight() - 1);
        }
    }
}
