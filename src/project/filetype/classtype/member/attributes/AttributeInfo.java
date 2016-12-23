package project.filetype.classtype.member.attributes;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagUTF8;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public abstract class AttributeInfo {

    public static final String CONSTANT_VALUE = "ConstantValue";
    public static final String CODE = "Code";
    public static final String STACK_MAP_TABLE = "StackMapTable";
    public static final String EXCEPTIONS = "Exceptions";
    public static final String INNER_CLASSES = "InnerClasses";
    public static final String ENCLOSING_METHOD = "EnclosingMethod";
    public static final String SYNTHETIC = "Synthetic";
    public static final String SIGNATURE = "Signature";
    public static final String SOURCE_FILE = "SourceFile";
    public static final String SOURCE_DEBUG_EXTENSION = "SourceDebugExtension";
    public static final String LINE_NUMBER_TABLE = "LineNumberTable";
    public static final String LOCAL_VARIABLE_TABLE = "LocalVariableTable";
    public static final String LOCAL_VARIABLE_TYPE_TABLE = "LocalVariableTypeTable";
    public static final String DEPRECATED = "Deprecated";
    public static final String RUNTIME_VISIBLE_ANNOTATIONS = "RuntimeVisibleAnnotations";
    public static final String RUNTIME_INVISIBLE_ANNOTATIONS = "RuntimeInvisibleAnnotations";
    public static final String RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS = "RuntimeVisibleParameterAnnotations";
    public static final String RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS = "RuntimeInvisibleParameterAnnotations";
    public static final String ANNOTATION_DEFAULT = "AnnotationDefault";
    public static final String BOOTSTRAP_METHODS = "BootstrapMethods";

    private final int nameIndex;

    AttributeInfo(final int nameIndex) {
        this.nameIndex = nameIndex;
    }

    public final int getNameIndex(){
        return this.nameIndex;
    }

    public final TagUTF8 getTagName(final ConstantPool pool){
        return (TagUTF8) pool.getEntry(this.nameIndex);
    }

    public final boolean isSupported(){
        return !(this instanceof _Default);
    }

    public static AttributeInfo create(final DataInputStream dis, final ConstantPool pool) throws IOException {
        final int nameIndex = dis.readUnsignedShort();
        final int length = dis.readInt();
        switch(((TagUTF8) pool.getEntry(nameIndex)).getValue()){
            case AttributeInfo.CONSTANT_VALUE:
                return new _ConstantValue(dis, nameIndex, length);
            //case AttributeInfo.CODE:
            //    return new _Code(dis, nameIndex, length);
            case AttributeInfo.EXCEPTIONS:
                return new _Exceptions(dis, nameIndex, length);
            case AttributeInfo.INNER_CLASSES:
                return new _InnerClasses(dis, nameIndex, length);
            case AttributeInfo.ENCLOSING_METHOD:
                return new _EnclosingMethod(dis, nameIndex, length);
            case AttributeInfo.SYNTHETIC:
                return new _Synthetic(dis, nameIndex, length);
            case AttributeInfo.SIGNATURE:
                return new _Signature(dis, nameIndex, length);
            case AttributeInfo.SOURCE_FILE:
                return new _SourceFile(dis, nameIndex, length);
            case AttributeInfo.SOURCE_DEBUG_EXTENSION:
                return new _SourceDebugExtension(dis, nameIndex, length);
            case AttributeInfo.LINE_NUMBER_TABLE:
                return new _LineNumberTable(dis, nameIndex, length);
            case AttributeInfo.LOCAL_VARIABLE_TABLE:
                return new _LocalVariableTable(dis, nameIndex, length);
            case AttributeInfo.LOCAL_VARIABLE_TYPE_TABLE:
                return new _LocalVariableTypeTable(dis, nameIndex, length);
            case AttributeInfo.DEPRECATED:
                return new _Deprecated(dis, nameIndex, length);
            case AttributeInfo.BOOTSTRAP_METHODS:
                return new _BootstrapMethods(dis, nameIndex, length);
        }
        return new _Default(dis, nameIndex, length);
    }
}
