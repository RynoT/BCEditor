package project.filetype.classtype.member;

import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.constantpool.ConstantPool;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class FieldInfo extends MemberInfo {

    // Possible attributes: ConstantValue, Synthetic, Signature, Deprecated, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations

    public FieldInfo(final DataInputStream dis, final ConstantPool pool) throws IOException {
        super(dis, pool, AccessFlags.Type.FIELD);
    }
}
