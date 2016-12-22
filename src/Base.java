import ui.Canvas;
import ui.mnemonic.MnemonicManager;
import util.AssetManager;
import util.async.Async;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class Base {

    private static final boolean ESCAPE_TO_CLOSE = true;

    private Base() {
    }

    public static void main(final String[] args) {
        // safely setup the look and feel and show main display
        SwingUtilities.invokeLater(() -> {
            try {
                // we use the default system look and feel for our application
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch(final ClassNotFoundException | InstantiationException
                    | UnsupportedLookAndFeelException | IllegalAccessException e) {
                e.printStackTrace(System.err);
            }
            // initialize the Canvas by invoking it statically and then make it visible
            Canvas.getCanvas().setVisible(true);
        });
        // attach our mnemonic key manager to the application so that we can implement mnemonic shortcuts
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(MnemonicManager.getManager());
        // when the application closes, request that our Async threads are all stopped
        Runtime.getRuntime().addShutdownHook(new Thread(null, () -> {
            Async.shutdown();
            AssetManager.clearCache();
        }, "Shutdown"));

        // convenience shortcut for closing the application (for development purposes)
        if(Base.ESCAPE_TO_CLOSE) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
                return false;
            });
        }
    }
}
