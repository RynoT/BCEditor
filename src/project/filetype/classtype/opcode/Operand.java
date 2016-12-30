package project.filetype.classtype.opcode;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 28/12/2016.
 */
public class Operand {

    private int value;
    private final int byteCount;
    private final OperandType type;

    public Operand(final DataInputStream dis, final boolean signed, final int byteCount, final OperandType type) throws IOException {
        this.byteCount = byteCount;
        this.type = type;
        switch(byteCount){
            case 1:
                this.value = signed ? dis.readByte() : dis.readUnsignedByte();
                break;
            case 2:
                this.value = signed ? dis.readShort() : dis.readUnsignedShort();
                break;
            case 4:
                assert(signed); //we can only read signed ints
                this.value = dis.readInt();
                break;
            default:
                this.value = -1;
                assert(false);
        }
    }

    public Operand(final int value, final int byteCount, final OperandType type){
        this.value = value;
        this.byteCount = byteCount;
        this.type = type;
    }

    public OperandType getType(){
        return this.type;
    }

    public int getValue(){
        return this.value;
    }

    public int getByteCount(){
        return this.byteCount;
    }

    public void setValue(final int value){
        this.value = value;
    }
}
