package ui.mnemonic;

import ui.component.IActionComponent;
import ui.component.event.IActionEvent;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class MnemonicKey {

    private final int key;
    private final IActionComponent component;

    public MnemonicKey(final int key, final IActionComponent component){
        assert(key != -1 && component != null);
        this.key = key;
        this.component = component;
    }

    public int getKey(){
        return this.key;
    }

    public void runKey(){
        this.component.click();
    }
}
