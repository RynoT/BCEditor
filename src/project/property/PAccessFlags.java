package project.property;

import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.constantpool.ConstantPool;

/**
 * Created by Ryan Thomson on 05/01/2017.
 */
public class PAccessFlags extends Property {

    public static final String NAME = ".AccessFlags";

    private final int accessFlags;

    private final AccessFlags.Type type;

    public PAccessFlags(final int accessFlags, final AccessFlags.Type type) {
        this.accessFlags = accessFlags;
        this.type = type;
    }

    @Override
    public Property[] getChildProperties() {
        return new Property[0];
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        final StringBuilder binary = new StringBuilder(Short.SIZE + 4);
        {
            //format binary string with padding
            final String str = Integer.toBinaryString(this.accessFlags);
            for(int i = 0; i < Short.SIZE; i++){
                if(i != 0 && i % 4 == 0){
                    binary.append(' ');
                }
                if(Short.SIZE - i <= str.length()){
                    binary.append(str.charAt(str.length() - (Short.SIZE - i)));
                } else {
                    binary.append('0');
                }
            }
        }
        return PAccessFlags.NAME + "[" + binary + "]";
    }
}
