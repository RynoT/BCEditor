package ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Ryan Thomson on 15/12/2016.
 */
public class IResizer extends IComponent implements ComponentListener {

    public static final int RESIZER_SIZE_NORMAL = 3;
    public static final int RESIZER_SIZE_DETAILED = 9;

    public static final int RESIZER_DETAILED_DOT_FREQUENCY = 8;
    public static final float RESIZER_DETAILED_DOT_SPACE = 0.25f; //take 25% of space (form middle)
    public static final Color RESIZER_DETAILED_DOT_COLOR = new Color(230, 232, 234, 220);

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

        super.setBackground(detailed ? IComponent.DEFAULT_BACKGROUND : IComponent.DEFAULT_HIGHLIGHT_LIGHT);
        super.setCursor(Cursor.getPredefinedCursor(orientation.isHorizontal() ? Cursor.E_RESIZE_CURSOR : Cursor.N_RESIZE_CURSOR));

        final Dimension dimension;
        if(orientation.isHorizontal()) {
            super.setBorder(new IBorder(0, 1, 0, 1));
            dimension = new Dimension(this.getResizerSize(), Integer.MAX_VALUE);
        } else {
            super.setBorder(new IBorder(1, 0, 1, 0));
            dimension = new Dimension(Integer.MAX_VALUE, this.getResizerSize());
        }
        super.setPreferredSize(dimension);
        super.setMaximumSize(dimension);
        super.setMinimumSize(dimension);
    }

    public int getResizerSize() {
        return this.detailed ? IResizer.RESIZER_SIZE_DETAILED : IResizer.RESIZER_SIZE_NORMAL;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if(this.detailed) {
            g.setColor(IResizer.RESIZER_DETAILED_DOT_COLOR);

            if(this.orientation.isHorizontal()) {
                final int dist = (int) (super.getHeight() * IResizer.RESIZER_DETAILED_DOT_SPACE);
                final int x = super.getWidth() / 2;
                for(int j = super.getHeight() / 2 - dist / 2; j < super.getHeight() / 2 + dist / 2;
                    j += IResizer.RESIZER_DETAILED_DOT_FREQUENCY) {
                    g.drawLine(x, j, x, j); // draw dot
                }
            } else {
                final int dist = (int) (super.getWidth() * IResizer.RESIZER_DETAILED_DOT_SPACE);
                final int y = super.getHeight() / 2;
                for(int i = super.getWidth() / 2 - dist / 2; i < super.getWidth() / 2 + dist / 2;
                    i += IResizer.RESIZER_DETAILED_DOT_FREQUENCY) {
                    g.drawLine(i, y, i, y); // draw dot
                }
            }
        }
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        if(this.comp1 == null || e.getComponent() != this.parent) {
            return;
        }
        final boolean horizontal = this.orientation.isHorizontal();
        final int value = horizontal ? this.comp1.getWidth() : this.comp1.getHeight();
        if(value < IResizer.COMPONENT_MIN_SIZE) {
            final int max = (horizontal ? this.parent.getWidth() : this.parent.getHeight()) - IResizer.COMPONENT_MIN_SIZE;
            final Dimension current = this.comp2.getPreferredSize();
            this.comp2.setPreferredSize(new Dimension(horizontal ? max : current.width, horizontal ? current.height : max));
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
                offset = this.pressX - e.getXOnScreen();
                this.pressX = e.getXOnScreen();
            } else {
                offset = this.pressY - e.getYOnScreen();
                this.pressY = e.getYOnScreen();
            }
            if(IResizer.this.orientation == IOrientation.SOUTH
                    || IResizer.this.orientation == IOrientation.EAST) {
                offset = -offset;
            }
            final Component comp1 = IResizer.this.comp1, comp2 = IResizer.this.comp2;
            final Dimension dim2 = comp2.getPreferredSize();

            int newSize;
            if(horizontal) {
                newSize = dim2.width + offset;
            } else {
                newSize = dim2.height + offset;
            }
            if(comp1 != null && (horizontal ? comp1.getWidth() : comp1.getHeight()) - offset < IResizer.COMPONENT_MIN_SIZE) {
                newSize = (horizontal ? IResizer.this.parent.getWidth()
                        : IResizer.this.parent.getHeight()) - IResizer.COMPONENT_MIN_SIZE;
            } else if(newSize < IResizer.COMPONENT_MIN_SIZE + IResizer.this.getResizerSize()) {
                newSize = IResizer.COMPONENT_MIN_SIZE + IResizer.this.getResizerSize();
            }
            comp2.setPreferredSize(new Dimension(horizontal ? newSize : dim2.width, horizontal ? dim2.height : newSize));
            comp2.setMaximumSize(comp2.getPreferredSize());
            comp2.revalidate();
        }
    }
}
