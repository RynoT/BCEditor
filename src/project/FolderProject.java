package project;

import java.io.*;

/**
 * Created by Ryan Thomson on 26/02/2017.
 */
public class FolderProject extends Project {

    public FolderProject(final String path) {
        super(path);
    }

    @Override
    public synchronized void unload() {
        super.files.clear();
    }

    @Override
    public synchronized boolean load() throws IOException {
        return true;
    }

    @Override
    public synchronized InputStream getStream(final String path) {
        final File file = new File(path);
        if(!file.exists()) {
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch(final FileNotFoundException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }
}
