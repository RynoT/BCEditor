package project.filetype.classtype.constantpool;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public abstract class PoolTag {

    public static final int TAG_UTF8 = 1;
    public static final int TAG_INTEGER = 3;
    public static final int TAG_FLOAT = 4;
    public static final int TAG_LONG = 5;
    public static final int TAG_DOUBLE = 6;
    public static final int TAG_CLASS = 7;
    public static final int TAG_STRING = 8;
    public static final int TAG_FIELD_REF = 9;
    public static final int TAG_METHOD_REF = 10;
    public static final int TAG_IM_REF = 11;
    public static final int TAG_NAME_AND_TYPE = 12;
    public static final int TAG_METHOD_HANDLE = 15;
    public static final int TAG_METHOD_TYPE = 16;
    public static final int TAG_INVOKE_DYNAMIC = 18;

    protected PoolTag(){
    }

    public abstract int getTagId();

    public abstract String getContentString(final ConstantPool pool);
}
