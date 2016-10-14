package ui.component.event;

import ui.component.IComponent;
import ui.component.ILabel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class IForwardMouseEvent extends MouseAdapter {

    @Override
    public void mouseClicked(final MouseEvent event) {
        this.passEvent(event);
    }

    @Override
    public void mousePressed(final MouseEvent event) {
        this.passEvent(event);
    }

    @Override
    public void mouseReleased(final MouseEvent event) {
        this.passEvent(event);
    }

    @Override
    public void mouseEntered(final MouseEvent event) {
        this.passEvent(event);
    }

    @Override
    public void mouseExited(final MouseEvent event) {
        this.passEvent(event);
    }

    @Override
    public void mouseMoved(final MouseEvent event) {
        this.passEvent(event);
    }

    @Override
    public void mouseDragged(final MouseEvent event) {
        this.passEvent(event);
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent event) {
        this.passEvent(event);
    }

    private void passEvent(final MouseEvent event){
        if(event.getComponent() instanceof IComponent){
            final IComponent component = (IComponent) event.getComponent();
            component.forwardMouseEvent(event);
            System.out.println(134);
        } else {
            System.err.printf("IForwardMouseEvent was added to invalid component (%s)\n", event.getComponent().toString());
        }
    }
}
