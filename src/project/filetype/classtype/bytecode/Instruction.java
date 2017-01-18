package project.filetype.classtype.bytecode;

import project.filetype.classtype.bytecode.opcode.Opcode;
import project.filetype.classtype.bytecode.opcode.Operand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan Thomson on 28/12/2016.
 */
public class Instruction {

    public static final int ATTRIBUTE_REDUNDANT = 0b0000_0001;
    public static final int ATTRIBUTE_LOOP = 0b0000_0010;
    public static final int ATTRIBUTE_CASE = 0b0000_0100;
    public static final int ATTRIBUTE_ERROR = 0b0000_1000;

    private final Opcode opcode;
    private final int pc;

    private final List<Operand> operands = new ArrayList<>(4);

    private int attributes = 0x0; //used by analyzer
    private String errorMessage = null;

    public Instruction(final Opcode opcode, final int pc) {
        this.opcode = opcode;
        this.pc = pc;
    }

    public int getPc() {
        return this.pc;
    }

    public Opcode getOpcode() {
        return this.opcode;
    }

    public int getAttributes() {
        return this.attributes;
    }

    public int getOperandCount() {
        return this.operands.size();
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public List<Operand> getOperands() {
        return this.operands;
    }

    public void setErrorMessage(final String error) {
        this.errorMessage = error;
    }

    public void setAttributes(final int attributes) {
        this.attributes = attributes;
    }

    public boolean isAttributeSet(final int attribute) { //used for text editor
        return (this.attributes & attribute) == attribute;
    }
}
