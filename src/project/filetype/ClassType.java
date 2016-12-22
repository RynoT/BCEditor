package project.filetype;

/**
 * Created by Ryan Thomson on 16/12/2016.
 */
public class ClassType extends FileType {

    public static final String EXTENSION_REGEX = "(?i)class";

    public ClassType(final String name, final String extension, final String path){
        super(name, extension, path);
    }
}
