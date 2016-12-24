package project.filetype;

/**
 * Created by Ryan Thomson on 16/12/2016.
 */
public class ImageType extends FileType {

    public static final String EXTENSION_REGEX = "(?i)|(png)|(jp[e]g)|(gif)|([w]bmp)";

    public ImageType(final String name, final String extension, final String path) {
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
