package ui.component;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ryan Thomson on 04/11/2016.
 */
public class IToolbar extends IComponent {

    public static final int TOOLBAR_DEFAULT_SIZE = 26;
    public static final int CONTENT_DEFAULT_SIZE = 300;
    public static final int DEFAULT_LOWER_SIZE = 250;

    private final IOrientation orientation;

    private final JPanel buttonPanel;
    private final IToolbarContent contentPanel;
    private final Component buttonSeparator;

    public IToolbar(final IOrientation orientation) {
        this.orientation = orientation;

        super.setBackground(Color.PINK);
        super.setLayout(new BorderLayout(0, 0));

        final JPanel buttonPanel = new JPanel();
        {
            buttonPanel.setBackground(IComponent.DEFAULT_BACKGROUND);
            buttonPanel.setLayout(new BoxLayout(buttonPanel, orientation.isVertical() ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));

            this.buttonSeparator = Box.createGlue();
            buttonPanel.add(this.buttonSeparator);
        }
        super.add(this.buttonPanel = buttonPanel, orientation.getBorder());
        super.add(this.contentPanel = new IToolbarContent(), IOrientation.getOpposite(orientation).getBorder());

        this.setDimensions();
    }

    public IOrientation getOrientation() {
        return this.orientation;
    }

    @Override
    protected void setDimensions() {
        int width = Integer.MAX_VALUE, height = IToolbar.TOOLBAR_DEFAULT_SIZE;
        if(this.orientation.isVertical()) {
            this.buttonPanel.setPreferredSize(new Dimension(width, height));
            super.setPreferredSize(new Dimension(width, height + this.contentPanel.getContentSize()));
        } else {
            this.buttonPanel.setPreferredSize(new Dimension(height, width));
            super.setPreferredSize(new Dimension(height + this.contentPanel.getContentSize(), width));
        }
        super.setMinimumSize(super.getPreferredSize());
        super.setMaximumSize(super.getPreferredSize());
    }

    public void addTab(final ITab tab, final boolean top) {
        final IButton button = tab.getButton();
        {
            // Add an event to the button which will show the tab when pressed
            if(tab.getContent() != null) {
                button.addEvent(() -> IToolbar.this.contentPanel.setContentTab(tab, button.isSelected()));
            }
            // We only need to change the orientation of the buttons if this toolbar is EAST or WEST
            //if(this.orientation.isHorizontal()) {
                button.setOrientation(IOrientation.getOpposite(this.orientation), false);
            //}
        }
        this.buttonPanel.add(button, top ? this.buttonPanel.getComponentZOrder(this.buttonSeparator) : this.buttonPanel.getComponentCount());
    }

    private class IToolbarContent extends IComponent {

        private static final int UPPER_INDEX = 0;
        private static final int LOWER_INDEX = 1;

        private IResizer resizer = null;
        private final ITab[] tabs = new ITab[2];

        private IToolbarContent() {
            super.setBackground(Color.YELLOW);
            super.setLayout(new BorderLayout(0, 0));

            if(IToolbar.this.orientation.isHorizontal()) {
                super.setPreferredSize(new Dimension(IToolbar.CONTENT_DEFAULT_SIZE, Integer.MAX_VALUE));
            } else {
                super.setPreferredSize(new Dimension(Integer.MAX_VALUE, IToolbar.CONTENT_DEFAULT_SIZE));
            }

            super.setVisible(false);
        }

        private int getContentSize() {
            if(!super.isVisible()) {
                return 0;
            }
            return IToolbar.CONTENT_DEFAULT_SIZE;
        }

        private Dimension getUpperDimension() {
            final boolean horizontal = IToolbar.this.orientation.isHorizontal();
            return new Dimension(horizontal ? 0 : Integer.MAX_VALUE, horizontal ? Integer.MAX_VALUE : 0);
        }

        private Dimension getLowerDimension() {
            final boolean horizontal = IToolbar.this.orientation.isHorizontal();
            return new Dimension(horizontal ? 0 : IToolbar.DEFAULT_LOWER_SIZE, horizontal ? IToolbar.DEFAULT_LOWER_SIZE : 0);
        }

        private void createResizer(final IComponent upper, final IComponent lower) {
            assert (this.resizer == null);
            lower.add(this.resizer = new IResizer(this, upper, lower, IToolbar.this.orientation, true),
                    IToolbar.this.orientation.isHorizontal() ? BorderLayout.NORTH : BorderLayout.WEST);
            super.addComponentListener(this.resizer);
        }

        private void destroyResizer(final IComponent lower) {
            assert (this.resizer != null);
            lower.remove(this.resizer);
            super.removeComponentListener(this.resizer);
            this.resizer = null;
        }

        private void enableTab(final ITab tab) {
            final int index = tab.isMainTab() ? IToolbarContent
                    .UPPER_INDEX : IToolbarContent.LOWER_INDEX;
            if(this.tabs[index] != null) {
                this.disableTab(this.tabs[index]);
            }
            tab.display();
            this.tabs[index] = tab;

            final IComponent content = tab.getContent();
            if(tab.isMainTab()) {
                content.setPreferredSize(this.getUpperDimension());
                if(this.tabs[IToolbarContent.LOWER_INDEX] != null) {
                    final IComponent lowerContent = this.tabs[IToolbarContent.LOWER_INDEX].getContent();
                    this.createResizer(content, lowerContent);
                    lowerContent.setPreferredSize(this.getLowerDimension());
                }
                super.add(content, BorderLayout.CENTER);
            } else {
                if(this.tabs[IToolbarContent.UPPER_INDEX] == null) {
                    content.setPreferredSize(this.getUpperDimension());
                } else {
                    this.createResizer(this.tabs[IToolbarContent.UPPER_INDEX].getContent(), content);
                    content.setPreferredSize(this.getLowerDimension());
                }
                super.add(content, IToolbar.this.orientation.isHorizontal() ? BorderLayout.SOUTH : BorderLayout.EAST);
            }
            super.revalidate();
        }

        private void disableTab(final ITab tab) {
            final int index = tab.isMainTab() ? IToolbarContent
                    .UPPER_INDEX : IToolbarContent.LOWER_INDEX;
            if(this.tabs[index] == null || this.tabs[index] != tab) {
                return;
            }
            tab.hide();
            this.tabs[index] = null;

            super.remove(tab.getContent());

            if(!tab.isMainTab()) {
                if(this.tabs[IToolbarContent.UPPER_INDEX] != null) {
                    this.destroyResizer(tab.getContent());
                }
            } else if(tab.isMainTab() && this.tabs[IToolbarContent.LOWER_INDEX] != null) {
                final IComponent lowerContent = this.tabs[IToolbarContent.LOWER_INDEX].getContent();
                this.destroyResizer(lowerContent);
                lowerContent.setPreferredSize(this.getUpperDimension());
            }
        }

        private void setContentTab(final ITab tab, final boolean visible) {
            assert (tab.getContent() != null);
            if(visible) {
                this.enableTab(tab);
            } else {
                this.disableTab(tab);
            }
            boolean showContent = this.tabs[IToolbarContent.UPPER_INDEX] != null
                    || this.tabs[IToolbarContent.LOWER_INDEX] != null;
            if(super.isVisible() != showContent) {
                super.setVisible(showContent);
                IToolbar.this.setDimensions();
            }
        }
    }
}
