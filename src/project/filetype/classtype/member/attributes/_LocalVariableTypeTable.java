package project.filetype.classtype.member.attributes;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagUTF8;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _LocalVariableTypeTable extends AttributeInfo {

    private final LocalVariableType[] localVariableTypes;

    _LocalVariableTypeTable(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        this.localVariableTypes = new LocalVariableType[dis.readUnsignedShort()];
        assert(length == this.localVariableTypes.length * 10 + 2);
        for(int i = 0; i < this.localVariableTypes.length; i++){
            this.localVariableTypes[i] = new LocalVariableType(dis);
        }
    }

    public LocalVariableType[] getLocalVariableTypes(){
        return this.localVariableTypes;
    }

    public class LocalVariableType {

        private final int startPc, length;
        private final int nameIndex, signatureIndex;
        private final int index;

        private LocalVariableType(final DataInputStream dis) throws IOException {
            this.startPc = dis.readUnsignedShort();
            this.length = dis.readUnsignedShort();
            this.nameIndex = dis.readUnsignedShort();
            this.signatureIndex = dis.readUnsignedShort();
            this.index = dis.readUnsignedShort();
        }

        public int getStartPc(){
            return this.startPc;
        }

        public int getLength(){
            return this.length;
        }

        public int getNameIndex(){
            return this.nameIndex;
        }

        public int getSignatureIndex(){
            return this.signatureIndex;
        }

        public int getIndex(){
            return this.index;
        }

        public TagUTF8 getTagName(final ConstantPool pool){
            return (TagUTF8) pool.getEntry(this.nameIndex);
        }

        public TagUTF8 getTagSignature(final ConstantPool pool){
            return (TagUTF8) pool.getEntry(this.signatureIndex);
        }
    }
}
