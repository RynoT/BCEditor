package project.filetype.classtype;

import project.filetype.ClassType;
import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.Descriptor;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.FieldInfo;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._ConstantValue;
import project.filetype.classtype.member.attributes._Exceptions;
import project.filetype.classtype.member.attributes._Signature;
import project.filetype.classtype.opcode.Instruction;
import project.filetype.classtype.opcode.Opcode;
import project.filetype.classtype.opcode.Operand;
import project.filetype.classtype.opcode.OperandType;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan Thomson on 28/12/2016.
 */
public class ClassFormat {

    private ClassFormat() {
    }

    //TODO move
    //Format raw bytecode into formatted instructions
    public static List<Instruction> format(final byte[] code) {
        final List<Instruction> instructions = new ArrayList<>();
        try(final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(code))){
            final int count = dis.available();
            while(dis.available() > 0) {
                final int pc = count - dis.available();
                final Opcode opcode = Opcode.get(dis.readUnsignedByte());
                assert (opcode != null);
                final Instruction instruction = new Instruction(opcode, pc);
                final List<Operand> operands = instruction.getOperands();
                switch(opcode.getType()) {
                    case INDEX_POOL:
                    case INDEX_LOCAL:
                        operands.add(new Operand(dis, false, opcode.getOtherBytes(), opcode.getType()));
                        break;
                    case BRANCH_OFFSET:
                    case CONSTANT:
                        operands.add(new Operand(dis, true, opcode.getOtherBytes(), opcode.getType()));
                        break;
                    case UNDEFINED:
                        switch(opcode){
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
                                if(wideOpcode.getValue() == Opcode._iinc.getOpcode()){
                                    operands.add(new Operand(dis, true, 2, OperandType.CONSTANT)); //const increment
                                }
                                break;
                            case _tableswitch:
                            case _lookupswitch:
                                dis.skipBytes((4 - ((pc + 1) % 4)) % 4); //padding
                                final int defaultOffset = dis.readInt();
                                operands.add(new Operand(defaultOffset, 4, OperandType.BRANCH_OFFSET));
                                if(opcode == Opcode._tableswitch){
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
                                    for(int i = 0; i < n; i++){
                                        operands.add(new Operand(dis, true, 4, OperandType.CONSTANT)); //match
                                        operands.add(new Operand(dis, true, 4, OperandType.BRANCH_OFFSET)); //offset
                                    }
                                }
                                break;
                            default: assert(false);
                        }
                        break;

                }
                instructions.add(instruction);
            }
        } catch(final IOException e) {
            e.printStackTrace(System.err);
        }
        return instructions;
    }
}
