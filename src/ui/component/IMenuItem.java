package ui.component;

import util.AssetManager;
import util.async.AsyncEvent;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Created by Ryan Thomson on 15/12/2016.
 */
public class IMenuItem extends IComponent {

    public static final int ITEM_SIZE = 23; //height
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

        final int width = Math.max(IMenuItem.ITEM_MIN_WIDTH, IMenuItem.ITEM_SIZE + this.button
                .getInternalLabel().getLabelWidth() + IMenuItem.TEXT_TO_SHORTCUT_SEPARATION + IMenuItem.ITEM_SIZE);
        super.setPreferredSize(new Dimension(width, IMenuItem.ITEM_SIZE));

        final IImagePanel westPanel = new IImagePanel();
        {
            westPanel.setPreferredSize(new Dimension(IMenuItem.ITEM_SIZE, IMenuItem.ITEM_SIZE));

            if(iconPath != null){
                AssetManager.loadImage(iconPath, new AsyncEvent<BufferedImage>() {
                    @Override
                    public void onComplete(final BufferedImage image) {
                        westPanel.setImage(image);
                    }
                });
            }
        }
        super.add(westPanel, BorderLayout.WEST);

        super.add(this.button, BorderLayout.CENTER);

        final IImagePanel eastPanel = new IImagePanel();
        {
            eastPanel.setPreferredSize(new Dimension(IMenuItem.ITEM_SIZE, IMenuItem.ITEM_SIZE));
        }
        super.add(eastPanel, BorderLayout.EAST);

        // Copy the background color of the button to the icon and east panel
        this.button.addMouseListener(this.getCopyAdapter());

        final MouseAdapter eventPass = this.getPassAdapter();
        westPanel.addMouseListener(eventPass);
        eastPanel.addMouseListener(eventPass);
    }

    public IButton getInternalButton() {
        return this.button;
    }

    public void setMnemonic(final int key) {
        this.button.setMnemonic(key);
    }

    private MouseAdapter getCopyAdapter(){
        return new MouseAdapter() {
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
                IMenuItem.super.setBackground(IMenuItem.this.button.getBackground());
            }
        };
    }

    private MouseAdapter getPassAdapter(){
        return new MouseAdapter() {
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
                IMenuItem.this.button.dispatchEvent(e);
            }
        };
    }
}
