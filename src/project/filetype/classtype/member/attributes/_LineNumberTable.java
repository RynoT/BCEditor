package project.filetype.classtype.member.attributes;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _LineNumberTable extends AttributeInfo {

    private final LineNumber[] lineNumbers;

    _LineNumberTable(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        this.lineNumbers = new LineNumber[dis.readUnsignedShort()];
        assert (length == this.lineNumbers.length * 4 + 2);
        for(int i = 0; i < this.lineNumbers.length; i++){
            this.lineNumbers[i] = new LineNumber(dis);
        }
    }

    public LineNumber[] getLineNumbers(){
        return this.lineNumbers;
    }

    public class LineNumber {

        private final int startPc, lineNumber;

        private LineNumber(final DataInputStream dis) throws IOException {
            this.startPc = dis.readUnsignedShort();
            this.lineNumber = dis.readUnsignedShort();
        }

        public int getStartPc(){
            return this.startPc;
        }

        public int getLineNumber(){
            return this.lineNumber;
        }
    }
}
