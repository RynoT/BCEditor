package project.filetype.classtype.bytecode.interpreter;

import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.bytecode.BytecodeAnalyzer;
import project.filetype.classtype.bytecode.Instruction;
import project.filetype.classtype.bytecode.interpreter.item.ArrayItem;
import project.filetype.classtype.bytecode.interpreter.item.MethodItem;
import project.filetype.classtype.bytecode.interpreter.item.NumberItem;
import project.filetype.classtype.bytecode.interpreter.item.ObjectItem;
import project.filetype.classtype.bytecode.opcode.Opcode;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._Code;

/**
 * Created by Ryan Thomson on 18/01/2017.
 */
public class BytecodeInterpreter {

    private BytecodeInterpreter() {
    }

    //public static void interpret(final List<Instruction> instructions, final int maxStack, final int maxLocal, final boolean isStatic) {
    public static void interpret(final BytecodeAnalyzer analyzer, final ConstantPool pool) {
        final MethodInfo method = analyzer.getMethod();
        final _Code code = (_Code) AttributeInfo.findFirst(AttributeInfo.CODE, method.getAttributes(), pool);
        if(code == null) {
            return;
        }
        // Reset attributes for instructions
        analyzer.getInstructions().forEach(instruction -> instruction.setAttributes(0x0));

        final MethodStack stack = new MethodStack(code.getMaxStack());
        final MethodLocal local = new MethodLocal(code.getMaxLocals());
        if(!AccessFlags.containsFlag(method.getAccessFlags(), AccessFlags.ACC_STATIC)) { //if method is not static
            local.set(new ObjectItem(null, "this"), 0); //local 0 is always 'this' for non-static methods
        }
        for(final Instruction instruction : analyzer.getInstructions()) {
            final Opcode opcode = instruction.getOpcode();
            switch(opcode) {
                // Const values
                case _iconst_m1:
                    stack.push(new NumberItem(instruction, String.valueOf(-1), NumberItem.NumberType.INTEGER));
                    break;
                case _iconst_0:
                case _iconst_1:
                case _iconst_2:
                case _iconst_3:
                case _iconst_4:
                case _iconst_5:
                    stack.push(new NumberItem(instruction, BytecodeInterpreter.getOpcodeIndex(opcode), NumberItem.NumberType.INTEGER));
                    break;
                case _fconst_0:
                case _fconst_1:
                case _fconst_2:
                    stack.push(new NumberItem(instruction, BytecodeInterpreter.getOpcodeIndex(opcode), NumberItem.NumberType.FLOAT));
                    break;
                case _lconst_0:
                case _lconst_1:
                    stack.push(new NumberItem(instruction, BytecodeInterpreter.getOpcodeIndex(opcode), NumberItem.NumberType.LONG));
                    break;
                case _dconst_0:
                case _dconst_1:
                    stack.push(new NumberItem(instruction, BytecodeInterpreter.getOpcodeIndex(opcode), NumberItem.NumberType.DOUBLE));
                    break;
                // Load values
                case _iload_0:
                case _iload_1:
                case _iload_2:
                case _iload_3:
                    
                    break;
            }
        }
    }

    private static String getOpcodeIndex(final Opcode opcode){
        return opcode.name().substring(opcode.name().length() - 1);
    }
}
