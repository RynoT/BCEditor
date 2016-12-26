package project;

import project.filetype.ClassType;
import project.filetype.FileType;
import ui.Canvas;
import ui.component.explorer.IFileNode;
import ui.component.explorer.ITileNode;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ryan Thomson on 16/12/2016.
 */
public abstract class Project {

    protected final String name, path;
    protected final Map<String, FileType> files = new HashMap<>();

    protected Project(final String path) {
        assert (new File(path).exists());
        this.path = path.replaceAll("\\\\", "/");
        if(path.contains(".")) {
            final int idx = this.path.lastIndexOf('/');
            this.name = this.path.substring(idx + 1);
        } else {
            assert (this.path.endsWith("/"));
            this.name = this.path.substring(0, this.path.length() - 2).replaceFirst("(?i)(.*)+/", "");
        }
    }

    public abstract void unload();

    public abstract boolean load() throws IOException;

    public abstract InputStream getStream(final String path);

    public final InputStream getStream(final FileType file) {
        return this.getStream(file.getFullPath());
    }

    public final String getName() {
        return this.name;
    }

    public final String getPath() {
        return this.path;
    }

    public final boolean isLoaded() {
        return this.files.size() != 0;
    }

    public final Map<String, FileType> getFiles() {
        return this.files;
    }

    public final void index() {
        System.out.println("[Project] Indexing project...");
        for(final FileType file : this.files.values()) {
            if(!(file instanceof ClassType)) {
                continue;
            }
            if(((ClassType) file).index()) {
                System.out.println("[Project]     Indexed class: " + file.getFullPath());
            } else {
                System.err.println("[Project]     An error occurred when indexing file: " + file.getFullPath());
            }
        }
        System.out.println("[Project]     Updating explorer tiles...");
        for(final ITileNode node : Canvas.getProjectExplorer().getRootNode()){
            if(node == null){
                continue;
            }
            assert (node instanceof IFileNode);
            if(node.isVisible()){
                // We only have to update the icons which are visible in the explorer
                ((IFileNode) node).updateIcon();
            }
            // We need to update the comment of every tile
            ((IFileNode) node).updateComment();
        }
        System.out.println("[Project] Indexing complete");
    }
}
