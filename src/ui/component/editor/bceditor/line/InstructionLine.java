package ui.component.editor.bceditor.line;

import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.opcode.Instruction;
import project.filetype.classtype.opcode.Operand;
import project.filetype.classtype.opcode.OperandType;
import ui.component.editor.bceditor.IBCEditor;
import ui.component.editor.bceditor.IBCTextEditor;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.List;

/**
 * Created by Ryan Thomson on 30/12/2016.
 */
public class InstructionLine extends Line {

    private final Instruction instruction;
    private final MethodLine methodLine;

    public InstructionLine(final Instruction instruction, final MethodLine methodLine, final int indent) {
        super(indent);

        assert instruction != null && methodLine != null;
        this.instruction = instruction;
        this.methodLine = methodLine;
    }

    public Instruction getInstruction() {
        return this.instruction;
    }

    public MethodLine getMethodLine() {
        return this.methodLine;
    }

    @Override
    public void update(final ConstantPool pool) {
        final StringBuilder sb = new StringBuilder();
        final String mnemonic = this.instruction.getOpcode().getMnemonic();
        sb.append(mnemonic);
        if(this.instruction.getOperandCount() > 0) {
            sb.append(" ");
            for(int i = 0; i < this.instruction.getOperandCount(); i++) {
                final Operand operand = this.instruction.getOperands().get(i);
                if(operand.getType() == OperandType.INDEX_POOL) {
                    sb.append(IBCEditor.formatIndexPool(operand.getValue()));
                } else if(operand.getType() == OperandType.INDEX_LOCAL){
                    sb.append(IBCEditor.formatIndexLocal(operand.getValue()));
                } else if(operand.getType() == OperandType.BRANCH_OFFSET) {
                    sb.append(IBCEditor.formatBranch(operand.getValue() + this.instruction.getPc()));
                } else {
                    sb.append(operand.getValue());
                }
                if(i < this.instruction.getOperandCount() - 1) {
                    sb.append(", ");
                }
            }
        }

        final String str = sb.toString();
        super.setString(str);

        if(mnemonic.contains("return")) {
            super.attributes.addAttribute(TextAttribute.FOREGROUND, Line.ORANGE_COLOR, 0, mnemonic.length());
        }
        if(this.instruction.getOperandCount() > 0){
            int offset = mnemonic.length() + 1;
            final String[] operands = str.substring(offset).split("\\s");
            assert operands.length == this.instruction.getOperandCount();
            for(int i = 0; i < operands.length; i++){
                final OperandType type = this.instruction.getOperands().get(i).getType();
                int length = operands[i].length();
                if(i < operands.length - 1){
                    length -= 1; //accommodate for comma
                }
                if(type == OperandType.BRANCH_OFFSET){
                    super.attributes.addAttribute(TextAttribute.FOREGROUND, IBCTextEditor
                            .BRANCH_COLOR, offset, offset + length);
                } else if(type == OperandType.INDEX_POOL){
                    super.attributes.addAttribute(TextAttribute.FOREGROUND, IBCTextEditor
                            .INDEX_POOL_COLOR, offset, offset + length);
                } else if(type == OperandType.INDEX_LOCAL || type == OperandType.CONSTANT){
                    super.attributes.addAttribute(TextAttribute.FOREGROUND, IBCTextEditor
                            .INDEX_LOCAL_COLOR, offset, offset + length);
                }
                offset += operands[i].length() + 1;
            }
            //Line.colorSymbols(str, super.attributes, mnemonic.length(), str.length());
        }
    }
}
