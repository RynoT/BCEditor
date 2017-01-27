import java.awt.*;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Ryan Thomson on 28/12/2016.
 */
public abstract class Test extends HashMap<Image, Void> implements Runnable, Closeable {

    public abstract void something();

    public void euphoria(List<?> var, int i, float... kk) throws Exception {
        switch(i) {
            case 5:
                System.out.println(5);
                break;
            case 6:
                System.out.println(6);
                break;
            case 7:
                System.out.println(7);
                break;
        }
        System.out.println("Ok\" then");
    }

    public <T extends InputStream> void test(List<? super T> list, T set) throws IOException, InterruptedException {
    }

    public <T extends InputStream> void test2(List<? extends T> list, T set) {
    }

    public <De> void ryan(De what, int j) {
        final int x = 4 * j;
        final int y = 9 << x;
        System.out.println(y + x);
    }

    public <D extends InputStream, K> void turndownforwhat(D what) {
        for(int i = 0; i < 100; i++) {
            System.out.println(i);
        }
        for(int i = 0; i < 100; i++) {
            System.out.println(i);
        }
        for(int k = 0; k < 100; k++) {
            System.out.println(k);
        }
        for(int j = 0; j < 100; j++) {
            System.out.println(j);
        }
    }

    public <F extends InputStream, K> void speed(F list, K set) {
    }

    public <F extends List<F>> void kenzie(F list) {
    }

    public <F extends List<Set<F>>> void ralph(F list) {
    }

    public <F extends List<F> & Closeable, K> void lauren(F list) {
    }
}
