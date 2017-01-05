package project.filetype.classtype.member;

import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.constantpool.ConstantPool;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class MethodInfo extends MemberInfo {

    // Possible attributes: Code, Exceptions, Synthetic, Signature, Deprecated, RuntimeVisibleAnnotations,
    // RuntimeInvisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations, AnnotationDefault

    public MethodInfo(final DataInputStream dis, final ConstantPool pool) throws IOException {
        super(dis, pool, AccessFlags.Type.METHOD);
    }
}
