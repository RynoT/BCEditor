package ui.component;

import project.filetype.ClassType;
import project.filetype.classtype.ClassFormat;
import project.filetype.classtype.member.FieldInfo;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._Code;
import project.filetype.classtype.opcode.Instruction;
import ui.component.bceditor.IBCTextEditor;

import java.awt.*;
import java.util.List;

/**
 * Created by Ryan Thomson on 27/12/2016.
 */
public class IBCEditor extends IEditor {

    public static final Font EDITOR_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    public static final Color EDITOR_BACKGROUND = new Color(50, 50, 50);

    private final ClassType classType;

    private final IBCTextEditor textEditor;

    public IBCEditor(final ClassType classType) {
        this.classType = classType;

        super.setBackground(IBCEditor.EDITOR_BACKGROUND);

        super.setLayout(new BorderLayout(0, 0));
        super.add(this.textEditor = new IBCTextEditor(), BorderLayout.CENTER);

        final StringBuilder sb = new StringBuilder();

        System.out.println(ClassFormat.format(classType));
        sb.append(ClassFormat.format(classType)).append("\n\n");
        for(final FieldInfo field : classType.getFields()) {
            System.out.println(ClassFormat.format(field, classType.getConstantPool()));
            sb.append(ClassFormat.format(field, classType.getConstantPool())).append("\n");
        }
        sb.append("\n");
        for(final MethodInfo method : classType.getMethods()) {
            // Print method name
            System.out.println(ClassFormat.format(method, classType.getConstantPool()));
            sb.append(ClassFormat.format(method, classType.getConstantPool())).append("\n");

            final _Code code = (_Code) AttributeInfo.findFirst(AttributeInfo.CODE, method.getAttributes(), classType.getConstantPool());
            if(code != null){
                final List<Instruction> instructions = ClassFormat.format(code.getRawCode());
                for(final Instruction next : instructions){
                    System.out.print("    " + next.getPc() + ": " + next.getOpcode().getMnemonic() + " ");
                    sb.append(next.getPc()).append(": ").append(next.getOpcode().getMnemonic()).append(" ");
                    for(int i = 0; i < next.getOperandCount(); i++){
                        System.out.print(next.getOperands().get(i).getValue());
                        sb.append(next.getOperands().get(i).getValue());
                        if(i < next.getOperandCount() - 1){
                            System.out.print(", ");
                            sb.append(", ");
                        }
                    }
                    System.out.println();
                    sb.append("\n");
                }
                System.out.println("}");
                sb.append("}\n\n");
            }
        }
        sb.append(" ");

        this.textEditor.setText(sb.toString());
    }

}
