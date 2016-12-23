package project.filetype.classtype.member.attributes;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;
import project.filetype.classtype.constantpool.tag.TagMethodHandle;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class _BootstrapMethods extends AttributeInfo {

    private final BootstrapMethod[] bootstrapMethods;

    _BootstrapMethods(final DataInputStream dis, final int nameIndex, final int length) throws IOException {
        super(nameIndex);

        // We can't verify length because BootstrapMethod's length is unknown until read
        this.bootstrapMethods = new BootstrapMethod[dis.readUnsignedShort()];
        for(int i = 0; i < this.bootstrapMethods.length; i++){
            this.bootstrapMethods[i] = new BootstrapMethod(dis);
        }
    }

    public BootstrapMethod[] getBootstrapMethods(){
        return this.bootstrapMethods;
    }

    public class BootstrapMethod {

        private final int methodIndex;
        private final int[] argumentIndices;

        private BootstrapMethod(final DataInputStream dis) throws IOException {
            this.methodIndex = dis.readUnsignedShort();
            this.argumentIndices = new int[dis.readUnsignedShort()];
            for(int i = 0; i < this.argumentIndices.length; i++){
                this.argumentIndices[i] = dis.readUnsignedShort();
            }
        }

        public int getMethodIndex(){
            return this.methodIndex;
        }

        public int[] getArgumentIndices(){
            return this.argumentIndices;
        }

        public TagMethodHandle getTagMethod(final ConstantPool pool){
            return (TagMethodHandle) pool.getEntry(this.methodIndex);
        }

        public PoolTag getTagArgument(final ConstantPool pool, final int index){
            assert (index >= 0 && index < this.argumentIndices.length);
            return pool.getEntry(this.argumentIndices[index]);
        }

        public String getContentString(final ConstantPool pool){
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getTagMethod(pool).getContentString(pool)).append(" (");
            for(int i = 0; i < this.argumentIndices.length; i++){
                sb.append(this.getTagArgument(pool, i));
                if(i < this.argumentIndices.length - 1){
                    sb.append(", ");
                }
            }
            return sb.append(")").toString();
        }
    }
}
