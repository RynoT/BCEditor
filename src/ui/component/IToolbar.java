package ui.component;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ryan Thomson on 04/11/2016.
 */
public class IToolbar extends IComponent {

    public static final int TOOLBAR_DEFAULT_SIZE = 26;
    public static final int CONTENT_DEFAULT_SIZE = 300;

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
            button.setOrientation(IOrientation.getOpposite(this.orientation), false);
        }
        this.buttonPanel.add(button, top ? this.buttonPanel.getComponentZOrder(this.buttonSeparator) : this.buttonPanel.getComponentCount());
    }

    private class IToolbarContent extends IComponent {

        private static final int UPPER_INDEX = 0;
        private static final int LOWER_INDEX = 1;

        private int size = IToolbar.CONTENT_DEFAULT_SIZE;
        private final ITab[] tabs = new ITab[2];

        private int lowerSize = 250;

        private IToolbarContent() {
            super.setBackground(Color.YELLOW);
            super.setLayout(new BorderLayout(0, 0));
            super.setPreferredSize(new Dimension(this.size, Integer.MAX_VALUE));

            super.setVisible(false);
        }

        private int getContentSize(){
            return super.isVisible() ? size : 0;
        }

        private void enableTab(final ITab tab){
            final int index = tab.isMainTab() ? IToolbarContent
                    .UPPER_INDEX : IToolbarContent.LOWER_INDEX;
            if(this.tabs[index] != null){
                this.disableTab(this.tabs[index]);
            }
            tab.display();
            this.tabs[index] = tab;

            if(tab.isMainTab()){
                tab.getContent().setPreferredSize(new Dimension(0, Integer.MAX_VALUE));
                if(this.tabs[IToolbarContent.LOWER_INDEX] != null){
                    this.tabs[IToolbarContent.LOWER_INDEX].getContent()
                            .setPreferredSize(new Dimension(0, this.lowerSize));
                }
                super.add(tab.getContent(), BorderLayout.CENTER);
            } else {
                if(this.tabs[IToolbarContent.UPPER_INDEX] == null){
                    tab.getContent().setPreferredSize(new Dimension(0, Integer.MAX_VALUE));
                } else {
                    tab.getContent().setPreferredSize(new Dimension(0, this.lowerSize));
                }
                super.add(tab.getContent(), BorderLayout.SOUTH);
            }
            super.revalidate();
        }

        private void disableTab(final ITab tab){
            final int index = tab.isMainTab() ? IToolbarContent
                    .UPPER_INDEX : IToolbarContent.LOWER_INDEX;
            if(this.tabs[index] == null || this.tabs[index] != tab){
                return;
            }
            tab.hide();
            this.tabs[index] = null;

            super.remove(tab.getContent());

            if(tab.isMainTab() && this.tabs[IToolbarContent.LOWER_INDEX] != null){
                this.tabs[IToolbarContent.LOWER_INDEX].getContent().setPreferredSize(new Dimension(0, Integer.MAX_VALUE));
            }
        }

        private void setContentTab(final ITab tab, final boolean visible){
            assert(tab.getContent() != null);
            if(visible) {
                this.enableTab(tab);
            } else {
                this.disableTab(tab);
            }
            boolean showContent = this.tabs[IToolbarContent.UPPER_INDEX] != null
                    || this.tabs[IToolbarContent.LOWER_INDEX] != null;
            if(super.isVisible() != showContent){
                super.setVisible(showContent);
                IToolbar.this.setDimensions();
            }
        }
    }
}
