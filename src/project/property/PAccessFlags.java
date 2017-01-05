package project.property;

import project.filetype.classtype.AccessFlags;

/**
 * Created by Ryan Thomson on 05/01/2017.
 */
public class PAccessFlags extends Property {

    public static final String NAME = "AccessFlags";

    private int accessFlags;
    private String accessFlagsString = null;

    private final AccessFlags.Type type;

    public PAccessFlags(final int accessFlags, final AccessFlags.Type type) {
        this.accessFlags = accessFlags;
        this.type = type;
    }

    public int getAccessFlags() {
        return this.accessFlags;
    }

    public String getAccessFlagsString() {
        if(this.accessFlagsString == null) {
            this.accessFlagsString = AccessFlags.decode(this.accessFlags, this.type);
        }
        return this.accessFlagsString;
    }

    @Override
    public String getContentString() {
        return PAccessFlags.NAME + "(" + this.getAccessFlagsString() + ", " + this.getAccessFlags() + ")";
    }
}
