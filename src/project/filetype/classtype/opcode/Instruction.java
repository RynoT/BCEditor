package project.filetype.classtype.opcode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan Thomson on 28/12/2016.
 */
public class Instruction {

    private final Opcode opcode;
    private final int pc;

    private final List<Operand> operands = new ArrayList<>(4);

    public Instruction(final Opcode opcode, final int pc){
        this.opcode = opcode;
        this.pc = pc;
    }

    public int getPc(){
        return this.pc;
    }

    public Opcode getOpcode(){
        return this.opcode;
    }

    public int getOperandCount(){
        return this.operands.size();
    }

    public List<Operand> getOperands(){
        return this.operands;
    }
}
