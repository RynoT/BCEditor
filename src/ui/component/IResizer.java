package ui.component;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Ryan Thomson on 15/12/2016.
 */
public class IResizer extends IComponent implements ComponentListener {

    public static final int RESIZER_SIZE_NORMAL = 4;
    public static final int RESIZER_SIZE_DETAILED = 7;

    public static final int COMPONENT_MIN_SIZE = 20;

    private final Component parent, comp1, comp2;
    private final IOrientation orientation;
    private final boolean detailed;

    public IResizer(final Component parent, final Component comp1, final Component comp2, final IOrientation orientation, final boolean detailed) {
        assert ((comp1 == null || parent != null) && comp2 != null);
        this.parent = parent;
        this.comp1 = comp1;
        this.comp2 = comp2;
        this.orientation = orientation;
        this.detailed = detailed;

        final MouseAdapter adapter = new IResizerMouseAdapter();
        super.addMouseListener(adapter);
        super.addMouseMotionListener(adapter);

        super.setBackground(Color.CYAN);
        super.setCursor(Cursor.getPredefinedCursor(orientation.isHorizontal() ? Cursor.N_RESIZE_CURSOR : Cursor.E_RESIZE_CURSOR));

        final Dimension dimension;
        if(orientation.isHorizontal()) {
            dimension = new Dimension(Integer.MAX_VALUE, this.getResizerSize());
        } else {
            dimension = new Dimension(this.getResizerSize(), Integer.MAX_VALUE);
        }
        super.setPreferredSize(dimension);
        super.setMaximumSize(dimension);
        super.setMinimumSize(dimension);
    }

    public int getResizerSize(){
        return this.detailed ? IResizer.RESIZER_SIZE_DETAILED : IResizer.RESIZER_SIZE_NORMAL;
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        if(this.comp1 == null || e.getComponent() != this.parent){
            return;
        }
        final boolean horizontal = this.orientation.isHorizontal();
        final int value = horizontal ? this.comp1.getHeight() : this.comp1.getWidth();
        if(value < IResizer.COMPONENT_MIN_SIZE){
            final int max = (horizontal ? this.parent.getHeight() : this.parent.getWidth()) - IResizer.COMPONENT_MIN_SIZE;
            final Dimension current = this.comp2.getPreferredSize();
            this.comp2.setPreferredSize(new Dimension(horizontal ? current.width : max, horizontal ? max : current.height));
            this.comp2.revalidate();
        }
    }

    @Override
    public void componentMoved(final ComponentEvent e) {
    }

    @Override
    public void componentShown(final ComponentEvent e) {
    }

    @Override
    public void componentHidden(final ComponentEvent e) {
    }

    private class IResizerMouseAdapter extends MouseAdapter {

        private int pressX, pressY;

        @Override
        public void mousePressed(final MouseEvent e) {
            this.pressX = e.getXOnScreen();
            this.pressY = e.getYOnScreen();
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            int offset;
            final boolean horizontal = IResizer.this.orientation.isHorizontal();
            if(horizontal) {
                offset = this.pressY - e.getYOnScreen();
                this.pressY = e.getYOnScreen();
            } else {
                offset = this.pressX - e.getXOnScreen();
                this.pressX = e.getXOnScreen();
            }
            if(IResizer.this.orientation == IOrientation.EAST
                    || IResizer.this.orientation == IOrientation.NORTH){
                offset = -offset;
            }
            final Component comp1 = IResizer.this.comp1, comp2 = IResizer.this.comp2;
            final Dimension dim2 = comp2.getPreferredSize();

            int newSize;
            if(horizontal){
                newSize = dim2.height + offset;
            } else {
                newSize = dim2.width + offset;
            }
            if(comp1 != null && (horizontal ? comp1.getHeight() : comp1.getWidth()) - offset < IResizer.COMPONENT_MIN_SIZE){
                newSize = (horizontal ? IResizer.this.parent.getHeight()
                        : IResizer.this.parent.getWidth()) - IResizer.COMPONENT_MIN_SIZE;
            } else if(newSize < IResizer.COMPONENT_MIN_SIZE + IResizer.this.getResizerSize()){
                newSize = IResizer.COMPONENT_MIN_SIZE + IResizer.this.getResizerSize();
            }
            comp2.setPreferredSize(new Dimension(horizontal ? dim2.width : newSize, horizontal ? newSize : dim2.height));
            comp2.setMaximumSize(comp2.getPreferredSize());
            comp2.revalidate();
        }
    }
}
