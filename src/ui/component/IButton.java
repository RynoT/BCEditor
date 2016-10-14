package ui.component;

import ui.component.event.IActionEvent;
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

    public IButton(final String text){
        this(text, -1);
    }

    public IButton(final String text, final int mnemonic){
        this(text, mnemonic, null);
    }

    public IButton(final String text, final BufferedImage icon){
        this(text, -1, icon);
    }

    public IButton(final String text, final int mnemonic, final BufferedImage icon){
        this.label = new ILabel(text, icon);

        super.setBackground(Color.BLUE);

        super.setLayout(new BorderLayout(0, 0));
        super.add(this.label, BorderLayout.CENTER);
        super.addMouseListener(new IButtonMouseListener());

        this.setDimensions();
        if(mnemonic != -1){
            this.setMnemonic(mnemonic);
        }
    }

    public ILabel getInternalLabel(){
        return this.label;
    }

    public void setMnemonic(final int key){
        this.label.setMnemonic(key);
        Component parent = super.getParent();
        while(parent != null){
            if(parent instanceof IMenu){
                break;
            }
            parent = parent.getParent();
        }
        final MnemonicNode node;
        if(parent != null){
            node = ((IMenu) parent).getMnemonicNode();
        } else {
            node = MnemonicManager.getManager().getRootNode();
        }
        if(this.mnemonic != null){
            node.unregister(this.mnemonic);
        }
        if(key != -1){
            System.out.println(1);
            node.register(this.mnemonic = new MnemonicKey(key, this));
        } else {
            this.mnemonic = null;
        }
    }

    @Override
    protected void setDimensions() {
        super.setPreferredSize(new Dimension(this.label.getLabelWidth() + IButton.PADDING_WIDTH, this.label.getLabelHeight()));
    }

    private class IButtonMouseListener extends MouseAdapter {

        private boolean pressed = false, hovered = false;

        @Override
        public void mousePressed(final MouseEvent event) {
            this.pressed = true;
            System.out.println(1);
        }

        @Override
        public void mouseReleased(final MouseEvent event) {
            this.pressed = false;
            System.out.println(2);
        }

        @Override
        public void mouseEntered(final MouseEvent event) {
            this.hovered = true;
            System.out.println(3);
        }

        @Override
        public void mouseExited(final MouseEvent event) {
            this.hovered = false;
            System.out.println(4);
        }
    }

}
