package project.property;

import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.constantpool.ConstantPool;

/**
 * Created by Ryan Thomson on 05/01/2017.
 */
public class PAccessFlags extends Property {

    public static final String NAME = ".AccessFlags";

    private final int accessFlags;
    private String accessFlagsString = null;

    private final AccessFlags.Type type;

    public PAccessFlags(final int accessFlags, final AccessFlags.Type type) {
        this.accessFlags = accessFlags;
        this.type = type;
    }

    @Override
    public String getContentString(final ConstantPool pool) {
        final StringBuilder binary = new StringBuilder(Short.SIZE + 4);
        {
            //format binary string with padding
            final String str = Integer.toBinaryString(this.accessFlags);
            for(int i = Short.SIZE; i > 0; i--){
                if(i != Short.SIZE && i % 4 == 0){
                    binary.append(' ');
                }
                if(i > str.length()){
                    binary.append('0');
                } else {
                    binary.append(str.charAt(i - 1));
                }
            }
        }
        if(this.accessFlagsString == null) {
            this.accessFlagsString = AccessFlags.decode(this.accessFlags, this.type);
        }
        return PAccessFlags.NAME + "[" + binary + ", " + this.accessFlagsString + "]";
    }
}
