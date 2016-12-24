package project.filetype.classtype.index;

import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.FieldInfo;
import project.filetype.classtype.member.MethodInfo;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class Index {

    private final int accessFlags;
    private final MemberIndex[] fields, methods;
    private final InterfaceIndex[] interfaces;

    public Index(final ConstantPool pool, final int accessFlags, final FieldInfo[] fields, final MethodInfo[] methods, final int[] interfaces){
        this.accessFlags = accessFlags;
        this.fields = new MemberIndex[fields.length];
        for(int i = 0; i < fields.length; i++){
            this.fields[i] = new MemberIndex(fields[i], pool);
        }
        this.methods = new MemberIndex[methods.length];
        for(int i = 0; i < methods.length; i++){
            this.methods[i] = new MemberIndex(methods[i], pool);
        }
        this.interfaces = new InterfaceIndex[interfaces.length];
    }

    public int getAccessFlags(){
        return this.accessFlags;
    }

    public MemberIndex[] getFields(){
        return this.fields;
    }

    public MemberIndex[] getMethods(){
        return this.methods;
    }

    public InterfaceIndex[] getInterfaces(){
        return this.interfaces;
    }

    public boolean isEnum(){
        return AccessFlags.containsFlag(this.accessFlags, AccessFlags.ACC_ENUM);
    }

    public boolean isFinal(){
        return AccessFlags.containsFlag(this.accessFlags, AccessFlags.ACC_FINAL);
    }

    public boolean isAbstract(){
        return AccessFlags.containsFlag(this.accessFlags, AccessFlags.ACC_ABSTRACT);
    }

    public boolean isInterface(){
        return AccessFlags.containsFlag(this.accessFlags, AccessFlags.ACC_INTERFACE);
    }

    public boolean isMainClass(){
        for(final MemberIndex method : this.methods){
            if(!method.getName().equals("main")){
                continue;
            }
            if(!method.getDescriptor().equals("([Ljava/lang/String;)V")){
                continue;
            }
            if(method.getAccessFlags() != (AccessFlags.ACC_PUBLIC.mask() | AccessFlags.ACC_STATIC.mask())){
                continue;
            }
            return true;
        }
        return false;
    }
}
