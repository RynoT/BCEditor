package project.filetype.classtype;

import java.util.Arrays;

/**
 * Created by Ryan Thomson on 23/12/2016.
 */
public enum AccessFlags {

    ACC_PUBLIC(0x1), ACC_PRIVATE(0x2), ACC_PROTECTED(0x4),
    ACC_STATIC(0x8), ACC_FINAL(0x10), ACC_SYNCHRONIZED(0x20),
    ACC_SUPER(0x20), ACC_VOLATILE(0x40), ACC_TRANSIENT(0x80),
    ACC_NATIVE(0x100), ACC_INTERFACE(0x200), ACC_ABSTRACT(0x400), ACC_STRICTFP(0x800),
    ACC_SYNTHETIC(0x1000), ACC_ANNOTATION(0x2000), ACC_ENUM(0x4000), ACC_BRIDGE(0x40), ACC_VARARGS(0x80);

    private final int mask;
    private final String verbose;

    AccessFlags(final int mask) {
        this.mask = mask;
        this.verbose = super.name().replaceFirst("ACC_", "").toLowerCase();
    }

    public int mask() {
        return this.mask;
    }

    public String verbose() {
        return this.verbose;
    }

    public static boolean containsFlag(final int modifier, final AccessFlags flag){
        return AccessFlags.containsFlag(modifier, flag.mask());
    }

    public static boolean containsFlag(final int modifier, final int mask){
        return (modifier & mask) == mask;
    }

    public static String decode(int modifier, final Type type) {
        if(modifier <= 0){
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        if((type == Type.CLASS || type == Type.FIELD) && (modifier & ACC_ENUM.mask()) == ACC_ENUM.mask())
            modifier = modifier & (~ACC_STATIC.mask() & ~ACC_FINAL.mask()); //Remove static and final modifiers from enums
        for(int i = 0; i < type.flags.length; i++) {
            final AccessFlags flag = type.flags[i];
            final int mask = flag.mask();
            if(mask == ACC_ABSTRACT.mask() && (modifier & ACC_INTERFACE.mask()) == ACC_INTERFACE.mask())
                continue; //Don't show abstract modifier on interfaces
            if(mask != ACC_SUPER.mask() && mask != ACC_ANNOTATION.mask() && mask != ACC_VARARGS.mask()
                    && (modifier & mask) == mask) {
                sb.append(" ");
                if(mask == ACC_INTERFACE.mask() && (modifier & ACC_ANNOTATION.mask()) == ACC_ANNOTATION.mask())
                    sb.append("@");
                sb.append(flag.verbose());
            }
        }
        return sb.toString().trim();
    }

    public enum Type {
        CLASS(new AccessFlags[]{ACC_PUBLIC, ACC_FINAL, ACC_SUPER, ACC_ABSTRACT, ACC_INTERFACE, ACC_SYNTHETIC, ACC_ANNOTATION, ACC_ENUM}),
        INNER(new AccessFlags[]{ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_ABSTRACT, ACC_INTERFACE, ACC_SYNTHETIC, ACC_ANNOTATION, ACC_ENUM}),
        FIELD(new AccessFlags[]{ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_VOLATILE, ACC_TRANSIENT, ACC_SYNTHETIC, ACC_ENUM}),
        METHOD(new AccessFlags[]{ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_SYNCHRONIZED,
                ACC_BRIDGE, ACC_VARARGS, ACC_NATIVE, ACC_ABSTRACT, ACC_STRICTFP, ACC_SYNTHETIC});

        private final AccessFlags[] flags;

        Type(final AccessFlags[] flags) {
            this.flags = flags;
        }

        public boolean contains(final AccessFlags flag){
            for(final AccessFlags next : this.flags){
                if(next == flag){
                    return true;
                }
            }
            return false;
        }
    }
}