package project.filetype.classtype.constantpool;

import project.filetype.classtype.constantpool.tag.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan Thomson on 22/12/2016.
 */
public class ConstantPool {

    private final int count; //initial count of constant pool
    private final List<PoolTag> entries;

    public ConstantPool(final int count) {
        this.count = count;
        this.entries = new ArrayList<>(count);
    }

    public int getCount() {
        return this.count;
    }

    public void clearEntries(){
        this.entries.clear();
    }

    public PoolTag getEntry(final int index) {
        assert (index >= 0 && index < this.entries.size());
        return this.entries.get(index);
    }

    public void index(final DataInputStream dis) throws IOException {
        this.entries.add(new TagEmpty());
        for(int i = 0; i < this.count - 1; i++) {
            final int id = dis.readUnsignedByte();
            switch(id) {
                // We only need to read TagUTF8 and TagClass when indexing
                case PoolTag.TAG_UTF8:
                    this.entries.add(new TagUTF8(dis));
                    break;
                case PoolTag.TAG_CLASS:
                    this.entries.add(new TagClass(dis));
                    break;

                case PoolTag.TAG_INTEGER:
                case PoolTag.TAG_FLOAT:
                case PoolTag.TAG_FIELD_REF:
                case PoolTag.TAG_METHOD_REF:
                case PoolTag.TAG_IM_REF:
                case PoolTag.TAG_NAME_AND_TYPE:
                case PoolTag.TAG_METHOD_HANDLE:
                case PoolTag.TAG_INVOKE_DYNAMIC:
                    this.entries.add(new TagEmpty());
                    dis.skipBytes(4);
                    break;
                case PoolTag.TAG_LONG:
                case PoolTag.TAG_DOUBLE:
                    i++;
                    this.entries.add(new TagEmpty());
                    this.entries.add(new TagEmpty());
                    dis.skipBytes(8);
                    break;
                case PoolTag.TAG_STRING:
                case PoolTag.TAG_METHOD_TYPE:
                    this.entries.add(new TagEmpty());
                    dis.skipBytes(2);
                    break;
                default:
                    System.err.println("[ConstantPool][Indexing] Found tag with unknown id: " + id + " (index: " + i + ")");
            }
        }
    }

    public void load(final DataInputStream dis) throws IOException {
        this.entries.add(new TagEmpty()); //the constant pool is indexed starting at 1 (so we must fill index 0)
        for(int i = 0; i < this.count - 1; i++) {
            final int id = dis.readUnsignedByte();
            switch(id) {
                case PoolTag.TAG_UTF8:
                    this.entries.add(new TagUTF8(dis));
                    break;
                case PoolTag.TAG_INTEGER:
                    this.entries.add(new TagInteger(dis));
                    break;
                case PoolTag.TAG_FLOAT:
                    this.entries.add(new TagFloat(dis));
                    break;
                case PoolTag.TAG_LONG:
                    i++;
                    this.entries.add(new TagLong(dis));
                    this.entries.add(new TagEmpty());
                    break;
                case PoolTag.TAG_DOUBLE:
                    i++;
                    this.entries.add(new TagDouble(dis));
                    this.entries.add(new TagEmpty());
                    break;
                case PoolTag.TAG_CLASS:
                    this.entries.add(new TagClass(dis));
                    break;
                case PoolTag.TAG_STRING:
                    this.entries.add(new TagString(dis));
                    break;
                case PoolTag.TAG_FIELD_REF:
                    this.entries.add(new TagRef(dis, TagRef.TagRefType.FIELD));
                    break;
                case PoolTag.TAG_METHOD_REF:
                    this.entries.add(new TagRef(dis, TagRef.TagRefType.METHOD));
                    break;
                case PoolTag.TAG_IM_REF:
                    this.entries.add(new TagRef(dis, TagRef.TagRefType.INTERFACE_METHOD));
                    break;
                case PoolTag.TAG_NAME_AND_TYPE:
                    this.entries.add(new TagNameAndType(dis));
                    break;
                case PoolTag.TAG_METHOD_HANDLE:
                    this.entries.add(new TagMethodHandle(dis));
                    break;
                case PoolTag.TAG_METHOD_TYPE:
                    this.entries.add(new TagMethodType(dis));
                    break;
                case PoolTag.TAG_INVOKE_DYNAMIC:
                    this.entries.add(new TagInvokeDynamic(dis));
                    break;
                default:
                    System.err.println("[ConstantPool] Found tag with unknown id: " + id + " (index: " + i + ")");
            }
        }
    }
}
