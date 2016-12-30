package ui.component;

import project.filetype.ClassType;
import ui.component.bceditor.IBCTextEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by Ryan Thomson on 27/12/2016.
 */
public class IBCEditor extends IEditor {

    public static final Font EDITOR_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    private final ClassType classType;

    private final IBCTextEditor textEditor;

    public IBCEditor(final ClassType classType) {
        this.classType = classType;

        super.setBackground(IComponent.DEFAULT_BACKGROUND_DARK);

        super.setLayout(new BorderLayout(0, 0));

        this.textEditor = new IBCTextEditor();
        {
            this.textEditor.populate(classType);
        }
        super.add(this.textEditor, BorderLayout.CENTER);

        final IToolbar toolbar = new IToolbar(IOrientation.EAST);
        {
            final IButton poolButton = new IButton("Constant Pool").setToggle(true);
            toolbar.addTab(new ITab(poolButton, new IBreakdown(), true), true);

            poolButton.click();
        }
        super.add(toolbar, toolbar.getOrientation().getBorder());

//        final StringBuilder sb = new StringBuilder();
//
//        System.out.println(ClassFormat.format(classType));
//        sb.append(ClassFormat.format(classType)).append("\n\n");
//        for(final FieldInfo field : classType.getFields()) {
//            System.out.println(ClassFormat.format(field, classType.getConstantPool()));
//            sb.append(ClassFormat.format(field, classType.getConstantPool())).append("\n");
//        }
//        sb.append("\n");
//        for(final MethodInfo method : classType.getMethods()) {
//            // Print method name
//            System.out.println(ClassFormat.format(method, classType.getConstantPool()));
//            sb.append(ClassFormat.format(method, classType.getConstantPool())).append("\n");
//
//            final _Code code = (_Code) AttributeInfo.findFirst(AttributeInfo.CODE, method.getAttributes(), classType.getConstantPool());
//            if(code != null){
//                final List<Instruction> instructions = ClassFormat.format(code.getRawCode());
//                for(final Instruction next : instructions){
//                    System.out.print("    " + next.getPc() + ": " + next.getOpcode().getMnemonic() + " ");
//                    sb.append(next.getPc()).append(": ").append(next.getOpcode().getMnemonic()).append(" ");
//                    for(int i = 0; i < next.getOperandCount(); i++){
//                        System.out.print(next.getOperands().get(i).getValue());
//                        sb.append(next.getOperands().get(i).getValue());
//                        if(i < next.getOperandCount() - 1){
//                            System.out.print(", ");
//                            sb.append(", ");
//                        }
//                    }
//                    System.out.println();
//                    sb.append("\n");
//                }
//                System.out.println("}");
//                sb.append("}\n");
//            }
//            sb.append("\n");
//        }
//        sb.append(" ");
//
//        this.textEditor.setText(sb.toString());
    }

}
