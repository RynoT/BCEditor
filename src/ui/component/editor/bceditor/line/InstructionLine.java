package ui.component.editor.bceditor.line;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.opcode.Instruction;
import project.filetype.classtype.opcode.Operand;
import project.filetype.classtype.opcode.OperandType;

import java.awt.font.TextAttribute;

/**
 * Created by Ryan Thomson on 30/12/2016.
 */
public class InstructionLine extends Line {

    private final Instruction instruction;
    private final MethodLine methodLine;
    private final ConstantPool pool;

    public InstructionLine(final Instruction instruction, final MethodLine methodLine, final ConstantPool pool, final int indent) {
        super(indent);

        assert instruction != null && methodLine != null && pool != null;
        this.instruction = instruction;
        this.methodLine = methodLine;
        this.pool = pool;

        final StringBuilder sb = new StringBuilder(instruction.getOpcode().getMnemonic());
        if(instruction.getOperands().size() > 0){
            sb.append(" ");
            for(int i = 0; i < instruction.getOperandCount(); i++){
                final Operand operand = instruction.getOperands().get(i);
                if(operand.getType() == OperandType.INDEX_POOL) {
                    sb.append("<").append(operand.getValue()).append(">");
                } else if(operand.getType() == OperandType.BRANCH_OFFSET) {
                    sb.append("#").append(instruction.getPc() + operand.getValue());
                } else {
                    sb.append(operand.getValue());
                }
                if(i < instruction.getOperandCount() - 1){
                    sb.append(", ");
                }
            }
        }
        super.setString(sb.toString());
    }

    public Instruction getInstruction(){
        return this.instruction;
    }

    public MethodLine getMethodLine(){
        return this.methodLine;
    }

//    @Override
//    public void stylize() {
//        final String mnemonic = this.instruction.getOpcode().getMnemonic();
//        if(mnemonic.contains("return")) {
//            super.attributes.addAttribute(TextAttribute.FOREGROUND, Line.KEYWORD_COLOR_MAP
//                    .get("return"), 0, mnemonic.length());
//        }
//    }

    @Override
    public void update() {

    }
}
