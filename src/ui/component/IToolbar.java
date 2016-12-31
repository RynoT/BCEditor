package ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

        super.setBackground(IComponent.DEFAULT_HIGHLIGHT_LIGHT);
        super.setLayout(new BorderLayout(0, 0));

        final JPanel buttonPanel = new JPanel();
        {
            buttonPanel.setBackground(IComponent.DEFAULT_BACKGROUND);
            buttonPanel.setLayout(new BoxLayout(buttonPanel, orientation.isVertical() ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));
            switch(orientation){
                case NORTH:
                    buttonPanel.setBorder(new IBorder(0, 0, 2, 0));
                    break;
                case EAST:
                    buttonPanel.setBorder(new IBorder(1, 0, 1, 2));
                    break;
                case SOUTH:
                    buttonPanel.setBorder(new IBorder(2, 0, 0, 0));
                    break;
                case WEST:
                    buttonPanel.setBorder(new IBorder(1, 2, 1, 0));
                    break;
            }

            this.buttonSeparator = Box.createGlue();
            buttonPanel.add(this.buttonSeparator);
        }
        super.add(this.buttonPanel = buttonPanel, orientation.getBorder());
        super.add(this.contentPanel = new IToolbarContent(), IOrientation.getOpposite(orientation).getBorder());

        this.setDimensions();
    }

    public JPanel getButtonPanel(){
        return this.buttonPanel;
    }

    public IOrientation getOrientation() {
        return this.orientation;
    }

    public IComponent getUpperContent(){
        if(this.contentPanel.tabs[IToolbarContent.UPPER_INDEX] != null) {
            return this.contentPanel.tabs[IToolbarContent.UPPER_INDEX].getContent();
        }
        return null;
    }

    public IComponent getLowerContent(){
        if(this.contentPanel.tabs[IToolbarContent.LOWER_INDEX] != null) {
            return this.contentPanel.tabs[IToolbarContent.LOWER_INDEX].getContent();
        }
        return null;
    }

    public void setContentSize(final int size){
        if(this.orientation.isHorizontal()) {
            this.contentPanel.setPreferredSize(new Dimension(size, Integer.MAX_VALUE));
        } else {
            this.contentPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, size));
        }
        this.contentPanel.revalidate();
        this.contentPanel.repaint();
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

    public void addTab(final ITab tab, final boolean first) {
        final IButton button = tab.getButton();
        {
            // Add an event to the button which will show the tab when pressed
            if(tab.getContent() != null) {
                button.addEvent(() -> IToolbar.this.contentPanel.setContentTab(tab, button.isSelected()));
            }
            // We only need to change the orientation of the buttons if this toolbar is EAST or WEST
            if(this.orientation.isHorizontal()) {
                button.setOrientation(IOrientation.getOpposite(this.orientation), false);
            }
        }
        this.buttonPanel.add(button, first ? this.buttonPanel.getComponentZOrder(this.buttonSeparator) : this.buttonPanel.getComponentCount());
    }

    private class IToolbarContent extends IComponent {

        private static final int UPPER_INDEX = 0;
        private static final int LOWER_INDEX = 1;

        private final JPanel innerPanel;
        private IResizer innerResizer = null;
        private final IResizer outerReszier;
        private final ITab[] tabs = new ITab[2];

        private IToolbarContent() {
            super.setBackground(Color.YELLOW);
            super.setLayout(new BorderLayout(0, 0));

            if(IToolbar.this.orientation.isHorizontal()) {
                super.setPreferredSize(new Dimension(IToolbar.CONTENT_DEFAULT_SIZE, Integer.MAX_VALUE));
            } else {
                super.setPreferredSize(new Dimension(Integer.MAX_VALUE, IToolbar.CONTENT_DEFAULT_SIZE));
            }

            final JPanel inner = new JPanel();
            {
                inner.setLayout(new BorderLayout(0, 0));
            }
            super.add(this.innerPanel = inner, BorderLayout.CENTER);

            final IOrientation orientation = IOrientation.getOpposite(IToolbar.this.orientation);
            this.outerReszier = new IResizer(null, null, this, orientation,false);
            super.add(this.outerReszier, orientation.getBorder());

            super.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(final ComponentEvent e) {
                    IToolbar.this.setDimensions();
                }
            });
            super.setVisible(false);
        }

        private int getContentSize() {
            if(!super.isVisible()) {
                return 0;
            }
            final Dimension size = super.getPreferredSize();
            return IToolbar.this.orientation.isHorizontal() ? size.width : size.height;
        }

        private Dimension getUpperDimension() {
            final boolean horizontal = IToolbar.this.orientation.isHorizontal();
            return new Dimension(horizontal ? 0 : Integer.MAX_VALUE, horizontal ? Integer.MAX_VALUE : 0);
        }

        private Dimension getLowerDimension() {
            final boolean horizontal = IToolbar.this.orientation.isHorizontal();
            return new Dimension(horizontal ? 0 : IToolbar.DEFAULT_LOWER_SIZE, horizontal ? IToolbar.DEFAULT_LOWER_SIZE : 0);
        }

        private void createInnerResizer(final IComponent upper, final IComponent lower) {
            assert (this.innerResizer == null);
            final IOrientation orientation = IToolbar.this.orientation.isHorizontal() ? IOrientation.NORTH : IOrientation.WEST;
            this.innerResizer = new IResizer(this, upper, lower, orientation, true);
            lower.add(this.innerResizer, orientation.getBorder());
            super.addComponentListener(this.innerResizer);
        }

        private void destroyResizer(final IComponent lower) {
            assert (this.innerResizer != null);
            lower.remove(this.innerResizer);
            super.removeComponentListener(this.innerResizer);
            this.innerResizer = null;
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
                    this.createInnerResizer(content, lowerContent);
                    lowerContent.setPreferredSize(this.getLowerDimension());
                }
                this.innerPanel.add(content, BorderLayout.CENTER);
            } else {
                if(this.tabs[IToolbarContent.UPPER_INDEX] == null) {
                    content.setPreferredSize(this.getUpperDimension());
                } else {
                    this.createInnerResizer(this.tabs[IToolbarContent.UPPER_INDEX].getContent(), content);
                    content.setPreferredSize(this.getLowerDimension());
                }
                this.innerPanel.add(content, IToolbar.this.orientation.isHorizontal() ? BorderLayout.SOUTH : BorderLayout.EAST);
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

            this.innerPanel.remove(tab.getContent());

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
