package project.filetype.classtype.member.attributes;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagUTF8;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _LocalVariableTable extends AttributeInfo {

    private final LocalVariable[] localVariables;

    _LocalVariableTable(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        this.localVariables = new LocalVariable[dis.readUnsignedShort()];
        assert(length == this.localVariables.length * 10 + 2);
        for(int i = 0; i < this.localVariables.length; i++){
            this.localVariables[i] = new LocalVariable(dis);
        }
    }

    public LocalVariable[] getLocalVariables(){
        return this.localVariables;
    }

    public class LocalVariable {

        private final int startPc, length;
        private final int nameIndex, descriptorIndex;
        private final int index;

        public LocalVariable(final DataInputStream dis) throws IOException {
            this.startPc = dis.readUnsignedShort();
            this.length = dis.readUnsignedShort();
            this.nameIndex = dis.readUnsignedShort();
            this.descriptorIndex = dis.readUnsignedShort();
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

        public int getDescriptorIndex(){
            return this.descriptorIndex;
        }

        public int getIndex(){
            return this.index;
        }

        public TagUTF8 getTagName(final ConstantPool pool){
            return (TagUTF8) pool.getEntry(this.nameIndex);
        }

        public TagUTF8 getTagDescriptor(final ConstantPool pool){
            return (TagUTF8) pool.getEntry(this.descriptorIndex);
        }
    }
}
