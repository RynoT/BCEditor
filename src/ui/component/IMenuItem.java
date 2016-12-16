package ui.component;

import util.async.Async;
import util.async.AsyncEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Ryan Thomson on 15/12/2016.
 */
public class IMenuItem extends IComponent {

    public static final int ITEM_HEIGHT = 23;
    public static final int TEXT_TO_SHORTCUT_SEPARATION = 10;
    public static final int ITEM_MIN_WIDTH = 150;

    private final IButton button;
    private final boolean expandable;

    public IMenuItem(final String text) {
        this(text, false);
    }

    public IMenuItem(final String text, final boolean expandable) {
        this(text, null, expandable);
    }

    public IMenuItem(final String text, final String iconPath) {
        this(text, iconPath, false);
    }

    public IMenuItem(final String text, final String iconPath, final boolean expandable) {
        this.expandable = expandable;
        this.button = new IButton(text);
        this.button.getInternalLabel().setAlignment(ITextAlign.LEFT);
        if(!expandable){
            this.button.addEvent(() -> super.getParent().dispatchEvent(new FocusEvent(this, FocusEvent.FOCUS_LOST)));
        }

        super.setBackground(IComponent.DEFAULT_BACKGROUND);
        super.setLayout(new BorderLayout(0, 0));
        super.setBorder(new IBorder(1, 1, 1, 1));

        final int width = Math.max(IMenuItem.ITEM_MIN_WIDTH, IMenuItem.ITEM_HEIGHT + this.button
                .getInternalLabel().getLabelWidth() + IMenuItem.TEXT_TO_SHORTCUT_SEPARATION + IMenuItem.ITEM_HEIGHT);
        super.setPreferredSize(new Dimension(width, IMenuItem.ITEM_HEIGHT));

        final JPanel iconPanel = new IMenuItemIconPanel(iconPath);
        super.add(iconPanel, BorderLayout.WEST);

        super.add(this.button, BorderLayout.CENTER);

        final JPanel eastPanel = new JPanel();
        {
            eastPanel.setOpaque(false);
            eastPanel.setPreferredSize(new Dimension(IMenuItem.ITEM_HEIGHT, IMenuItem.ITEM_HEIGHT));
        }
        super.add(eastPanel, BorderLayout.EAST);

        // Copy the background color of the button to the icon and east panel
        final MouseAdapter backgroundCopy = new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                this.copyBackground();
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                this.copyBackground();
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
                this.copyBackground();
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                this.copyBackground();
            }

            private void copyBackground(){
                IMenuItem.super.setBackground(button.getBackground());
            }
        };
        this.button.addMouseListener(backgroundCopy);

        final MouseAdapter eventPass = new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                this.pass(e);
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                this.pass(e);
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
                this.pass(e);
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                this.pass(e);
            }

            private void pass(final MouseEvent e){
                button.dispatchEvent(e);
            }
        };
        iconPanel.addMouseListener(eventPass);
        eastPanel.addMouseListener(eventPass);
    }

    public IButton getInternalButton() {
        return this.button;
    }

    public void setMnemonic(final int key) {
        this.button.setMnemonic(key);
    }

    private class IMenuItemIconPanel extends JPanel {

        private BufferedImage icon = null;

        private IMenuItemIconPanel(final String iconPath) {
            super.setOpaque(false);
            super.setPreferredSize(new Dimension(IMenuItem.ITEM_HEIGHT, IMenuItem.ITEM_HEIGHT));

            if(iconPath != null) { //there's nothing to load if we have no path, don't put pointless cpu-wastes in the queue
                Async.loadImage(new File(iconPath), new AsyncEvent<BufferedImage>() {
                    @Override
                    public void onComplete(final BufferedImage item) {
                        icon = item;
                        repaint();
                    }
                });
            }
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);

            if(this.icon != null) {
                g.drawImage(this.icon, super.getWidth() / 2 - this.icon.getWidth() / 2,
                        super.getHeight() / 2 - this.icon.getHeight() / 2, null);
            }
        }
    }
}
