package ui.mnemonic;

import ui.Canvas;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class MnemonicManager implements KeyEventDispatcher {

    private static final MnemonicManager instance = new MnemonicManager();

    private final MnemonicNode root = new MnemonicNode();

    public MnemonicManager(){
    }

    public static MnemonicManager getManager(){
        return MnemonicManager.instance;
    }

    public MnemonicNode getRootNode(){
        return this.root;
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent event) {
        if(event.getID() == KeyEvent.KEY_RELEASED && event.isAltDown()){
            final ui.Canvas canvas = Canvas.getCanvas();
            MnemonicKey key;
            for(int i = canvas.getActiveMenuCount() - 1; i >= 0; i--){
                key = canvas.getActiveMenu(i).getMnemonicNode().get(event.getKeyCode());
                if(key != null){
                    key.runKey();
                    return false;
                }
            }
            key = this.root.get(event.getKeyCode());
            if(key != null){
                key.runKey();
            }
        }
        return false;
    }
}
