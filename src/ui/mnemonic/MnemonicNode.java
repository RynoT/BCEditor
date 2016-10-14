package ui.mnemonic;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class MnemonicNode {

    private final Set<MnemonicKey> mnemonics = new HashSet<>();

    public MnemonicNode(){
    }

    Set<MnemonicKey> getMnemonics(){
        return this.mnemonics;
    }

    public MnemonicKey get(final int key){
        for(final MnemonicKey mnemonicKey : this.mnemonics){
            if(mnemonicKey.getKey() == key){
                return mnemonicKey;
            }
        }
        return null;
    }

    public void register(final MnemonicKey key){
        this.mnemonics.add(key);
    }

    public void unregister(final MnemonicKey key){
        this.mnemonics.remove(key);
    }
}
