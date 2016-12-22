package project;

import project.filetype.FileType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ryan Thomson on 16/12/2016.
 */
public abstract class Project {

    protected final String name, path;
    protected Set<FileType> files = new HashSet<>();

    protected Project(final String path) {
        assert (new File(path).exists());
        this.path = path.replaceAll("\\\\", "/");
        if(path.contains(".")){
            final int idx = this.path.lastIndexOf('/');
            this.name = this.path.substring(idx + 1);
        } else {
            assert(this.path.endsWith("/"));
            this.name = this.path.substring(0, this.path.length() - 2).replaceFirst("(?i)(.*)+/", "");
        }
    }

    public abstract void unload();

    public abstract boolean load() throws IOException;

    public abstract InputStream getStream(final String path);

    public final InputStream getStream(final FileType file){
        return this.getStream(file.getPath() + file.getName() + "." + file.getExtension());
    }

    public final String getName(){
        return this.name;
    }

    public final String getPath() {
        return this.path;
    }

    public final boolean isLoaded() {
        return this.files.size() != 0;
    }

    public final Set<FileType> getFiles(){
        return this.files;
    }
}
