package project.filetype.classtype.index;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagClass;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class InterfaceIndex {

    private final String className;

    InterfaceIndex(final int _interface, final ConstantPool pool){
        this.className = ((TagClass) pool.getEntry(_interface)).getTagName(pool).getValue();
    }

    public String getClassName(){
        return this.className;
    }
}
