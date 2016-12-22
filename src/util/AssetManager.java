package util;

import util.async.Async;
import util.async.AsyncEvent;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by Ryan Thomson on 21/12/2016.
 */
public class AssetManager {

    public static final String RESOURCE_FOLDER = "/resource/";

    public static final String EXPANDED_ICON = AssetManager.RESOURCE_FOLDER + "expanded.png";
    public static final String COLLAPSED_ICON = AssetManager.RESOURCE_FOLDER + "collapsed.png";
    public static final String MENU_EXPAND_ICON = AssetManager.RESOURCE_FOLDER + "menu_expand.png";

    public static final String FOLDER_ICON = AssetManager.RESOURCE_FOLDER + "folder.png";
    public static final String CLASS_ICON = AssetManager.RESOURCE_FOLDER + "class.png";
    public static final String ENUM_ICON = AssetManager.RESOURCE_FOLDER + "enum.png";
    public static final String INTERFACE_ICON = AssetManager.RESOURCE_FOLDER + "interface.png";
    public static final String TEXT_ICON = AssetManager.RESOURCE_FOLDER + "text.png";
    public static final String IMAGE_ICON = AssetManager.RESOURCE_FOLDER + "image.png";
    public static final String UNKNOWN_ICON = AssetManager.RESOURCE_FOLDER + "unknown.png";

    public static final String ABSTRACT_MOD_ICON = AssetManager.RESOURCE_FOLDER + "mod_abstract.png";
    public static final String FINAL_MOD_ICON = AssetManager.RESOURCE_FOLDER + "mod_final.png";
    public static final String MAIN_MOD_ICON = AssetManager.RESOURCE_FOLDER + "mod_main.png";

    private static final AssetManager instance = new AssetManager();

    private final Map<String, BufferedImage> assets = new WeakHashMap<>();
    private final Map<String, Set<AsyncEvent<BufferedImage>>> pending = new HashMap<>();

    private AssetManager(){
    }

    public static void clearCache(){
        synchronized(AssetManager.class) {
            AssetManager.instance.assets.clear();
            AssetManager.instance.pending.clear();
        }
    }

    // Loads an image using a cache. Cache items remain in memory until cleared manually (although the JVM may automatically discard some assets over time if not used).
    public static void loadImage(final String path, final AsyncEvent<BufferedImage> event){
        assert(event != null);
        if(path == null){
            return;
        }

        final AssetManager instance = AssetManager.instance;
        synchronized(AssetManager.class){
            // Check to see if asset is already loaded
            if(instance.assets.containsKey(path)){
                // Return the pre-loaded asset if we have it. This does not happen async (it happens during method call).
                event.onComplete(instance.assets.get(path));
                return;
            }
            // Check to see if the asset is currently being loaded by someone else
            if(instance.pending.containsKey(path)){
                // If it is, just let them know that we want it to
                instance.pending.get(path).add(event);
            } else {
                // We are the first to request this asset. Lets let the application know that we're loading it
                final HashSet<AsyncEvent<BufferedImage>> events = new HashSet<>();
                {
                    events.add(event);
                }
                instance.pending.put(path, events);

                // Load the asset async. This only happens once per asset
                Async.loadImage(path, new AsyncEvent<BufferedImage>() {
                    @Override
                    public void onComplete(final BufferedImage image) {
                        final AssetManager instance = AssetManager.instance;
                        synchronized(AssetManager.class){
                            assert(!instance.assets.containsKey(path));
                            assert(instance.pending.containsKey(path));

                            // Put the asset into a cache so that we can easily access it later
                            instance.assets.put(path, image);
                            // Loop through all the pending requests for this asset (including our own) and let them know we loaded it
                            for(final AsyncEvent<BufferedImage> event : instance.pending.get(path)){
                                event.onComplete(image);
                            }
                            // Let the application know that we are no longer loading this asset, and all the pending requests have been fulfilled
                            instance.pending.remove(path);
                        }
                    }
                });
            }
        }
    }
}
