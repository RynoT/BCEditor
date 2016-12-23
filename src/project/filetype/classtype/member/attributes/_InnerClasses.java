package project.filetype.classtype.member.attributes;

import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagClass;
import project.filetype.classtype.constantpool.tag.TagUTF8;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _InnerClasses extends AttributeInfo {

    private final InnerClass[] innerClasses;

    _InnerClasses(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        this.innerClasses = new InnerClass[dis.readUnsignedShort()];
        assert(length == this.innerClasses.length * 8 + 2);
        for(int i = 0; i < this.innerClasses.length; i++){
            this.innerClasses[i] = new InnerClass(dis);
        }
    }

    public InnerClass[] getInnerClasses(){
        return this.innerClasses;
    }

    public class InnerClass {

        private final int innerClassIndex, outerClassIndex;
        private final int nameIndex;
        private final int innerAccessFlags;

        public InnerClass(final DataInputStream dis) throws IOException {
            this.innerClassIndex = dis.readUnsignedShort();
            this.outerClassIndex = dis.readUnsignedShort();
            this.nameIndex = dis.readUnsignedShort();
            this.innerAccessFlags = dis.readUnsignedShort();
        }

        public int getInnerClassIndex(){
            return this.innerClassIndex;
        }

        public int getOuterClassIndex(){
            return this.outerClassIndex;
        }

        public int getNameIndex(){
            return this.nameIndex;
        }

        public int getInnerAccessFlags(){
            return this.innerAccessFlags;
        }

        public TagClass getTagInnerClass(final ConstantPool pool){
            return (TagClass) pool.getEntry(this.innerClassIndex);
        }

        public TagClass getTagOuterClass(final ConstantPool pool){
            return (TagClass) pool.getEntry(this.outerClassIndex);
        }

        public TagUTF8 getTagName(final ConstantPool pool){
            return (TagUTF8) pool.getEntry(this.nameIndex);
        }

        public String getInnerAccessFlagsString(){
            return AccessFlags.decode(this.innerAccessFlags, AccessFlags.Type.INNER);
        }
    }
}
