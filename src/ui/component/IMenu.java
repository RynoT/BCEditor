package ui.component;

import jdk.nashorn.internal.scripts.JD;
import ui.mnemonic.MnemonicNode;

import javax.swing.*;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class IMenu extends JDialog {

    private final MnemonicNode mnemonicNode = new MnemonicNode();

    public MnemonicNode getMnemonicNode(){
        return this.mnemonicNode;
    }
}
