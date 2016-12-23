package project.filetype;

import ui.Canvas;

import java.io.InputStream;

/**
 * Created by Ryan Thomson on 16/12/2016.
 */
public abstract class FileType {

    private final String name, extension, path;

    // Example file: org/web/file/Test.class
    // Required input: name: "Test", extension: "class", path: "org/web/file/"
    protected FileType(final String name, final String extension, final String path) {
        this.name = name;
        this.extension = extension;
        this.path = path;
    }

    public abstract boolean load();

    public final String getName(){
        return this.name;
    }

    public final String getExtension(){
        return this.extension;
    }

    public final String getFullName(){
        return this.name + "." + this.extension;
    }

    public final String getPath(){
        return this.path;
    }

    public final String getFullPath(){
        return this.path + this.getFullName();
    }

    public final InputStream getStream(){
        return Canvas.getProjectExplorer().getProject().getStream(this);
    }

    public static FileType create(final String filePath) {
        final String extension;
        if(filePath.contains(".")) {
            extension = filePath.substring(filePath.lastIndexOf('.') + 1);
        } else {
            extension = "";
        }
        final String name;
        if(filePath.contains("/")){
            name = filePath.substring(filePath.lastIndexOf('/') + 1).replace("." + extension, "");
        } else {
            name = filePath.replace("." + extension, "");
        }
        final String path = filePath.replace(name + "." + extension, "");
        if(extension.matches(ClassType.EXTENSION_REGEX)){
            return new ClassType(name, extension, path);
        }
        if(extension.matches(TextType.EXTENSION_REGEX)){
            return new TextType(name, extension, path);
        }
        if(extension.matches(ImageType.EXTENSION_REGEX)){
            return new ImageType(name, extension, path);
        }
        System.out.println("[Project Load] Extension unknown: " + extension + ", for: " + filePath);
        return new BinaryType(name, extension, path);
    }
}
