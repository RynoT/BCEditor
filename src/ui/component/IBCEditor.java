package ui.component;

import project.filetype.ClassType;
import project.filetype.classtype.ClassFormat;
import project.filetype.classtype.member.FieldInfo;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._Code;
import project.filetype.classtype.opcode.Instruction;

import java.awt.*;
import java.util.List;

/**
 * Created by Ryan Thomson on 27/12/2016.
 */
public class IBCEditor extends IEditor {

    private final ClassType classType;

    public IBCEditor(final ClassType classType) {
        this.classType = classType;

        super.setBackground(Color.ORANGE);
        super.add(new IButton(classType.getFullPath()));

        System.out.println(ClassFormat.format(classType));
        for(final FieldInfo field : classType.getFields()) {
            System.out.println(ClassFormat.format(field, classType.getConstantPool()));
        }
        for(final MethodInfo method : classType.getMethods()) {
            // Print method name
            System.out.println(ClassFormat.format(method, classType.getConstantPool()));

            final _Code code = (_Code) AttributeInfo.findFirst(AttributeInfo.CODE, method.getAttributes(), classType.getConstantPool());
            if(code != null){
                final List<Instruction> instructions = ClassFormat.format(code.getRawCode());
                for(final Instruction next : instructions){
                    System.out.print("    " + next.getPc() + ": " + next.getOpcode().getMnemonic() + " ");
                    for(int i = 0; i < next.getOperandCount(); i++){
                        System.out.print(next.getOperands().get(i).getValue());
                        if(i < next.getOperandCount() - 1){
                            System.out.print(", ");
                        }
                    }
                    System.out.println();
                }
                System.out.println("}");
            }
        }
    }

}
