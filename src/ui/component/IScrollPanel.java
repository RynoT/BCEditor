package ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Ryan Thomson on 22/12/2016.
 */
public class IScrollPanel extends IComponent {

    public static final int SCROLLER_SIZE = 8;
    public static final int SCROLLER_MIN_SCALE = 8;
    public static final int SCROLLER_WHEEL_SENSITIVITY = 4;

    public static final Color SCROLLER_COLOR = new Color(110, 110, 110);
    public static final Color SCROLLER_HOVER_COLOR = new Color(135, 135, 135);
    public static final Color SCROLLER_HOVER_BACKGROUND = new Color(90, 90, 90, 185);

    private final JViewport viewport;
    private final Component content;
    private final IScroller horizontal, vertical;

    public IScrollPanel(final Container content, final boolean horizontal, final boolean vertical) {
        assert (content != null && content != this);
        this.content = content;
        this.viewport = new JViewport();
        this.viewport.setOpaque(false);
        this.viewport.setView(content);
        this.viewport.setScrollMode(JViewport.BLIT_SCROLL_MODE);
        this.viewport.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                content.repaint();
            }
        });

        super.setLayout(new BorderLayout(0, 0));
        super.add(this.viewport, BorderLayout.CENTER);

        this.horizontal = horizontal ? new IScroller(true, IScrollPanel.SCROLLER_SIZE) : null;
        this.vertical = vertical ? new IScroller(false, IScrollPanel.SCROLLER_SIZE) : null;
        if(horizontal) {
            super.add(this.horizontal, BorderLayout.SOUTH);
        }
        if(vertical) {
            super.add(this.vertical, BorderLayout.EAST);

            content.addMouseWheelListener(e -> IScrollPanel.this.vertical.move((int) (e
                    .getUnitsToScroll() * IScrollPanel.this.vertical.getWheelMultiplier())));
        }
        this.updateScale();

        super.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                IScrollPanel.this.updateScale();
            }
        });
        content.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(final ContainerEvent e) {
                IScrollPanel.this.updateScale();
            }

            @Override
            public void componentRemoved(final ContainerEvent e) {
                IScrollPanel.this.updateScale();
            }
        });
    }

    public JViewport getViewport() {
        return this.viewport;
    }

    private void updateScale() {
        final Dimension target = this.content.getPreferredSize();
        if(this.vertical != null) {
            if(target.height == 0 || super.getHeight() >= target.height) {
                this.vertical.y = 0;
                this.vertical.height = -1;
            } else {
                this.vertical.height = Math.max(IScrollPanel.SCROLLER_MIN_SCALE,
                        super.getHeight() * super.getHeight() / target.height);
                this.vertical.y = (int) (super.getHeight() * this.viewport.getViewPosition().y / (float) target.height);
            }
            this.vertical.limitPosition();
            this.vertical.repaint();
        }
        if(this.horizontal != null) {
            if(target.width == 0 || super.getWidth() >= target.width) {
                this.horizontal.x = 0;
                this.horizontal.width = -1;
            } else {
                this.horizontal.width = Math.max(IScrollPanel.SCROLLER_MIN_SCALE,
                        super.getWidth() * super.getWidth() / target.width);
                this.horizontal.x = (int) (super.getWidth() * this.viewport.getViewPosition().x / (float) target.width);
            }
            this.horizontal.limitPosition();
            this.horizontal.repaint();
        }
    }

    private class IScroller extends IComponent {

        private final boolean horizontal;

        private int x = 0, y = 0, width, height;
        private boolean backHover = false, barHover = false, scrolling = false;

        private IScroller(final boolean horizontal, final int size) {
            this.horizontal = horizontal;

            super.setOpaque(false);
            super.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            super.setPreferredSize(new Dimension(horizontal ? 0 : size, horizontal ? size : 0));
            super.setMinimumSize(super.getPreferredSize());

            this.width = horizontal ? 0 : IScrollPanel.SCROLLER_SIZE;
            this.height = horizontal ? IScrollPanel.SCROLLER_SIZE : 0;

            final MouseAdapter mouse = new MouseAdapter() {

                private Point press;

                @Override
                public void mouseEntered(final MouseEvent e) {
                    IScroller.this.backHover = true;
                    IScroller.super.repaint();
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    IScroller.this.backHover = false;
                    IScroller.this.barHover = false;
                    IScroller.super.repaint();
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                    if(this.isBarHovered(e.getPoint())) {
                        IScroller.this.scrolling = true;
                        this.press = e.getLocationOnScreen();
                    }
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    IScroller.this.scrolling = false;
                    IScroller.super.repaint();
                }

                @Override
                public void mouseMoved(final MouseEvent e) {
                    final boolean hovered = this.isBarHovered(e.getPoint());
                    if(IScroller.this.barHover != hovered) {
                        IScroller.this.barHover = hovered;
                        IScroller.super.repaint();
                    }
                }

                @Override
                public void mouseDragged(final MouseEvent e) {
                    if(!IScroller.this.scrolling) {
                        return;
                    }
                    final int offset;
                    if(IScroller.this.horizontal) {
                        offset = e.getXOnScreen() - this.press.x;
                    } else {
                        offset = e.getYOnScreen() - this.press.y;
                    }
                    IScroller.this.move(offset);
                    this.press = e.getLocationOnScreen(); //reset offset for next event
                }

                @Override
                public void mouseWheelMoved(final MouseWheelEvent e) {
                    IScroller.this.move((int) (e.getUnitsToScroll() * IScroller.this.getWheelMultiplier()));
                }

                private boolean isBarHovered(final Point p) {
                    // Check to see if mouse is on top of scroll bar
                    return p.getX() >= IScroller.this.x && p.getX() < IScroller.this.x + IScroller.this.width
                            && p.getY() >= IScroller.this.y && p.getY() < IScroller.this.y + IScroller.this.height;
                }
            };
            super.addMouseListener(mouse);
            super.addMouseWheelListener(mouse);
            super.addMouseMotionListener(mouse);
        }

        private float getWheelMultiplier() {
            final float multiplier = super.getHeight() * 0.001f;
            return multiplier > 1.0f ? IScrollPanel.SCROLLER_WHEEL_SENSITIVITY : IScrollPanel.SCROLLER_WHEEL_SENSITIVITY * multiplier;
        }

        private void move(final int offset) {
            if(this.horizontal) {
                this.x += offset;
            } else {
                this.y += offset;
            }
            IScroller.this.limitPosition();
            IScroller.this.updateViewport();
            IScroller.super.repaint();
        }

        private void limitPosition() {
            if(this.horizontal) {
                int width = IScrollPanel.super.getWidth();
                if(IScrollPanel.this.vertical != null) {
                    width -= IScrollPanel.this.vertical.getWidth();
                }
                this.x = Math.max(0, Math.min(width - this.width, this.x));
            } else {
                int height = IScrollPanel.super.getHeight();
                if(IScrollPanel.this.horizontal != null) {
                    height -= IScrollPanel.this.horizontal.getHeight();
                }
                this.y = Math.max(0, Math.min(height - this.height, this.y));
            }
        }

        private void updateViewport() {
            final JViewport viewport = IScrollPanel.this.viewport;
            final Point viewPosition = viewport.getViewPosition();
            final Dimension target = IScrollPanel.this.content.getPreferredSize();
            if(this.horizontal) {
                viewport.setViewPosition(new Point((int) ((target.width - super.getWidth()) * this.x / (float) (super.getWidth() - this.width)), viewPosition.y));
            } else {
                viewport.setViewPosition(new Point(viewPosition.x, (int) ((target.height - super.getHeight()) * this.y / (float) (super.getHeight() - this.height))));
            }
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);

            // Set either of these to -1 to disable the scroll bar render
            if(this.width == -1 || this.height == -1) {
                return;
            }
            if(this.backHover || this.scrolling){
                g.setColor(IScrollPanel.SCROLLER_HOVER_BACKGROUND);

                int width = super.getWidth() - 1;
                if(this.horizontal && IScrollPanel.this.vertical != null){
                    width -= IScrollPanel.this.vertical.getWidth();
                }
                g.fillRect(0, 0, width, super.getHeight() - 1);
            }
            g.setColor(this.barHover || this.scrolling ? IScrollPanel.SCROLLER_HOVER_COLOR : IScrollPanel.SCROLLER_COLOR);
            g.fillRect(this.x, this.y, this.width, this.height);
        }
    }

}
