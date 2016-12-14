package ui.component;

import ui.mnemonic.MnemonicKey;
import ui.mnemonic.MnemonicManager;
import ui.mnemonic.MnemonicNode;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class IButton extends IActionComponent {

    public static final int PADDING_WIDTH = 10;

    private ILabel label;
    private MnemonicKey mnemonic = null;
    private IOrientation orientation = IOrientation.NORTH;

    private boolean selected = false;
    private boolean toggleButton = false;
    private boolean restrictHeight = true;

    private Color defaultColor, pressedColor, hoveredColor, selectedColor;

    public IButton(final String text) {
        this(text, -1);
    }

    public IButton(final String text, final int mnemonic) {
        this(text, mnemonic, null);
    }

    public IButton(final String text, final BufferedImage icon) {
        this(text, -1, icon);
    }

    public IButton(final String text, final int mnemonic, final BufferedImage icon) {
        this.label = new ILabel(text, icon);

        this.defaultColor = IComponent.DEFAULT_BACKGROUND;
        this.pressedColor = IComponent.DEFAULT_PRESSED;
        this.hoveredColor = IComponent.DEFAULT_HOVERED;
        this.selectedColor = IComponent.DEFAULT_SELECTED;

        super.setBackground(this.defaultColor);

        super.setLayout(new BorderLayout(0, 0));
        super.add(this.label, BorderLayout.CENTER);
        super.addMouseListener(new IButtonMouseListener());

        this.setDimensions();
        if(mnemonic != -1) {
            this.setMnemonic(mnemonic);
        }
    }

    // returns true if this button is a toggle button and it is currently down
    public boolean isSelected(){
        return this.selected;
    }

    public boolean isToggleButton() {
        return this.toggleButton;
    }

    public ILabel getInternalLabel() {
        return this.label;
    }

    public void setToggle(final boolean toggle) {
        this.selected = false;
        this.toggleButton = toggle;
    }

    public void setRestrictHeight(final boolean restrict){
        if(this.restrictHeight == restrict){
            return;
        }
        this.restrictHeight = restrict;
        super.updateDimensions();
    }

    public void setOrientation(final IOrientation orientation){
        this.setOrientation(orientation, this.restrictHeight);
    }

    public void setOrientation(final IOrientation orientation, final boolean restrictHeight){
        if(this.orientation == orientation){
            return;
        }
        this.orientation = orientation;
        this.restrictHeight = restrictHeight;
        this.label.setOrientation(orientation);

        super.updateDimensions();
    }

    public void setMnemonic(final int key) {
        this.label.setMnemonic(key);
        Component parent = super.getParent();
        while(parent != null) {
            if(parent instanceof IMenu) {
                break;
            }
            parent = parent.getParent();
        }
        final MnemonicNode node;
        if(parent != null) {
            node = ((IMenu) parent).getMnemonicNode();
        } else {
            node = MnemonicManager.getManager().getRootNode();
        }
        if(this.mnemonic != null) {
            node.unregister(this.mnemonic);
        }
        if(key != -1) {
            node.register(this.mnemonic = new MnemonicKey(key, this));
        } else {
            this.mnemonic = null;
        }
    }

    @Override
    protected void setDimensions() {
        final int width = this.label.getLabelWidth() + IButton.PADDING_WIDTH, height = this.label.getLabelHeight();
        if(this.orientation.isVertical()){
            super.setPreferredSize(new Dimension(width, this.restrictHeight ? height : Integer.MAX_VALUE));
        } else {
            super.setPreferredSize(new Dimension(this.restrictHeight ? height : Integer.MAX_VALUE, width));
        }
        super.setMaximumSize(super.getPreferredSize());
    }

    private class IButtonMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(final MouseEvent event) {
            IButton.super.setPressed(true);
            IButton.super.setBackground(IButton.this.pressedColor);
        }

        @Override
        public void mouseReleased(final MouseEvent event) {
            if(IButton.super.isHovered()){
                if(IButton.this.toggleButton) {
                    IButton.this.selected = !IButton.this.selected;
                }
                if(IButton.this.selected){
                    IButton.super.setBackground(IButton.this.selectedColor);
                } else {
                    IButton.super.setBackground(IButton.this.hoveredColor);
                }
                //if(IButton.super.isPressed() && (!IButton.this.toggleButton || IButton.this.selected)){
                    IButton.super.runEvents(); //run events on press and release
               // }
            } else {
                if(IButton.this.selected){
                    IButton.super.setBackground(IButton.this.selectedColor);
                } else {
                    IButton.super.setBackground(IButton.this.defaultColor);
                }
            }
            IButton.super.setPressed(false);
        }

        @Override
        public void mouseEntered(final MouseEvent event) {
            IButton.super.setHovered(true);
            if(!IButton.super.isPressed() && !IButton.this.selected){
                IButton.super.setBackground(IButton.this.hoveredColor);
            }
        }

        @Override
        public void mouseExited(final MouseEvent event) {
            IButton.super.setHovered(false);
            if(!IButton.super.isPressed() && !IButton.this.selected){
                IButton.super.setBackground(IButton.this.defaultColor);
            }
        }
    }

}
