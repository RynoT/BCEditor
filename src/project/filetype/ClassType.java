package project.filetype;

import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.index.Index;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.tag.TagClass;
import project.filetype.classtype.member.FieldInfo;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._Default;

import java.io.DataInputStream;
import java.io.IOException;

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

    private Index index = null;

    private boolean loaded = false;
    private final Object loadSyncLock = new Object();

    public ClassType(final String name, final String extension, final String path){
        super(name, extension, path);
    }

    public Index getIndex(){
        return this.index;
    }

    public int getMinor(){
        return this.minor;
    }

    public int getMajor(){
        return this.major;
    }

    public int getAccessFlags(){
        return this.accessFlags;
    }

    public String getAccessFlagsString(){
        return AccessFlags.decode(this.accessFlags, AccessFlags.Type.CLASS);
    }

    public FieldInfo[] getFields(){
        return this.fields;
    }

    public MethodInfo[] getMethods(){
        return this.methods;
    }

    public AttributeInfo[] getAttributes(){
        return this.attributes;
    }

    public ConstantPool getConstantPool(){
        return this.constantPool;
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

    public boolean isIndexed(){
        synchronized(this.loadSyncLock){
            return this.index != null;
        }
    }

    public boolean isLoaded(){
        synchronized(this.loadSyncLock) {
            return this.loaded;
        }
    }

    public boolean isEnum(){
        assert (this.isLoaded());
        return AccessFlags.containsFlag(this.accessFlags, AccessFlags.ACC_ENUM);
    }

    public boolean isFinal(){
        assert (this.isLoaded());
        return AccessFlags.containsFlag(this.accessFlags, AccessFlags.ACC_FINAL);
    }

    public boolean isAbstract(){
        assert (this.isLoaded());
        return AccessFlags.containsFlag(this.accessFlags, AccessFlags.ACC_ABSTRACT);
    }

    public boolean isInterface(){
        assert (this.isLoaded());
        return AccessFlags.containsFlag(this.accessFlags, AccessFlags.ACC_INTERFACE);
    }

    public boolean isMainClass(){
        assert (this.isLoaded());
        for(final MethodInfo method : this.methods){
            if(!method.getTagName(this.constantPool).getValue().equals("main")){
                continue;
            }
            if(!method.getTagDescriptor(this.constantPool).getValue().equals("([Ljava/lang/String;)V")){
                continue;
            }
            if(method.getAccessFlags() != (AccessFlags.ACC_PUBLIC.mask() | AccessFlags.ACC_STATIC.mask())){
                continue;
            }
            return true;
        }
        return false;
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
    public void unload() {
        // We allow all the memory consumed by this class to be freed
        synchronized(this.loadSyncLock){
            this.minor = this.major = 0;
            if(this.constantPool != null){
                this.constantPool.clearEntries();
            }
            this.constantPool = null;
            this.accessFlags = 0;
            this.thisClassIndex = this.superClassIndex = 0;
            this.interfaces = null;
            this.fields = null;
            this.methods = null;
            this.attributes = null;

            this.loaded = false;
        }
    }

    // Index is basically the same as loading except that it only formats important data and immediately unloads after completed
    public boolean index(){
        synchronized(this.loadSyncLock){
            boolean success = false;
            if(!this.isLoaded()){
                try(final DataInputStream dis = new DataInputStream(super.getStream())) {
                    if(!String.format("%x", dis.readInt()).equals("cafebabe")) {
                        return false;
                    }
                    dis.skipBytes(4);

                    this.constantPool = new ConstantPool(dis.readUnsignedShort());
                    this.constantPool.index(dis);

                    this.accessFlags = dis.readUnsignedShort();

                    dis.skipBytes(2);
                    this.superClassIndex = dis.readUnsignedShort();

                    this.interfaces = new int[dis.readUnsignedShort()];
                    for(int i = 0; i < this.interfaces.length; i++){
                        this.interfaces[i] = dis.readUnsignedShort();
                    }
                    this.fields = new FieldInfo[dis.readUnsignedShort()];
                    for(int i = 0; i < this.fields.length; i++) {
                        this.fields[i] = new FieldInfo(dis, this.constantPool);
                    }
                    this.methods = new MethodInfo[dis.readUnsignedShort()];
                    for(int i = 0; i < this.methods.length; i++) {
                        this.methods[i] = new MethodInfo(dis, this.constantPool);
                    }
                    // We don't need to read the attributes of the class

                    success = true;
                } catch(final IOException e) {
                    e.printStackTrace(System.err);
                }
            } else {
                success = true;
            }
            if(success){
                this.index = new Index(this.constantPool, this.accessFlags, this
                        .superClassIndex, this.fields, this.methods, this.interfaces);
            } else {
                this.index = null;
            }
            if(!this.isLoaded()){
                this.unload();
            }
            if(success){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean load() {
        if(this.isLoaded()){
            return true;
        }
        synchronized(this.loadSyncLock) {
            final String prefix = "[ClassType]     ";
            System.out.println("[ClassType] Loading " + super.getFullName() + "...");
            try(final DataInputStream dis = new DataInputStream(super.getStream())) {
                // Check the magic number of the class
                if(!String.format("%x", dis.readInt()).equals("cafebabe")) {
                    System.err.println(prefix + "Invalid magic number! Expected 0xCAFEBABE.");
                    return false;
                }
                // Load minor and major version
                this.minor = dis.readUnsignedShort();
                this.major = dis.readUnsignedShort();
                System.out.println(prefix + "Minor: " + this.getMinorString() + " (" + this.minor
                        + "), Major: " + this.getMajorString() + " (" + this.major + ")");

                // Load constant pool
                this.constantPool = new ConstantPool(dis.readUnsignedShort());
                this.constantPool.load(dis);
                System.out.println(prefix + "ConstantPool loaded with " + this.constantPool.getEntryCount() + " entries");

                // Load access flags
                this.accessFlags = dis.readUnsignedShort();
                System.out.println(prefix + "AccessFlags: " + AccessFlags.decode(this.accessFlags, AccessFlags.Type.CLASS) + " (" + this.accessFlags + ")");

                // Load this class and super class indices
                this.thisClassIndex = dis.readUnsignedShort();
                this.superClassIndex = dis.readUnsignedShort();
                System.out.println(prefix + "ThisClass: " + this.getTagThisClass().getContentString(this.constantPool)
                        + ", SuperClass: " + this.getTagSuperClass().getContentString(this.constantPool));

                // Load interface indices
                this.interfaces = new int[dis.readUnsignedShort()];
                if(this.interfaces.length > 0) {
                    System.out.print(prefix + "Interfaces: ");
                }
                for(int i = 0; i < this.interfaces.length; i++) {
                    this.interfaces[i] = dis.readUnsignedShort();

                    System.out.print(this.getTagInterface(i).getContentString(this.constantPool));
                    if(i < this.interfaces.length - 1) {
                        System.out.print(", ");
                    }
                }
                if(this.interfaces.length > 0) {
                    System.out.println();
                }

                // Load fields
                this.fields = new FieldInfo[dis.readUnsignedShort()];
                for(int i = 0; i < this.fields.length; i++) {
                    this.fields[i] = new FieldInfo(dis, this.constantPool);
                }
                // Load methods
                this.methods = new MethodInfo[dis.readUnsignedShort()];
                for(int i = 0; i < this.methods.length; i++) {
                    this.methods[i] = new MethodInfo(dis, this.constantPool);
                }
                System.out.println(prefix + "Field count: " + this.fields.length + ", Method count: " + this.methods.length);

                // Load class attributes
                this.attributes = new AttributeInfo[dis.readUnsignedShort()];
                if(this.attributes.length > 0) {
                    System.out.print(prefix + "Attributes: ");
                }
                for(int i = 0; i < this.attributes.length; i++) {
                    this.attributes[i] = AttributeInfo.create(dis, this.constantPool);

                    System.out.print(this.attributes[i].getTagName(this.constantPool).getValue());
                    if(this.attributes[i] instanceof _Default) {
                        System.out.print(" (Unsupported)");
                    }
                    if(i < this.attributes.length - 1) {
                        System.out.print(", ");
                    }
                }
                if(this.attributes.length > 0) {
                    System.out.println();
                }

                // Check to see if the class has been fully loaded
                if(dis.available() != 0) { //should always be 0 once we get to here
                    System.err.println(prefix + "Unable to load complete class file. Abort.");
                    this.unload();
                    return false;
                }

                // Return true. The loading was successful.
                return this.loaded = true;
            } catch(final IOException e) {
                e.printStackTrace(System.err);
            }
            // Return false. Something bad happened and the load failed. (Hopefully never happens)
            this.unload(); //remove anything we may of partially loaded
            return this.loaded = false;
        }
    }
}
