package ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public abstract class IComponent extends JPanel {

    public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);

    public static final Color DEFAULT_BACKGROUND = new Color(68, 69, 70);
    public static final Color DEFAULT_BACKGROUND_DARK = new Color(46, 47, 48);
    public static final Color DEFAULT_BACKGROUND_INTERMEDIATE = new Color(53, 54, 55);
    public static final Color DEFAULT_FOREGROUND = new Color(199, 201, 203);
    public static final Color DEFAULT_HIGHLIGHT_DARK = new Color(45, 46, 47);
    public static final Color DEFAULT_HIGHLIGHT_LIGHT = new Color(85, 86, 87);

    public static final Color DEFAULT_BACKGROUND_HIGHLIGHT = new Color(202, 84, 0);

//    public static final Color DEFAULT_HOVERED = new Color(10, 97, 116);
//    public static final Color DEFAULT_PRESSED = new Color(4, 59, 70);

    public static final Color DEFAULT_HOVERED = new Color(182, 76, 0);
    public static final Color DEFAULT_PRESSED = new Color(117, 47, 0);
    public static final Color DEFAULT_SELECTED = new Color(56, 56, 57);

//    public static final Color DEFAULT_HOVERED = new Color(151, 55, 150);
//    public static final Color DEFAULT_PRESSED = new Color(93, 33, 92);
//
//    public static final Color DEFAULT_HOVERED = new Color(168, 24, 27);
//    public static final Color DEFAULT_PRESSED = new Color(112, 24, 24);

    protected IComponent() {
        super.setFont(IComponent.DEFAULT_FONT);
    }

    protected void setDimensions() { }

    protected final void updateDimensions() {
        if(super.getParent() instanceof IComponent) {
            ((IComponent) super.getParent()).updateDimensions();
        }
        this.setDimensions();
        super.revalidate();
    }

    public void forwardMouseEvent(final MouseEvent event) {
        super.getParent().dispatchEvent(SwingUtilities.convertMouseEvent(event.getComponent(), event, super.getParent()));
    }
}
