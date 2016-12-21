package util.async;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public class Async {

    private static Async async = new Async();

    private boolean shutdown = false;

    // Our single-threaded ExecutorService will use a single thread for async tasks (to be used when execution order is important)
    private final ExecutorService single = Executors.newSingleThreadExecutor();

    // Our multi-threaded ExecutorService will use all of the available processors minus 1 for async tasks
    private final ExecutorService multi = Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));

    private Async() {
    }

    public static void shutdown() {
        assert (!Async.async.shutdown);
        Async.async.shutdown = true;
        Async.async.single.shutdown();
        Async.async.multi.shutdown();
    }

    public static void submit(final Runnable runnable, final AsyncType type) {
        Async.async.launch(runnable, type);
    }

    public static <T> Future<T> submit(final Callable<T> callable, final AsyncType type) {
        return Async.async.launch(callable, type);
    }

    public static void loadImage(final String path, final AsyncEvent<BufferedImage> event) {
        Async.loadImage(path, event, AsyncType.MULTI);
    }

    public static void loadImage(final String path, final AsyncEvent<BufferedImage> event, final AsyncType type) {
        assert (event != null);
        Async.async.launch(() -> {
            BufferedImage image;
            try {
                //image = ImageIO.read(new File(file));

                final URL url = Async.class.getResource(path);
                assert (url != null);
                image = ImageIO.read(url);
            } catch(final IOException e) {
                image = null;
                e.printStackTrace(System.err);
            }
            event.onComplete(image);
        }, type);
    }

    private <T> Future<T> launch(final Callable<T> callable, final AsyncType type) {
        assert (callable != null);
        switch(type) {
            case SINGLE:
                return this.single.submit(callable);
            case MULTI:
                return this.multi.submit(callable);
            default:
                assert (false);
        }
        return null;
    }

    private void launch(final Runnable runnable, final AsyncType type) {
        assert (runnable != null);
        switch(type) {
            case SINGLE:
                this.single.execute(runnable);
                break;
            case MULTI:
                this.multi.execute(runnable);
                break;
            default:
                assert (false);
        }
    }
}
