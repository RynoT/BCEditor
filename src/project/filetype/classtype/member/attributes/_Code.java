package project.filetype.classtype.member.attributes;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagClass;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _Code extends AttributeInfo {

    private final int maxStack, maxLocals;
    private final byte[] code;
    private final ExceptionTable[] exceptionTables;
    private final AttributeInfo[] attributes;

    _Code(final DataInputStream dis, final int nameIndex, final int length, final ConstantPool pool) throws IOException {
        super(nameIndex);

        this.maxStack = dis.readUnsignedShort();
        this.maxLocals = dis.readUnsignedShort();

        this.code = new byte[dis.readInt()];
        dis.readFully(this.code);

        this.exceptionTables = new ExceptionTable[dis.readUnsignedShort()];
        for(int i = 0; i < this.exceptionTables.length; i++){
            this.exceptionTables[i] = new ExceptionTable(dis);
        }

        this.attributes = new AttributeInfo[dis.readUnsignedShort()];
        for(int i = 0; i < this.attributes.length; i++){
            this.attributes[i] = AttributeInfo.create(dis, pool);
        }
    }

    public int getMaxStack(){
        return this.maxStack;
    }

    public int getMaxLocals(){
        return this.maxLocals;
    }

    public byte[] getRawCode(){
        return this.code;
    }

    public ExceptionTable[] getExceptionTables(){
        return this.exceptionTables;
    }

    public AttributeInfo[] getAttributes(){
        return this.attributes;
    }

    public class ExceptionTable {

        private final int startPc, endPc, handlerPc;
        private final int catchType;

        private ExceptionTable(final DataInputStream dis) throws IOException {
            this.startPc = dis.readUnsignedShort();
            this.endPc = dis.readUnsignedShort();
            this.handlerPc = dis.readUnsignedShort();
            this.catchType = dis.readUnsignedShort();
        }

        public int getStartPc(){
            return this.startPc;
        }

        public int getEndPc(){
            return this.endPc;
        }

        public int getHandlerPc(){
            return this.handlerPc;
        }

        public int getCatchType(){
            return this.catchType;
        }

        public TagClass getTabCatch(final ConstantPool pool){
            return (TagClass) pool.getEntry(this.catchType);
        }
    }
}
