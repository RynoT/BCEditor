package project.filetype;

import org.omg.IOP.TAG_CODE_SETS;
import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagClass;
import project.filetype.classtype.member.FieldInfo;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._Default;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ryan Thomson on 16/12/2016.
 */
public class ClassType extends FileType {

    public static final String EXTENSION_REGEX = "(?i)class";

    private int minor, major;
    private ConstantPool constantPool;
    private int accessFlags;
    private int thisClassIndex, superClassIndex;
    private int[] interfaces;
    private FieldInfo[] fields;
    private MethodInfo[] methods;
    private AttributeInfo[] attributes;

    public ClassType(final String name, final String extension, final String path){
        super(name, extension, path);
    }

    public int getMinor(){
        return this.minor;
    }

    public int getMajor(){
        return this.major;
    }

    public int getThisClassIndex(){
        return this.thisClassIndex;
    }

    public int getSuperClassIndex(){
        return this.superClassIndex;
    }

    public int[] getInterfaceIndices(){
        return this.interfaces;
    }

    public String getMinorString() {
        if(this.minor < 45) {
            return String.valueOf(this.minor);
        }
        return this.getVersionString(this.minor);
    }

    public String getMajorString() {
        if(this.major < 45 || this.major > 52) {
            return "Invalid or Unsupported";
        }
        return this.getVersionString(this.major);
    }

    public TagClass getTagThisClass(){
        return (TagClass) this.constantPool.getEntry(this.thisClassIndex);
    }

    public TagClass getTagSuperClass(){
        return (TagClass) this.constantPool.getEntry(this.superClassIndex);
    }

    public TagClass getTagInterface(final int index){
        assert (index >= 0 && index < this.interfaces.length);
        return (TagClass) this.constantPool.getEntry(this.interfaces[index]);
    }

    private String getVersionString(final int _version) {
        final int version = _version - 44;
        if(_version >= 49) {
            return "JDK " + version;
        }
        return "JDK 1." + version;
    }

    @Override
    public boolean load() {
        final String prefix = "[ClassType]     ";
        System.out.println("[ClassType] Loading " + super.getFullName() + "...");
        try(final DataInputStream dis = new DataInputStream(super.getStream())){
            if(!String.format("%x", dis.readInt()).equals("cafebabe")) {
                throw new RuntimeException("Invalid magic number! This is not a valid java class file!");
            }
            this.minor = dis.readUnsignedShort();
            this.major = dis.readUnsignedShort();
            System.out.println(prefix + "Minor: " + this.getMinorString() + " (" + this.minor
                    + "), Major: " + this.getMajorString() + " (" + this.major + ")");

            this.constantPool = new ConstantPool(dis.readUnsignedShort());
            this.constantPool.load(dis);
            System.out.println(prefix + "ConstantPool loaded with " + this.constantPool.getCount() + " entries");

            this.accessFlags = dis.readUnsignedShort();
            System.out.println(prefix + "AccessFlags: " + AccessFlags.decode(this.accessFlags, AccessFlags.Type.CLASS) + " (" + this.accessFlags + ")");

            this.thisClassIndex = dis.readUnsignedShort();
            this.superClassIndex = dis.readUnsignedShort();
            System.out.println(prefix + "ThisClass: " + this.getTagThisClass().getContentString(this.constantPool)
                    + ", SuperClass: " + this.getTagSuperClass().getContentString(this.constantPool));

            this.interfaces = new int[dis.readUnsignedShort()];
            if(this.interfaces.length > 0){
                System.out.print(prefix + "Interfaces: ");
            }
            for(int i = 0; i < this.interfaces.length; i++) {
                this.interfaces[i] = dis.readUnsignedShort();

                System.out.print(this.getTagInterface(i).getContentString(this.constantPool));
                if(i < this.interfaces.length - 1){
                    System.out.print(", ");
                }
            }
            if(this.interfaces.length > 0){
                System.out.println();
            }

            this.fields = new FieldInfo[dis.readUnsignedShort()];
            for(int i = 0; i < this.fields.length; i++){
                this.fields[i] = new FieldInfo(dis, this.constantPool);
            }
            this.methods = new MethodInfo[dis.readUnsignedShort()];
            for(int i = 0; i < this.methods.length; i++){
                this.methods[i] = new MethodInfo(dis, this.constantPool);
            }
            System.out.println(prefix + "Field count: " + this.fields.length + ", Method count: " + this.methods.length);

            this.attributes = new AttributeInfo[dis.readUnsignedShort()];
            if(this.attributes.length > 0){
                System.out.print(prefix + "Attributes: ");
            }
            for(int i = 0; i < this.attributes.length; i++){
                this.attributes[i] = AttributeInfo.create(dis, this.constantPool);

                System.out.print(this.attributes[i].getTagName(this.constantPool).getValue());
                if(this.attributes[i] instanceof _Default){
                    System.out.print(" (Unsupported)");
                }
                if(i < this.attributes.length - 1){
                    System.out.print(", ");
                }
            }
            if(this.attributes.length > 0){
                System.out.println();
            }

            if(dis.available() != 0){ //should always be 0 once we get to here
                System.err.println(prefix + "Unable to load complete class file. Abort.");
                return false;
            }
            return true;
        } catch(final IOException e) {
            e.printStackTrace(System.err);
        }
        return false;
    }
}
