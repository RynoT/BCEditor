package ui.component;

import ui.component.event.IActionEvent;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

/**
 * Created by Ryan Thomson on 14/10/2016.
 */
public abstract class IActionComponent extends IComponent {

    private boolean pressed = false, hovered = false;

    private IActionEvent[] events = new IActionEvent[0];

    protected IActionComponent(){
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public boolean isHovered() {
        return this.hovered;
    }

    protected void setPressed(final boolean pressed){
        this.pressed = pressed;
    }

    protected void setHovered(final boolean hovered){
        this.hovered = hovered;
    }

    public void addEvent(final IActionEvent event){
        assert (this.events != null && event != null);
        final IActionEvent[] events = new IActionEvent[this.events.length + 1];
        System.arraycopy(this.events, 0, events, 0, this.events.length);
        events[events.length - 1] = event;
        this.events = events;
    }

    public void removeEvent(final IActionEvent event){
        assert (this.events != null && event != null);
        int index = -1;
        for(int i = 0; i < this.events.length; i++){
            if(this.events[i] == event){
                index = i;
                break;
            }
        }
        if(index == -1){
            System.err.println("[IActionComponent] Unable to find index for event");
        } else { //size will always be more than 0 if we get to here
            final IActionEvent[] events = new IActionEvent[this.events.length - 1];
            System.arraycopy(this.events, 0, events, 0, index);
            System.arraycopy(this.events, index + 1, events, index, events.length - index);
            this.events = events;
        }
    }

    public void click(){
        final boolean hovered = this.hovered;
        if(!hovered) {
            this.hover();
        }
        this.press();
        this.release();
        if(!hovered){
            this.unhover();
        }
    }

    public void press(){
        for(final MouseListener mouseListener : super.getMouseListeners()){
            mouseListener.mousePressed(new MouseEvent(this, MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(), 0, 0, 0, 1, false, MouseEvent.BUTTON1));
        }
    }

    public void release(){
        for(final MouseListener mouseListener : super.getMouseListeners()){
            mouseListener.mouseReleased(new MouseEvent(this, MouseEvent.MOUSE_RELEASED,
                    System.currentTimeMillis(), 0, 0, 0, 1, false, MouseEvent.BUTTON1));
        }
    }

    public void hover(){
        for(final MouseListener mouseListener : super.getMouseListeners()){
            mouseListener.mouseEntered(new MouseEvent(this, MouseEvent.MOUSE_ENTERED,
                    System.currentTimeMillis(), 0, 0, 0, 1, false));
        }
    }

    public void unhover(){
        for(final MouseListener mouseListener : super.getMouseListeners()){
            mouseListener.mouseExited(new MouseEvent(this, MouseEvent.MOUSE_EXITED,
                    System.currentTimeMillis(), 0, 0, 0, 1, false));
        }
    }

    public void runEvents(){
        for(final IActionEvent event : this.events){
            event.action();
        }
    }
}
