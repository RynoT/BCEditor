package ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public abstract class IComponent extends JPanel {

    public static final Font DEFAULT_FONT = new Font("Helvetica", Font.PLAIN, 12);

    public static final Color DEFAULT_BACKGROUND = new Color(78, 79, 80);
    public static final Color DEFAULT_FOREGROUND = new Color(240, 242, 244);
    public static final Color DEFAULT_HIGHLIGHT = new Color(45, 46, 47, 235);

//    public static final Color DEFAULT_HOVERED = new Color(10, 97, 116);
//    public static final Color DEFAULT_PRESSED = new Color(4, 59, 70);

    public static final Color DEFAULT_HOVERED = new Color(182, 76, 0);
    public static final Color DEFAULT_PRESSED = new Color(117, 47, 0);

//    public static final Color DEFAULT_HOVERED = new Color(151, 55, 150);
//    public static final Color DEFAULT_PRESSED = new Color(93, 33, 92);
//
//    public static final Color DEFAULT_HOVERED = new Color(168, 24, 27);
//    public static final Color DEFAULT_PRESSED = new Color(112, 24, 24);

    protected IComponent() {
        super.setFont(IComponent.DEFAULT_FONT);
    }

    protected abstract void setDimensions();

    protected final void updateDimensions() {
        if(super.getParent() instanceof IComponent) {
            ((IComponent) super.getParent()).setDimensions();
            ((IComponent) super.getParent()).updateDimensions();
        }
    }

    public void forwardMouseEvent(final MouseEvent event) {
        super.getParent().dispatchEvent(SwingUtilities.convertMouseEvent(event.getComponent(), event, super.getParent()));
    }
}
