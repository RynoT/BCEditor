package project.filetype;

/**
 * Created by Ryan Thomson on 16/12/2016.
 */
public class TextType extends FileType {

    public static final String EXTENSION_REGEX = "(?i)(txt)|(java)|(doc)|([ms]f)|(rsa)|([(ht)x]ml)|(md)|(log)";

    public TextType(final String name, final String extension, final String path){
        super(name, extension, path);
    }

    @Override
    public void unload() {
    }

    @Override
    public boolean load() {
        return false;
    }
}
