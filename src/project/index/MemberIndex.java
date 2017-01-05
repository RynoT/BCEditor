package project.index;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.MemberInfo;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public class MemberIndex {

    private final int accessFlags;
    private final String name, descriptor;

    MemberIndex(final MemberInfo info, final ConstantPool pool){
        this.accessFlags = info.getAccessFlags();
        this.name = info.getTagName(pool).getValue();
        this.descriptor = info.getTagDescriptor(pool).getValue();
    }

    public int getAccessFlags(){
        return this.accessFlags;
    }

    public String getName(){
        return this.name;
    }

    public String getDescriptor(){
        return this.descriptor;
    }

}
