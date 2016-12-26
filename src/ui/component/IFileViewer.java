package ui.component;

import ui.component.explorer.IFileNode;
import util.AssetManager;
import util.async.AsyncEvent;

import javax.swing.*;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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

    public void setActiveTab(final ViewerTab tab){
        assert(tab != null && this.tabs.contains(tab));
        if(this.active == tab){
            return;
        }
        if(this.active != null){
            this.active.setActive(false);
        }
        this.active = tab;
        tab.setActive(true);
    }

    public void display(final IFileNode node) {
        final ViewerTab tab = new ViewerTab(node, this.tabs.size());
        this.tabs.add(tab);

        // Automatically activate the new tab
        this.setActiveTab(tab);
        this.tabBar.add(tab.button);

        super.revalidate();
        super.repaint();
    }

    public class ViewerTab {

        private final IFileNode node;

        private final JPanel button;

        private int index;
        private boolean active = false;

        private ViewerTab(final IFileNode node, final int index) {
            this.node = node;
            this.index = index;

            this.button = new JPanel(){
                @Override
                protected void paintComponent(final Graphics g) {
                    super.paintComponent(g);

                    final int index = ViewerTab.this.index;
                    final int tabCount = IFileViewer.this.tabBar.getComponentCount();

                    g.setColor(IComponent.DEFAULT_HIGHLIGHT_DARK);
                    //Draw north
                    g.drawLine(0, 0, super.getWidth() - 1, 0);

                    //Draw east
                    if(index == tabCount - 1){
                        g.drawLine(super.getWidth() - 1, 0, super.getWidth() - 1, super.getHeight() - 1);
                    }

                    //Draw south
                    if(!ViewerTab.this.active){
                        g.drawLine(0, super.getHeight() - 1, super.getWidth() - 1, super.getHeight() - 1);
                    }

                    //Draw west
                    if(index > 0){
                        g.drawLine(0, 0, 0, super.getHeight() - 1);
                    }
                }
            };
            this.button.setBackground(IFileViewer.TAB_BUTTON_UNFOCUSED_COLOR);
            this.button.setLayout(new BoxLayout(this.button, BoxLayout.X_AXIS));
            this.button.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(final MouseEvent e) {
                    if(!ViewerTab.this.active){
                        IFileViewer.this.setActiveTab(ViewerTab.this);
                    }
                }
            });

            this.updateButton();
        }

        public void setIndex(final int index){
            this.index = index;
        }

        public void setActive(final boolean active){
            this.active = active;
            this.button.setBackground(active ? IProjectExplorer.DEFAULT_BACKGROUND_HIGHLIGHT
                    : IFileViewer.TAB_BUTTON_UNFOCUSED_COLOR);
            this.button.repaint();
        }

        public void close(){
            System.out.println("closing tab");
        }

        public void updateButton() {
            this.button.removeAll();

            this.button.add(Box.createHorizontalStrut(IFileViewer.TAB_BUTTON_PADDING_A));

            final IImagePanel icon = new IImagePanel(this.node.getIconPanel().getImages());
            {
                icon.setPreferredSize(new Dimension(IFileViewer.TAB_BAR_HEIGHT, IFileViewer.TAB_BAR_HEIGHT));
                icon.setMaximumSize(icon.getPreferredSize());
            }
            this.button.add(icon);

            this.button.add(Box.createHorizontalStrut(IFileViewer.TAB_BUTTON_PADDING_B));

            final ILabel label = new ILabel(this.node.getFileType().getFullName());
            {
                label.setPreferredSize(new Dimension(label.getLabelWidth(), Integer.MAX_VALUE));
                label.setMaximumSize(label.getPreferredSize());

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
