package project.filetype.classtype.bytecode.interpreter;

import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.Descriptor;
import project.filetype.classtype.bytecode.BytecodeAnalyzer;
import project.filetype.classtype.bytecode.Instruction;
import project.filetype.classtype.bytecode.interpreter.item.*;
import project.filetype.classtype.bytecode.opcode.Opcode;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.member.MethodInfo;
import project.filetype.classtype.member.attributes.AttributeInfo;
import project.filetype.classtype.member.attributes._Code;

import java.util.ArrayList;
import java.util.List;

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

        final boolean isStatic = AccessFlags.containsFlag(method.getAccessFlags(), AccessFlags.ACC_STATIC);
        final MethodStack stack = new MethodStack(code.getMaxStack());
        final MethodLocal local = new MethodLocal(isStatic ? code.getMaxLocals() : code.getMaxLocals() + 1);

        if(!isStatic) { //if method is not static
            local.set(new ObjectItem(null, "this"), 0); //local 0 is always 'this' for non-static methods
        }
        final MethodItem[] parameters = BytecodeInterpreter.getParameters(method, pool);
        if(parameters.length > 0) {
            int index = isStatic ? 0 : 1;
            for(final MethodItem item : parameters) {
                local.set(item, index++);
            }
        }
        //if(method.get)
        for(final Instruction instruction : analyzer.getInstructions()) {
            final Opcode opcode = instruction.getOpcode();
            switch(opcode) {
                // Const mnemonics
                case _iconst_m1:
                    //m1 is special and will not work with our processConst algorithm. We must handle it separately.
                    BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, String.valueOf(-1), PrimitiveType.INTEGER));
                    break;
                case _iconst_0:
                case _iconst_1:
                case _iconst_2:
                case _iconst_3:
                case _iconst_4:
                case _iconst_5:
                case _fconst_0:
                case _fconst_1:
                case _fconst_2:
                case _lconst_0:
                case _lconst_1:
                case _dconst_0:
                case _dconst_1:
                    BytecodeInterpreter.processConst(instruction, stack);
                    break;

                // Load mnemonics
                case _iload:
                case _fload:
                case _lload:
                case _dload:
                    BytecodeInterpreter.processLoad(instruction, local, stack, false);
                    break;
                case _iload_0:
                case _iload_1:
                case _iload_2:
                case _iload_3:
                case _fload_0:
                case _fload_1:
                case _fload_2:
                case _fload_3:
                case _lload_0:
                case _lload_1:
                case _lload_2:
                case _lload_3:
                case _dload_0:
                case _dload_1:
                case _dload_2:
                case _dload_3:
                    BytecodeInterpreter.processLoad(instruction, local, stack, true);
                    break;

                // Store mnemonics
                case _istore:
                case _fstore:
                case _lstore:
                case _dstore:
                    BytecodeInterpreter.processStore(instruction, local, stack, false);
                    break;
                case _istore_0:
                case _istore_1:
                case _istore_2:
                case _istore_3:
                case _fstore_0:
                case _fstore_1:
                case _fstore_2:
                case _fstore_3:
                case _lstore_0:
                case _lstore_1:
                case _lstore_2:
                case _lstore_3:
                case _dstore_0:
                case _dstore_1:
                case _dstore_2:
                case _dstore_3:
                    BytecodeInterpreter.processStore(instruction, local, stack, true);
                    break;
            }
        }
    }

    private static void setError(final Instruction instruction, final String message) {
        instruction.setAttributes(Instruction.ATTRIBUTE_ERROR);
        instruction.setErrorMessage(message);
        System.out.println("[BytecodeInterpreter] Error: " + message + " (instruction: " + instruction.toString() + ")");
    }

    private static String getOpcodeIndex(final Opcode opcode) {
        return opcode.name().substring(opcode.name().length() - 1);
    }

    private static int getIndex(final Instruction instruction, final boolean predefined) {
        final int index;
        // Get index of local entry
        if(predefined) {
            index = Integer.parseInt(BytecodeInterpreter.getOpcodeIndex(instruction.getOpcode()));
        } else if(instruction.getOperandCount() == 0) {
            BytecodeInterpreter.setError(instruction, "Index operand not set");
            return -1;
        } else {
            index = instruction.getOperands().get(0).getValue();
            if(index < 0) {
                BytecodeInterpreter.setError(instruction, "Invalid index operand (must be 0 or greater)");
                return -1;
            }
        }
        return index;
    }

    private static void processConst(final Instruction instruction, final MethodStack stack) {
        final String value = BytecodeInterpreter.getOpcodeIndex(instruction.getOpcode());
        assert value.length() == 1 : "Invalid value: " + value;
        final PrimitiveType type = PrimitiveType.get(instruction.getOpcode().name().charAt(1));
        assert type != null : "Invalid opcode: " + instruction.getOpcode().name();
        stack.push(new NumberItem(instruction, value, type));
    }

    private static void processStackPush(final Instruction instruction, final MethodStack stack, final MethodItem item){
        if(!stack.push(item)){
            instruction.setErrorMessage("Stack is full");
        }
    }

    private static void processLoad(final Instruction instruction, final MethodLocal local, final MethodStack stack, final boolean predefined) {
        final int index = BytecodeInterpreter.getIndex(instruction, predefined);
        if(index == -1) {
            BytecodeInterpreter.processStackPush(instruction, stack, new InvalidItem(instruction));
            return;
        }
        final PrimitiveType type = PrimitiveType.get(instruction.getOpcode().name().charAt(1));
        assert type != null : "Invalid opcode: " + instruction.getOpcode().name();

        final MethodItem item = local.get(index);
        if(item == null) {
            BytecodeInterpreter.processStackPush(instruction, stack, new InvalidItem(instruction));
            BytecodeInterpreter.setError(instruction, "Local " + index + " is not set or doesn't exist");
        } else if(!(item instanceof NumberItem) || type != item.getType()) {
            BytecodeInterpreter.processStackPush(instruction, stack, new InvalidItem(instruction));
            BytecodeInterpreter.setError(instruction, type.name() + " cannot be used to load local of type " + item.getType().name());
        } else {
            BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, item.getValue(), type));
        }
    }

    private static void processStore(final Instruction instruction, final MethodLocal local, final MethodStack stack, final boolean predefined) {
        final int index = BytecodeInterpreter.getIndex(instruction, predefined);
        if(index == -1) {
            stack.push(new InvalidItem(instruction));
            return;
        }
        final MethodItem item = stack.peek();
        if(item == null) {
            BytecodeInterpreter.setError(instruction, "Stack is empty");
        } else {
            final PrimitiveType type = PrimitiveType.get(instruction.getOpcode().name().charAt(1));
            assert type != null;
            if(type != item.getType()) {
                BytecodeInterpreter.setError(instruction, type.name() + " cannot be used to store element of type " + item.getType().name());
                return;
            }
            local.set(stack.pop(), index);
        }
    }

    private static MethodItem[] getParameters(final MethodInfo method, final ConstantPool pool) {
        final List<MethodItem> items = new ArrayList<>();
        final String descriptor = Descriptor.decode(method.getTagDescriptor(pool).getValue());
        for(final String sp : descriptor.substring(1, descriptor.lastIndexOf(')')).split(",\\s")) {
            int dimensions = 0;
            for(int i = 0; i < sp.length(); i++) {
                if(sp.charAt(i) == '[') {
                    dimensions++;
                }
            }
            final PrimitiveType type = PrimitiveType.get(sp);
            if(dimensions != 0) {
                items.add(new ArrayRefItem(null, type, dimensions));
            } else if(type != PrimitiveType.OBJECT) {
                items.add(new NumberItem(null, null, type));
            } else { //if type is object
                items.add(new ObjectItem(null, null));
            }
        }
        return items.toArray(new MethodItem[items.size()]);
    }
}
