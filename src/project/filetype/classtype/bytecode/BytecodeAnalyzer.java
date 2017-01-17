package project.filetype.classtype.bytecode;

import project.filetype.classtype.bytecode.opcode.Opcode;
import project.filetype.classtype.bytecode.opcode.Operand;
import project.filetype.classtype.bytecode.opcode.OperandType;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._Code;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan Thomson on 17/01/2017.
 */
public class BytecodeAnalyzer {

    private final MethodInfo method;
    private final List<Instruction> instructions = new ArrayList<>();

    public BytecodeAnalyzer(final MethodInfo method) {
        assert method != null;

        this.method = method;
    }

    public int getInstructionCount() {
        return this.instructions.size();
    }

    public List<Instruction> getInstructions() {
        return this.instructions;
    }

    public void analyze(final ConstantPool pool) {
        final _Code code = (_Code) AttributeInfo.findFirst(AttributeInfo.CODE, this.method.getAttributes(), pool);
        if(code == null) {
            return;
        }
        // Turn bytes into readable instructions
        this.format(code.getRawCode());

        assert this.instructions.size() > 0; //should always be one or more
        this.instructions.get(0).attributes = Instruction.ATTRIBUTE_ERROR;
    }

    private void format(final byte[] code) {
        assert this.instructions.size() == 0;

        try(final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(code))) {
            final int count = dis.available();
            while(dis.available() > 0) {
                final int pc = count - dis.available();
                final Opcode opcode = Opcode.get(dis.readUnsignedByte());
                assert opcode != null;

                final Instruction instruction = new Instruction(opcode, pc);
                this.instructions.add(instruction);

                // Decode operands
                final List<Operand> operands = instruction.getOperands();
                switch(opcode.getType()) {
                    case NO_OPERAND:
                        continue;
                    case INDEX_POOL:
                    case INDEX_LOCAL:
                        operands.add(new Operand(dis, false, opcode.getOtherBytes(), opcode.getType()));
                        continue;
                    case BRANCH_OFFSET:
                    case CONSTANT:
                        operands.add(new Operand(dis, true, opcode.getOtherBytes(), opcode.getType()));
                        continue;
                }
                assert opcode.getType() == OperandType.UNDEFINED : opcode.getType().name();

                // If we get here then we have an undefined operand. These operands are special-case and must be handled uniquely.
                switch(opcode) {
                    case _iinc:
                        operands.add(new Operand(dis, false, 1, OperandType.INDEX_LOCAL)); //index local
                        operands.add(new Operand(dis, true, 1, OperandType.CONSTANT)); //const increment
                        break;
                    case _invokedynamic:
                    case _invokeinterface:
                        operands.add(new Operand(dis, false, 2, OperandType.INDEX_POOL)); //index pool
                        operands.add(new Operand(dis, false, 1, OperandType.CONSTANT)); //0 or count
                        operands.add(new Operand(dis, false, 1, OperandType.CONSTANT)); //0
                        break;
                    case _multianewarray:
                        operands.add(new Operand(dis, false, 2, OperandType.INDEX_POOL)); //index pool
                        operands.add(new Operand(dis, false, 1, OperandType.CONSTANT)); //dimensions
                        break;
                    case _wide:
                        final Operand wideOpcode = new Operand(dis, false, 1, OperandType.CONSTANT);
                        operands.add(wideOpcode); //opcode
                        operands.add(new Operand(dis, false, 2, OperandType.INDEX_POOL)); //index local
                        if(wideOpcode.getValue() == Opcode._iinc.getOpcode()) {
                            operands.add(new Operand(dis, true, 2, OperandType.CONSTANT)); //const increment
                        }
                        break;
                    case _tableswitch:
                    case _lookupswitch:
                        dis.skipBytes((4 - ((pc + 1) % 4)) % 4); //padding
                        final int defaultOffset = dis.readInt();
                        operands.add(new Operand(defaultOffset, 4, OperandType.BRANCH_OFFSET));
                        if(opcode == Opcode._tableswitch) {
                            final int low = dis.readInt(), high = dis.readInt();
                            operands.add(new Operand(low, 4, OperandType.CONSTANT));
                            operands.add(new Operand(high, 4, OperandType.CONSTANT));
                            for(int k = 0; k < high - low + 1; k++) {
                                operands.add(new Operand(dis, true, 4, OperandType.BRANCH_OFFSET)); //jump offset
                            }
                        } else { //if Opcode._lookupswitch
                            final int n = dis.readInt(); //number of pairs
                            assert (n >= 0);
                            operands.add(new Operand(n, 4, OperandType.CONSTANT));
                            for(int i = 0; i < n; i++) {
                                operands.add(new Operand(dis, true, 4, OperandType.CONSTANT)); //match
                                operands.add(new Operand(dis, true, 4, OperandType.BRANCH_OFFSET)); //offset
                            }
                        }
                        break;
                    default:
                        assert false;
                }
            }
        } catch(final IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
