package project.filetype.classtype.member.attributes;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _Deprecated extends AttributeInfo {

    _Deprecated(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        assert (length == 0);
    }
}
