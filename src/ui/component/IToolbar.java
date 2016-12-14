package ui.component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan Thomson on 04/11/2016.
 */
public class IToolbar extends IComponent {

    public static final int TOOLBAR_DEFAULT_SIZE = 26;
    public static final int CONTENT_DEFAULT_SIZE = 300;

    private final IOrientation orientation;

    private final JPanel buttonPanel;
    private final IToolbarContent contentPanel;

    public IToolbar(final IOrientation orientation) {
        this.orientation = orientation;

        super.setBackground(Color.PINK);
        super.setLayout(new BorderLayout(0, 0));

        final JPanel buttonPanel = new JPanel();
        {
            buttonPanel.setBackground(Color.GREEN);
            buttonPanel.setLayout(new BoxLayout(buttonPanel, orientation.isVertical() ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));
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

    public void addTab(final ITab tab) {
        final IButton button = tab.getButton();
        {
            // Add an event to the button which will show the tab when pressed
            if(tab.getContent() != null) {
                button.addEvent(() -> IToolbar.this.contentPanel.setContentTab(tab, button.isSelected()));
            }
            button.setOrientation(IOrientation.getOpposite(this.orientation), false);
        }
        this.buttonPanel.add(button, 0);
    }

    private class IToolbarContent extends IComponent {

        private int size = IToolbar.CONTENT_DEFAULT_SIZE;
        private final List<ITab> tabs = new ArrayList<>();

        private IToolbarContent() {
            super.setBackground(Color.YELLOW);
            super.setLayout(new BorderLayout(0, 0));
            super.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            super.setVisible(false);
        }

        private int getContentSize(){
            return super.isVisible() ? size : 0;
        }

        private void setContentTab(final ITab tab, final boolean visible){
            if(visible) {
                if(this.tabs.contains(tab)) {
                    return;
                }
                assert (!tab.isDisplayed());
                tab.display();
                this.tabs.add(tab);
            } else {
                if(!this.tabs.contains(tab)){
                    return;
                }
                assert(tab.isDisplayed());
                tab.hide();
                this.tabs.remove(tab);
            }
            boolean showContent = this.tabs.size() > 0;
            if(super.isVisible() != showContent){
                super.setVisible(showContent);
                IToolbar.this.setDimensions();
            }
        }
    }
}
