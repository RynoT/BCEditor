package ui.component;

import project.filetype.FileType;
import ui.*;
import ui.Canvas;
import ui.component.explorer.IFileNode;
import util.AssetManager;
import util.async.AsyncEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ryan Thomson on 26/12/2016.
 */
public class IFileViewer extends IComponent {

    public static final int TAB_BAR_HEIGHT = 24;

    public static final int TAB_BAR_HIGHLIGHT_HEIGHT = 4;

    public static final int TAB_BUTTON_PADDING_A = 4, TAB_BUTTON_PADDING_B = 0, TAB_BUTTON_PADDING_C = 8, TAB_BUTTON_PADDING_D = 8;
    public static final int TAB_BUTTON_CLOSE_SIZE = 7;
    public static final Color TAB_BUTTON_UNFOCUSED_COLOR = new Color(65, 66, 67);

    private final JPanel tabBar;

    private ViewerTab active = null;
    private final List<ViewerTab> tabs = new ArrayList<>();

    public IFileViewer() {
        super.setBackground(Color.GREEN);
        super.setLayout(new BorderLayout(0, 0));

        final JPanel top = new JPanel();
        {
            top.setLayout(new BorderLayout(0, 0));

            final JPanel tabBar = new JPanel();
            {
                tabBar.setBackground(IComponent.DEFAULT_BACKGROUND);
                tabBar.setLayout(new BoxLayout(tabBar, BoxLayout.X_AXIS));
                tabBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, IFileViewer.TAB_BAR_HEIGHT));
            }
            this.tabBar = tabBar;
            top.add(tabBar, BorderLayout.CENTER);

            final JPanel highlight = new JPanel();
            {
                highlight.setBackground(IComponent.DEFAULT_BACKGROUND_HIGHLIGHT);
                highlight.setPreferredSize(new Dimension(Integer.MAX_VALUE, IFileViewer.TAB_BAR_HIGHLIGHT_HEIGHT));
            }
            top.add(highlight, BorderLayout.SOUTH);
        }
        super.add(top, BorderLayout.NORTH);
    }

    public List<ViewerTab> getTabs(){
        return this.tabs;
    }

    public void clearTabs(){
        this.tabs.clear();
        this.tabBar.removeAll();
        this.tabBar.revalidate();
        this.tabBar.repaint();
    }

    public void setActiveTab(final ViewerTab tab) {
        assert (tab != null && this.tabs.contains(tab));
        if(this.active == tab) {
            return;
        }
        if(this.active != null) {
            this.active.setActive(false);
        }
        this.active = tab;
        tab.setActive(true);
    }

    public void open(final IFileNode node) {
        assert (node != null && node.getFileType() != null);

        // Check to see if this file is already opened or not
        for(final ViewerTab tab : this.tabs) {
            if(tab.node == node) {
                this.setActiveTab(tab);
                return;
            }
        }
        if(node.getFileType().load()){
            final ViewerTab tab = new ViewerTab(node, this.tabs.size());
            this.tabs.add(tab);

            // Automatically activate the new tab
            this.setActiveTab(tab);
            this.tabBar.add(tab.button);

            super.revalidate();
            super.repaint();
        } else {
            System.err.println("[Canvas] Unable to open file due to load failure");
        }
    }

    public class ViewerTab {

        private final IFileNode node;

        private final JPanel button;

        private int index;
        private boolean active = false;

        private ViewerTab(final IFileNode node, final int index) {
            this.node = node;
            this.index = index;

            this.button = new JPanel() {
                @Override
                protected void paintComponent(final Graphics g) {
                    super.paintComponent(g);

                    final int index = ViewerTab.this.index;
                    final int tabCount = IFileViewer.this.tabBar.getComponentCount();

                    g.setColor(IComponent.DEFAULT_HIGHLIGHT_DARK);
                    //Draw north
                    g.drawLine(0, 0, super.getWidth() - 1, 0);

                    //Draw east
                    if(index == tabCount - 1) {
                        g.drawLine(super.getWidth() - 1, 0, super.getWidth() - 1, super.getHeight() - 1);
                    }

                    //Draw south
                    if(!ViewerTab.this.active) {
                        g.drawLine(0, super.getHeight() - 1, super.getWidth() - 1, super.getHeight() - 1);
                    }

                    //Draw west
                    if(index > 0) {
                        g.drawLine(0, 0, 0, super.getHeight() - 1);
                    }
                }
            };
            this.button.setBackground(IFileViewer.TAB_BUTTON_UNFOCUSED_COLOR);
            this.button.setLayout(new BoxLayout(this.button, BoxLayout.X_AXIS));
            this.button.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(final MouseEvent e) {
                    if(!ViewerTab.this.active) {
                        IFileViewer.this.setActiveTab(ViewerTab.this);
                    }
                }
            });
            this.button.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(final MouseEvent e) {
                    final Point loc = e.getLocationOnScreen();
                    final int lastIndex = IFileViewer.this.tabs.size() - 1;
                    int newIndex = ViewerTab.this.index;
                    // Check to see if the mouse is left of the tab bar
                    if(loc.getX() < IFileViewer.this.tabBar.getLocationOnScreen().x) {
                        newIndex = 0;
                    } else {
                        // Check to see if the mouse is right of the right-most tab
                        final Component end = IFileViewer.this.tabs.get(lastIndex).button;
                        if(loc.getX() > end.getLocationOnScreen().x + end.getWidth()) {
                            newIndex = lastIndex;
                        } else {
                            // Check to see where the mouse is within the tabs
                            for(final ViewerTab tab : IFileViewer.this.tabs) {
                                final Rectangle bounds = new Rectangle(tab.button.getLocationOnScreen(),
                                        new Dimension(tab.button.getWidth(), tab.button.getHeight()));
                                // We only need to check to see if the x value is within range of the button
                                if(loc.getX() >= bounds.getX() && loc.getX() <= bounds.getX() + bounds.getWidth()) {
                                    // Decide whether to put the index before or after the tab
                                    final boolean left = loc.getX() < bounds.getX() + bounds.getWidth();
                                    newIndex = left ? tab.index : tab.index + 1;
                                    break;
                                }
                            }
                        }
                    }
                    // Ensure the new index is a valid index
                    newIndex = Math.max(Math.min(newIndex, lastIndex), 0);
                    // Check to see if the index has changed
                    if(newIndex != ViewerTab.this.index){
                        // Swap the elements on the tabs list and bar
                        IFileViewer.this.tabs.remove(ViewerTab.this.index);
                        IFileViewer.this.tabs.add(newIndex, ViewerTab.this);
                        IFileViewer.this.tabBar.remove(ViewerTab.this.index);
                        IFileViewer.this.tabBar.add(ViewerTab.this.button, newIndex);

                        // Adjust indices to match the new order
                        for(int i = 0; i < IFileViewer.this.tabs.size(); i++){
                            IFileViewer.this.tabs.get(i).index = i;
                        }

                        // Update the tab bar
                        IFileViewer.this.tabBar.revalidate();
                        IFileViewer.this.tabBar.repaint();
                    }
                }
            });

            this.updateButton();
        }

        public IFileNode getNode(){
            return this.node;
        }

        public void setIndex(final int index) {
            this.index = index;
        }

        private void setActive(final boolean active) {
            this.active = active;
            this.button.setBackground(active ? IProjectExplorer.DEFAULT_BACKGROUND_HIGHLIGHT
                    : IFileViewer.TAB_BUTTON_UNFOCUSED_COLOR);
            this.button.repaint();
        }

        public void close() {
            assert (this.node != null);
            IFileViewer.this.tabs.remove(this.index);
            for(int i = this.index; i < IFileViewer.this.tabs.size(); i++) {
                IFileViewer.this.tabs.get(i).index--;
            }
            IFileViewer.this.tabBar.remove(this.index);
            IFileViewer.this.tabBar.revalidate();
            IFileViewer.this.tabBar.repaint();

            // Unload the file since we no longer need it in memory
            this.node.getFileType().unload();
        }

        public void updateButton() {
            this.button.removeAll();

            this.button.add(Box.createHorizontalStrut(IFileViewer.TAB_BUTTON_PADDING_A));

            final IImagePanel icon = new IImagePanel(this.node.getIconPanel().getImages());
            {
                icon.setPreferredSize(new Dimension(IFileViewer.TAB_BAR_HEIGHT, IFileViewer.TAB_BAR_HEIGHT));
                icon.setMaximumSize(icon.getPreferredSize());
                icon.setMinimumSize(icon.getPreferredSize());
            }
            this.button.add(icon);

            this.button.add(Box.createHorizontalStrut(IFileViewer.TAB_BUTTON_PADDING_B));

            final ILabel label = new ILabel(this.node.getFileType().getFullName());
            {
                label.setPreferredSize(new Dimension(label.getLabelWidth(), Integer.MAX_VALUE));
                label.setMaximumSize(label.getPreferredSize());
                label.setMinimumSize(label.getPreferredSize());

                label.setPadding(0, 0, 2, 0);
            }
            this.button.add(label);

            this.button.add(Box.createHorizontalStrut(IFileViewer.TAB_BUTTON_PADDING_C));

            this.button.add(new ITabCloseButton(this));

            this.button.add(Box.createHorizontalStrut(IFileViewer.TAB_BUTTON_PADDING_D));

            this.button.revalidate();
            this.button.repaint();
        }
    }

    private class ITabCloseButton extends IComponent {

        private final ViewerTab tab;

        private BufferedImage normal, hover;
        private boolean hovered = false;

        private ITabCloseButton(final ViewerTab tab) {
            this.tab = tab;

            super.setOpaque(false);
            super.setPreferredSize(new Dimension(IFileViewer.TAB_BUTTON_CLOSE_SIZE, IFileViewer.TAB_BUTTON_CLOSE_SIZE));
            super.setMaximumSize(super.getPreferredSize());
            super.setMinimumSize(super.getPreferredSize());

            AssetManager.loadImage(AssetManager.CLOSE_NORMAL_ICON, new AsyncEvent<BufferedImage>() {
                @Override
                public void onComplete(final BufferedImage item) {
                    ITabCloseButton.this.normal = item;
                    ITabCloseButton.this.repaint();
                }
            });
            AssetManager.loadImage(AssetManager.CLOSE_HOVER_ICON, new AsyncEvent<BufferedImage>() {
                @Override
                public void onComplete(final BufferedImage item) {
                    ITabCloseButton.this.hover = item;
                    ITabCloseButton.this.repaint();
                }
            });
            super.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(final MouseEvent e) {
                    ITabCloseButton.this.hovered = true;
                    ITabCloseButton.this.repaint();
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    ITabCloseButton.this.hovered = false;
                    ITabCloseButton.this.repaint();
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                    ITabCloseButton.this.tab.close();
                }
            });
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);

            if(this.hovered && this.hover != null) {
                g.drawImage(this.hover, super.getWidth() / 2 - this.hover.getWidth() / 2,
                        super.getHeight() / 2 - this.hover.getHeight() / 2, null);
            } else if(this.normal != null) {
                g.drawImage(this.normal, super.getWidth() / 2 - this.normal.getWidth() / 2,
                        super.getHeight() / 2 - this.normal.getHeight() / 2, null);
            }
        }
    }
}
