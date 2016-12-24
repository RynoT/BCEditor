package project;

import project.filetype.FileType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Ryan Thomson on 16/12/2016.
 */
public class ZipProject extends Project {

    private ZipFile zipFile = null;

    public ZipProject(final String path) {
        super(path);
    }

    @Override
    public synchronized void unload() {
        if(this.zipFile != null) {
            try {
                this.zipFile.close();
            } catch(final IOException e) {
                e.printStackTrace(System.err);
            }
            this.zipFile = null;
        }
        super.files.clear();

        System.out.println("[ZipProject] Project unloaded (" + this.name + ")");
    }

    @Override
    public synchronized boolean load() throws IOException {
        if(this.zipFile != null){
            this.unload();
        }
        assert(super.files.size() == 0);
        this.zipFile = new ZipFile(super.path);

        final Enumeration<? extends ZipEntry> enumeration = this.zipFile.entries();
        while(enumeration.hasMoreElements()) {
            final String name = enumeration.nextElement().getName();
            if(name.endsWith("/")) { //Don't add folders, our hierarchy algorithm will take care of folders
                continue;
            }
            super.files.put(name, FileType.create(name));
        }
        if(super.isLoaded()){
            System.out.println("[ZipProject] Project loaded successfully (" + this.name + ")");
        }
        return super.isLoaded();
    }

    @Override
    public synchronized InputStream getStream(final String path) {
        final ZipEntry entry = this.zipFile.getEntry(path);
        if(entry != null) {
            try {
                return this.zipFile.getInputStream(entry);
            } catch(final IOException e) {
                e.printStackTrace(System.err);
            }
        }
        return null;
    }
}
