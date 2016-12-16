package ui.component;

import ui.mnemonic.MnemonicNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class IMenu extends JDialog {

    private final JPanel itemPanel;
    private final MnemonicNode mnemonicNode = new MnemonicNode();

    public IMenu(final Component parent){
        super.setUndecorated(true);
        super.setBackground(IComponent.DEFAULT_BACKGROUND);

        final JPanel itemPanel = new JPanel();
        {
            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        }
        super.setContentPane(this.itemPanel = itemPanel);

        final Point location = parent.getLocationOnScreen();
        super.setLocation(location.x, location.y + parent.getHeight());

        super.setVisible(true);

        super.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {
                ui.Canvas.getCanvas().popActiveMenu();
                IMenu.super.dispose();
            }
        });
    }

    public MnemonicNode getMnemonicNode(){
        return this.mnemonicNode;
    }

    public void addItem(final IMenuItem item){
        this.addItem((IComponent)item);
    }

    public void addItem(final ISeparator item){
        this.addItem((IComponent)item);
    }

    private void addItem(final IComponent item){
        this.itemPanel.add(item);

        final Dimension size = item.getPreferredSize();
        super.setSize(Math.max(super.getWidth(), size.width), super.getHeight() + size.height);
    }
}
