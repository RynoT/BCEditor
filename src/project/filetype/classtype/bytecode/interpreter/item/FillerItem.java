package project.filetype.classtype.bytecode.interpreter.item;

/**
 * Created by Ryan Thomson on 21/01/2017.
 */
public class FillerItem extends MethodItem {

    public FillerItem() {
        super(null);
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public PrimitiveType getType() {
        return null;
    }
}
