package project.filetype.classtype.bytecode.interpreter;

import project.filetype.classtype.AccessFlags;
import project.filetype.classtype.Descriptor;
import project.filetype.classtype.bytecode.BytecodeAnalyzer;
import project.filetype.classtype.bytecode.Instruction;
import project.filetype.classtype.bytecode.interpreter.item.*;
import project.filetype.classtype.bytecode.opcode.Opcode;
import project.filetype.classtype.constantpool.ConstantPool;
import project.filetype.classtype.constantpool.PoolTag;
import project.filetype.classtype.constantpool.tag.TagClass;
import project.filetype.classtype.constantpool.tag.TagNameAndType;
import project.filetype.classtype.constantpool.tag.TagRef;
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
        final MethodLocal local = new MethodLocal(code.getMaxLocals(), isStatic);
        //System.out.println("[BytecodeInterpreter] Method: " + method.getTagName(pool).getValue()
        //        + ", MaxStack: " + code.getMaxStack() + ", MaxLocals: " + code.getMaxLocals());

        final MethodItem[] parameters = BytecodeInterpreter.getParameters(method.getTagDescriptor(pool).getValue(), isStatic);
        for(int i = 0; i < parameters.length; i++) {
            local.set(parameters[i], i);
        }
        for(final Instruction instruction : analyzer.getInstructions()) {
            final Opcode opcode = instruction.getOpcode();
            switch(opcode) {
                // Do nothing mnemonics
                case _nop:
                case _breakpoint:
                case _impdep1:
                case _impdep2:
                    break;

                // Get mnemonics
                case _getfield:
                case _getstatic:
                case _putfield:
                case _putstatic:
                    BytecodeInterpreter.processGetAndPut(instruction, stack, pool);
                    break;

                // Invoke mnemonics
                case _invokestatic:
                case _invokedynamic:
                case _invokespecial:
                case _invokevirtual:
                case _invokeinterface:
                    BytecodeInterpreter.processInvoke(instruction, stack, pool);
                    break;

                // Pop mnemonics
                case _pop:
                case _pop2:
                    BytecodeInterpreter.processPop(instruction, stack);
                    break;

                // Dup mnemonics
                case _dup:
                case _dup_x1:
                case _dup_x2:
                    BytecodeInterpreter.processDup(instruction, stack);
                    break;
                case _dup2:
                case _dup2_x1:
                case _dup2_x2:
                    BytecodeInterpreter.processDup2(instruction, stack);
                    break;

                // New array mnemonics
                case _newarray:
                case _anewarray:
                    BytecodeInterpreter.processNewArray(instruction, stack, pool);
                    break;

                // Array length mnemonic
                case _arraylength: {
                    final MethodItem item = stack.peek();
                    if(item == null) {
                        BytecodeInterpreter.setError(instruction, "Stack is empty");
                        return;
                    }
                    if(!(item instanceof ArrayRefItem)) {
                        BytecodeInterpreter.setError(instruction, "Stack item must be array ref");
                        return;
                    }
                    stack.pop();
                    stack.push(new NumberItem(instruction, item.getValue() + ".length", PrimitiveType.INTEGER));
                    System.out.println(item.getValue() + ".length");
                    break;
                }

                // Checkcast and Instanceof mnemonic
                case _checkcast:
                case _instanceof:
                    BytecodeInterpreter.processInstanceofAndCast(instruction, stack, pool);
                    break;

                // Swap mnemonic
                case _swap:
                    if(stack.getCount() < 2) {
                        BytecodeInterpreter.setError(instruction, "There must be at least two items on the stack");
                        break;
                    }
                    assert stack.get(0) != null && stack.get(1) != null;
                    if(stack.get(0).getType().getStackSize() == 2 || stack.get(1).getType().getStackSize() == 2) {
                        BytecodeInterpreter.setError(instruction, "Swap cannot be used for items which take two slots");
                        break;
                    }
                    stack.swap(0, 1);
                    break;

                // Monitor mnemonic
                case _monitorenter:
                case _monitorexit: {
                    if(stack.getCount() < 1) {
                        BytecodeInterpreter.setError(instruction, "Requires object reference");
                        break;
                    }
                    final MethodItem item = stack.peek();
                    if(item == null || item.getType() != PrimitiveType.OBJECT) {
                        BytecodeInterpreter.setError(instruction, "Requires object reference");
                        break;
                    }
                    stack.pop();
                    break;
                }

                // New mnemonic
                case _new: {
                    if(instruction.getOperandCount() == 0) {
                        BytecodeInterpreter.setError(instruction, "ConstantPool index operand required");
                        BytecodeInterpreter.processStackPush(instruction, stack, new InvalidItem(instruction));
                        break;
                    }
                    final PoolTag tag = pool.getEntry(instruction.getOperand(0).getValue());
                    if(tag == null || tag.getPoolTagId() != PoolTag.TAG_CLASS) {
                        BytecodeInterpreter.setError(instruction, "ConstantPool index must be to Class Ref");
                        BytecodeInterpreter.processStackPush(instruction, stack, new InvalidItem(instruction));
                        break;
                    }
                    BytecodeInterpreter.processStackPush(instruction, stack, new ObjectItem(instruction, "new " + tag.getContentString(pool)));
                    break;
                }

                // Throw mnemonic
                case _athrow: {
                    if(stack.getCount() < 1) {
                        BytecodeInterpreter.setError(instruction, "Requires object reference");
                        break;
                    }
                    final MethodItem item = stack.peek();
                    if(item == null || item.getType() != PrimitiveType.OBJECT) {
                        BytecodeInterpreter.setError(instruction, "Requires object reference");
                        break;
                    }
                    stack.empty();
                    BytecodeInterpreter.processStackPush(instruction, stack, new ObjectItem(instruction, "throw " + item.getValue()));
                    break;
                }

                // Push mnemonics
                case _bipush:
                case _sipush:
                    if(instruction.getOperandCount() == 0) {
                        BytecodeInterpreter.setError(instruction, "No operand");
                        BytecodeInterpreter.processStackPush(instruction, stack, new InvalidItem(instruction));
                    } else {
                        BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction,
                                String.valueOf(instruction.getOperands().get(0).getValue()), PrimitiveType.INTEGER));
                    }
                    break;

                // LDC mnemonics
                case _ldc:
                case _ldc_w:
                case _ldc2_w:
                    BytecodeInterpreter.processLdc(instruction, stack, pool);
                    break;

                // Return mnemonics
                case _return:
                case _areturn:
                case _ireturn:
                case _freturn:
                case _lreturn:
                case _dreturn:
                    BytecodeInterpreter.processReturn(instruction, stack);
                    break;
                case _ret:
                    if(instruction.getOperandCount() == 0){
                        BytecodeInterpreter.setError(instruction, "Index operand required");
                    }
                    break;

                // Goto mnemonics
                case _goto:
                case _goto_w:
                    if(instruction.getOperandCount() == 0){
                        BytecodeInterpreter.setError(instruction, "Branch operand required");
                    }
                    break;

                // If mnemonics
                case _ifeq:
                case _ifge:
                case _ifgt:
                case _ifle:
                case _iflt:
                case _ifne:
                case _ifnull:
                case _ifnonnull:
                case _if_acmpeq:
                case _if_acmpne:
                case _if_icmpeq:
                case _if_icmpge:
                case _if_icmpgt:
                case _if_icmple:
                case _if_icmplt:
                case _if_icmpne:
                    BytecodeInterpreter.processIf(instruction, stack);
                    break;

                // Cast mnemonics
                case _i2b:
                case _i2c:
                case _i2d:
                case _i2f:
                case _i2l:
                case _i2s:
                case _d2f:
                case _d2i:
                case _d2l:
                case _f2d:
                case _f2i:
                case _f2l:
                case _l2d:
                case _l2f:
                case _l2i:
                    BytecodeInterpreter.processCast(instruction, stack);
                    break;

                    // Const mnemonics
                case _aconst_null:
                    BytecodeInterpreter.processStackPush(instruction, stack, new ObjectItem(instruction, "null"));
                    break;
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
                case _aload:
                case _iload:
                case _fload:
                case _lload:
                case _dload:
                    BytecodeInterpreter.processLoad(instruction, local, stack, false);
                    break;
                case _aload_0:
                case _aload_1:
                case _aload_2:
                case _aload_3:
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
                case _aaload:
                case _baload:
                case _caload:
                case _faload:
                case _daload:
                case _saload:
                case _iaload:
                case _laload:
                    BytecodeInterpreter.processArrayLoad(instruction, stack);
                    break;

                // Store mnemonics
                case _astore:
                case _istore:
                case _fstore:
                case _lstore:
                case _dstore:
                    BytecodeInterpreter.processStore(instruction, local, stack, false);
                    break;
                case _astore_0:
                case _astore_1:
                case _astore_2:
                case _astore_3:
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
                case _aastore:
                case _bastore:
                case _castore:
                case _fastore:
                case _dastore:
                case _sastore:
                case _iastore:
                case _lastore:
                    BytecodeInterpreter.processArrayStore(instruction, stack);
                    break;

                case _dcmpg:
                case _dcmpl:
                case _lcmp:
                case _fcmpg:
                case _fcmpl:
                    BytecodeInterpreter.processCmp(instruction, stack);
                    break;

                // Math mnemonics
                case _iadd:
                case _isub:
                case _imul:
                case _idiv:
                case _ineg:
                case _irem:
                case _iand:
                case _ior:
                case _ishl:
                case _ishr:
                case _iushr:
                case _ixor:
                case _iinc:
                case _fadd:
                case _fsub:
                case _fmul:
                case _fdiv:
                case _fneg:
                case _frem:
                case _ladd:
                case _lsub:
                case _lmul:
                case _ldiv:
                case _lneg:
                case _lrem:
                case _land:
                case _lor:
                case _lshl:
                case _lshr:
                case _lushr:
                case _lxor:
                case _dadd:
                case _dsub:
                case _dmul:
                case _ddiv:
                case _dneg:
                case _drem:
                    BytecodeInterpreter.processMath(instruction, stack);
                    break;

                default:
                    BytecodeInterpreter.setError(instruction, "Unsupported opcode: " + opcode.name().substring(1));
            }
        }
    }

    private static void setError(final Instruction instruction, final String message) {
        instruction.setAttributes(Instruction.ATTRIBUTE_ERROR);
        instruction.setErrorMessage(message);
        System.err.println("[BytecodeInterpreter] Error: " + message + " (instruction: " + instruction.toString() + ")");
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

    private static int getDimensionsCount(final String descriptorEncoded) {
        int count = 0;
        for(int i = 0; i < descriptorEncoded.length(); i++) {
            if(descriptorEncoded.charAt(i) == '[') {
                count++;
            }
        }
        return count;
    }

    private static String[] getParameters(final String descriptorEncoded) {
        final String descriptor = Descriptor.decode(descriptorEncoded);
        if(descriptor.startsWith("()")) {
            return new String[0];
        }
        return descriptor.substring(1, descriptor.lastIndexOf(')')).split(",\\s");
    }

    private static MethodItem[] getParameters(final String descriptorEncoded, final boolean isStatic) {
        final List<MethodItem> items = new ArrayList<>();
        if(!isStatic) {
            items.add(new ObjectItem(null, "this")); //local 0 is always 'this' for non-static methods
        }
        for(final String sp : BytecodeInterpreter.getParameters(descriptorEncoded)) {
            assert !sp.equals("");
            final int dimensions = BytecodeInterpreter.getDimensionsCount(sp);
            final PrimitiveType type = PrimitiveType.get(sp);
            final String value = "local" + items.size();
            if(dimensions != 0) {
                items.add(new ArrayRefItem(null, null, type, dimensions));
            } else if(type != PrimitiveType.OBJECT) {
                items.add(new NumberItem(null, value, type));
            } else { //if type is object
                items.add(new ObjectItem(null, value));
            }
        }
        return items.toArray(new MethodItem[items.size()]);
    }

    private static void processConst(final Instruction instruction, final MethodStack stack) {
        final String value = BytecodeInterpreter.getOpcodeIndex(instruction.getOpcode());
        assert value.length() == 1 : "Invalid value: " + value;
        final PrimitiveType type = PrimitiveType.get(instruction.getOpcode());
        assert type != null : "Invalid opcode: " + instruction.getOpcode().name();
        BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, value, type));
    }

    private static void processStackPush(final Instruction instruction, final MethodStack stack, final MethodItem item) {
        boolean success = true;
        if(item.getType().getStackSize() == 2) {
            success = stack.push(new FillerItem());
        }
        if(!success || !stack.push(item)) {
            BytecodeInterpreter.setError(instruction, "Stack is full");

            // If we managed to push the filler, we must remove it since the item failed to push
            if(success && item.getType().getStackSize() == 2) {
                stack.pop();
            }
        }
    }

    private static void processPop(final Instruction instruction, final MethodStack stack) {
        if(instruction.getOpcode() == Opcode._pop && stack.getCount() < 1) {
            BytecodeInterpreter.setError(instruction, "Stack is empty");
            return;
        }
        if(instruction.getOpcode() == Opcode._pop2 && stack.getCount() < 2) {
            BytecodeInterpreter.setError(instruction, "Stack must have at least two items");
            return;
        }
        MethodItem item = stack.pop();
        assert item != null;
        if(instruction.getOpcode() == Opcode._pop2) {
            item = stack.peek();
            assert item != null;
            if(item.getType() == PrimitiveType.LONG || item.getType() == PrimitiveType.DOUBLE) {
                BytecodeInterpreter.setError(instruction, "Second stack item of type "
                        + item.getType() + " cannot be removed this way");
                return;
            }
            // If first item is a long or double then this item will be a filler item
            stack.pop();
        }
    }

    private static void processArrayLoad(final Instruction instruction, final MethodStack stack) {
        if(stack.getCount() < 2) {
            BytecodeInterpreter.setError(instruction, "Arrayref and index stack items required");
            return;
        }
        final MethodItem index = stack.pop();
        assert index != null;
        if(index.getType() != PrimitiveType.INTEGER) {
            BytecodeInterpreter.setError(instruction, "Index must be of type " + PrimitiveType.INTEGER + " not " + index.getType());
            return;
        }
        final MethodItem arrayref = stack.pop();
        assert arrayref != null;
        if(!(arrayref instanceof ArrayRefItem)) {
            BytecodeInterpreter.setError(instruction, "Arrayref required");
            return;
        }
        final PrimitiveType type = PrimitiveType.get(instruction.getOpcode());
        if(type != arrayref.getType()) {
            BytecodeInterpreter.setError(instruction, "This mnemonic cannot push type " + arrayref.getType());
            return;
        }
        final String value = arrayref.getValue() + "[" + index.getValue() + "]";
        if(type == PrimitiveType.OBJECT) {
            BytecodeInterpreter.processStackPush(instruction, stack, new ObjectItem(instruction, value));
        } else {
            BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, value, type));
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
        } else if(type != item.getType()) {
            BytecodeInterpreter.processStackPush(instruction, stack, new InvalidItem(instruction));
            BytecodeInterpreter.setError(instruction, type.name() + " cannot be used to load local of type " + item.getType().name());
        } else {
            final String value = local.getLocalName(index);
            if(type == PrimitiveType.OBJECT) {
                BytecodeInterpreter.processStackPush(instruction, stack, new ObjectItem(instruction, value));
            } else {
                BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, value, type));
            }
        }
    }

    private static void processArrayStore(final Instruction instruction, final MethodStack stack) {
        if(stack.getCount() < 3) {
            BytecodeInterpreter.setError(instruction, "Arrayref, index, and value stack items required");
            return;
        }
        final MethodItem value = stack.pop(), index = stack.pop();
        assert value != null && index != null;
        if(index.getType() != PrimitiveType.INTEGER) {
            BytecodeInterpreter.setError(instruction, "Index must be of type " + PrimitiveType.INTEGER + " not " + index.getType());
            return;
        }
        final MethodItem arrayref = stack.pop();
        assert arrayref != null;
        if(!(arrayref instanceof ArrayRefItem)) {
            BytecodeInterpreter.setError(instruction, "Arrayref required");
            return;
        }
        final PrimitiveType type = PrimitiveType.get(instruction.getOpcode());
        if(type != arrayref.getType()) {
            BytecodeInterpreter.setError(instruction, "Expected type " + arrayref.getType() + ", got " + type);
            return;
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
            final PrimitiveType type = PrimitiveType.get(instruction.getOpcode());
            assert type != null;
            if(type != item.getType()) {
                BytecodeInterpreter.setError(instruction, type.name() + " cannot be used to store element of type " + item.getType().name());
                return;
            }
            stack.pop();
            if(item.getType().getStackSize() == 2) {
                assert stack.getCount() > 0;
                stack.pop();
            }
            local.set(item, index);
        }
    }

    private static void processCmp(final Instruction instruction, final MethodStack stack) {
        if(stack.getCount() < 2){
            BytecodeInterpreter.setError(instruction, "Value1 and Value2 stack items required");
            return;
        }
        final PrimitiveType type = PrimitiveType.get(instruction.getOpcode());
        final MethodItem item1 = stack.pop();
        if(item1.getType() != type){
            BytecodeInterpreter.setError(instruction, "Value1 must be of type " + type + ", not " + item1.getType());
            return;
        }
        final MethodItem item2 = stack.pop();
        if(item2.getType() != type){
            BytecodeInterpreter.setError(instruction, "Value2 must be of type " + type + ", not " + item2.getType());
            return;
        }
        final String mnemonic = instruction.getOpcode().getMnemonic();
        String value = item1.getValue() + " ";
        if(type == PrimitiveType.LONG || mnemonic.charAt(mnemonic.length() - 1) == 'g'){
            value += "> ";
        } else {
            value += "< ";
        }
        value += item2.getValue();
        BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, value, PrimitiveType.INTEGER));
    }

    private static void processReturn(final Instruction instruction, final MethodStack stack) {
        if(instruction.getOpcode() == Opcode._return) {
            return; //no action needs to be taken
        }
        final MethodItem item = stack.peek();
        if(item == null) {
            BytecodeInterpreter.setError(instruction, "Stack is empty");
            return;
        }
        final PrimitiveType type = PrimitiveType.get(instruction.getOpcode());
        assert type != null : "Bad opcode: " + instruction.getOpcode().name();
        if(item.getType() != type) {
            BytecodeInterpreter.setError(instruction, item.getType() + " cannot be returned using " + instruction.getOpcode().name().substring(1));
        }
        stack.pop();
        if(item.getType().getStackSize() == 2) {
            assert stack.getCount() > 0;
            stack.pop();
        }
    }

    private static void processInstanceofAndCast(final Instruction instruction, final MethodStack stack, final ConstantPool pool) {
        if(instruction.getOperandCount() == 0) {
            BytecodeInterpreter.setError(instruction, "ConstantPool index operand required");
            return;
        }
        final int index = instruction.getOperand(0).getValue();
        if(index <= 0 || index >= pool.getEntryCount()) {
            BytecodeInterpreter.setError(instruction, "Invalid ConstantPool index");
            return;
        }
        final PoolTag tag = pool.getEntry(index);
        if(!(tag instanceof TagClass)) {
            BytecodeInterpreter.setError(instruction, "ConstantPool entry must be of type " + TagClass.NAME);
            return;
        }
        final MethodItem ref = stack.peek();
        if(ref == null) {
            BytecodeInterpreter.setError(instruction, "Stack is empty");
            return;
        }
        if(ref.getType() != PrimitiveType.OBJECT) {
            BytecodeInterpreter.setError(instruction, "Stack item must be of type " + PrimitiveType.OBJECT);
            return;
        }
        stack.pop();
        if(instruction.getOpcode() == Opcode._checkcast) {
            BytecodeInterpreter.processStackPush(instruction, stack, new ObjectItem(instruction,
                    "(" + tag.getContentString(pool) + ")" + ref.getValue()));
        } else {
            assert instruction.getOpcode() == Opcode._instanceof;
            BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction,
                    ref.getValue() + " instanceof " + tag.getContentString(pool), PrimitiveType.INTEGER));
        }
    }

    private static void processDup(final Instruction instruction, final MethodStack stack) {
        if(stack.getCount() < 1) {
            BytecodeInterpreter.setError(instruction, "No stack items to duplicate");
            return;
        }
        final MethodItem item = stack.peek(); //top
        if(item.getType().getStackSize() == 2) {
            BytecodeInterpreter.setError(instruction, instruction.getOpcode().name().substring(1) + " cannot be used to duplicate item which takes two stack slots");
            return;
        }
        switch(instruction.getOpcode()) {
            case _dup:
                stack.push(item);
                break;
            case _dup_x1:
                if(stack.getCount() <= 1) {
                    BytecodeInterpreter.setError(instruction, "Stack must have at least 2 items in it");
                    return;
                }
                stack.insert(item, 2);
                break;
            case _dup_x2:
                if(stack.getCount() <= 2) {
                    BytecodeInterpreter.setError(instruction, "Stack must have at least 3 items in it");
                    return;
                }
                stack.insert(item, 3);
                break;

            default:
                assert false;
        }
    }

    private static void processDup2(final Instruction instruction, final MethodStack stack) {
        if(stack.getCount() < 2) {
            BytecodeInterpreter.setError(instruction, "Stack must contain at least two items");
            return;
        }
        final MethodItem item1 = stack.get(0), item2 = stack.get(1);
        assert item1 != null && item2 != null;
        switch(instruction.getOpcode()) {
            case _dup2:
                stack.push(item2);
                stack.push(item1);
                break;
            case _dup2_x1:
                if(stack.getCount() <= 3) {
                    BytecodeInterpreter.setError(instruction, "Stack must have at least 3 items in it");
                    return;
                }
                stack.insert(item2, 2);
                stack.insert(item1, 2);
                break;
            case _dup2_x2:
                if(stack.getCount() <= 4) {
                    BytecodeInterpreter.setError(instruction, "Stack must have at least 4 items in it");
                    return;
                }
                stack.insert(item2, 3);
                stack.insert(item1, 3);
                break;

            default:
                assert false;
        }
    }

    private static void processLdc(final Instruction instruction, final MethodStack stack, final ConstantPool pool) {
        if(instruction.getOperandCount() == 0) {
            BytecodeInterpreter.setError(instruction, "ConstantPool index operand required");
            BytecodeInterpreter.processStackPush(instruction, stack, new InvalidItem(instruction));
            return;
        }
        final int index = instruction.getOperand(0).getValue();
        if(index <= 0 || index >= pool.getEntryCount()) {
            BytecodeInterpreter.setError(instruction, "Invalid ConstantPool index");
            BytecodeInterpreter.processStackPush(instruction, stack, new InvalidItem(instruction));
            return;
        }
        // Validate index size
        if((instruction.getOpcode() == Opcode._ldc && index >= Byte.MAX_VALUE * 2 - 1) || index >= Short.MAX_VALUE * 2 - 1) {
            BytecodeInterpreter.setError(instruction, "ConstantPool index is too large");
            BytecodeInterpreter.processStackPush(instruction, stack, new InvalidItem(instruction));
            return;
        }
        final PoolTag entry = pool.getEntry(index);
        final String value = entry.getContentString(pool);
        if(instruction.getOpcode() == Opcode._ldc2_w) {
            switch(entry.getPoolTagId()) {
                case PoolTag.TAG_LONG:
                    BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, value, PrimitiveType.LONG));
                    break;
                case PoolTag.TAG_DOUBLE:
                    BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, value, PrimitiveType.DOUBLE));
                    break;
            }
        } else {
            switch(entry.getPoolTagId()) {
                case PoolTag.TAG_STRING:
                    BytecodeInterpreter.processStackPush(instruction, stack, new ObjectItem(instruction, value));
                    break;
                case PoolTag.TAG_INTEGER:
                    BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, value, PrimitiveType.INTEGER));
                    break;
                case PoolTag.TAG_FLOAT:
                    BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, value, PrimitiveType.FLOAT));
                    break;
            }
        }
    }

    private static void processCast(final Instruction instruction, final MethodStack stack) {
        final String mnemonic = instruction.getOpcode().getMnemonic();

        final String typeA = PrimitiveType.getName(mnemonic.charAt(0));
        final PrimitiveType typeAprim = PrimitiveType.get(mnemonic.charAt(0));
        if(stack.getCount() == 0) {
            BytecodeInterpreter.setError(instruction, "Stack is empty, need type " + typeA);
            return;
        }
        final MethodItem item = stack.peek();
        if(item.getType() != typeAprim) {
            BytecodeInterpreter.setError(instruction, "Type " + typeA + " required for cast");
            return;
        }
        final String typeB = PrimitiveType.getName(mnemonic.charAt(2));
        BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, "("
                + typeB + ") " + item.getType(), PrimitiveType.get(mnemonic.charAt(2))));
    }

    private static void processIf(final Instruction instruction, final MethodStack stack) {
        if(instruction.getOperandCount() == 0) {
            BytecodeInterpreter.setError(instruction, "Branch byte operand required");
            return;
        }
        final String mnemonic = instruction.getOpcode().getMnemonic();
        // if mnemonic contains _ (and therefore is comparing two stack variables)
        final boolean compare = mnemonic.charAt(2) == '_';

        if(stack.getCount() < (compare ? 2 : 1)) {
            BytecodeInterpreter.setError(instruction, compare ? "Two stack items required to compare" : "Stack item required");
            return;
        }
        final MethodItem value1 = stack.pop();
        assert value1 != null;
        if(!compare) {
            // if not ifnull or ifnonnull
            if(mnemonic.length() == 4) {
                if(!PrimitiveType.isNumber(value1.getType())) {
                    BytecodeInterpreter.setError(instruction, "Stack item must be a number");
                }
            } else {
                if(value1.getType() != PrimitiveType.OBJECT) {
                    BytecodeInterpreter.setError(instruction, "Stack item must be an object");
                }
            }
        } else {
            final MethodItem value2 = stack.pop();
            assert value2 != null;
            if(mnemonic.charAt(3) == 'a') {
                if(value1.getType() != PrimitiveType.OBJECT || value2.getType() != PrimitiveType.OBJECT) {
                    BytecodeInterpreter.setError(instruction, "Stack items must both be objects, not " + value1.getType() + " and " + value2.getType());
                }
            } else {
                if(!PrimitiveType.isNumber(value1.getType()) || !PrimitiveType.isNumber(value2.getType())) {
                    BytecodeInterpreter.setError(instruction, "Stack items must both be numbers, not " + value1.getType() + " and " + value2.getType());
                }
            }
        }
    }

    private static void processNewArray(final Instruction instruction, final MethodStack stack, final ConstantPool pool) {
        assert instruction.getOpcode() != Opcode._multianewarray : "This method doesn't support this array type";
        if(instruction.getOperandCount() < 1) {
            BytecodeInterpreter.setError(instruction, instruction.getOpcode() == Opcode._newarray
                    ? "Type" : "ConstantPool index" + " operand required");
            return;
        }
        String input;
        PrimitiveType primitive;
        final int operand = instruction.getOperand(0).getValue();
        if(instruction.getOpcode() == Opcode._newarray) {
            if(operand < 4 || operand > 11) {
                BytecodeInterpreter.setError(instruction, "Type must be a value between 4 and 11");
                return;
            }
            input = ArrayRefItem.getType(operand);
            primitive = ArrayRefItem.getPrimitiveType(operand);
        } else {
            if(operand <= 0 || operand >= pool.getEntryCount()) {
                BytecodeInterpreter.setError(instruction, "Invalid ConstantPool index");
                return;
            }
            final PoolTag tag = pool.getEntry(operand);
            if(!(tag instanceof TagClass)) {
                BytecodeInterpreter.setError(instruction, "ConstantPool entry must be of type " + TagClass.NAME);
                return;
            }
            input = tag.getContentString(pool);
            primitive = PrimitiveType.OBJECT;
        }
        final MethodItem item = stack.peek();
        if(item == null) {
            BytecodeInterpreter.setError(instruction, "Stack is empty");
            return;
        }
        if(item.getType() != PrimitiveType.INTEGER) {
            BytecodeInterpreter.setError(instruction, "Stack item must be of type " + PrimitiveType.INTEGER);
            return;
        }
        stack.pop();

        input = "new " + input + "[" + item.getValue() + "]";
        stack.push(new ArrayRefItem(instruction, input, primitive, 1));
    }

    private static void processInvoke(final Instruction instruction, final MethodStack stack, final ConstantPool pool) {
        if(instruction.getOperandCount() == 0) {
            BytecodeInterpreter.setError(instruction, "ConstantPool index operand required");
            return;
        }
        final int index = instruction.getOperand(0).getValue();
        if(index <= 0 || index >= pool.getEntryCount()) {
            BytecodeInterpreter.setError(instruction, "Invalid ConstantPool index");
            return;
        }
        final PoolTag entry = pool.getEntry(index);
        assert entry != null;
        switch(instruction.getOpcode()) {
            case _invokeinterface:
                if(entry.getPoolTagId() != PoolTag.TAG_IM_REF) {
                    BytecodeInterpreter.setError(instruction, "ConstantPool index must be of type " + TagRef.INTERFACE_METHOD_NAME);
                    return;
                }
                break;
            default:
                if(entry.getPoolTagId() != PoolTag.TAG_METHOD_REF) {
                    BytecodeInterpreter.setError(instruction, "ConstantPool index must be of type " + TagRef.METHOD_NAME);
                    return;
                }
        }
        assert entry instanceof TagRef;

        final String descriptor = ((TagRef) entry).getTagNameAndType(pool).getTagDescriptor(pool).getValue();
        final int parameterCount = BytecodeInterpreter.getParameters(descriptor).length;
        final MethodItem[] parameters = new MethodItem[parameterCount];
        for(int i = parameterCount - 1; i >= 0; i--) {
            final MethodItem next = stack.peek();
            //TODO verify type
            if(next == null) {
                BytecodeInterpreter.setError(instruction, "Method requires " + parameterCount + " parameters from stack");
                // Put all of our popped parameters back onto the stack
                for(++i; i < parameters.length; i++) {
                    stack.push(parameters[i]);
                }
                return;
            }
            stack.pop();
            parameters[i] = next;
        }

        MethodItem ref = null;
        switch(instruction.getOpcode()) {
            case _invokedynamic:
                if(instruction.getOperandCount() < 3 || instruction.getOperand(1).getValue() != 0
                        || instruction.getOperand(2).getValue() != 0) {
                    BytecodeInterpreter.setError(instruction, "invokedynamic requires two 0 operands as padding");
                    return;
                }
            case _invokestatic:
                break;
            case _invokeinterface:
                if(instruction.getOperandCount() < 3 || instruction.getOperand(2).getValue() != 0) {
                    BytecodeInterpreter.setError(instruction, "invokeinterface requires three operands (index, count, 0)");
                    return;
                }
                if(instruction.getOperand(1).getValue() != parameterCount + 1) {
                    BytecodeInterpreter.setError(instruction, "Second operand must be value " + (parameterCount + 1));
                    return;
                }
            case _invokespecial:
            case _invokevirtual:
                ref = stack.peek();
                if(ref == null || ref.getType() != PrimitiveType.OBJECT) {
                    BytecodeInterpreter.setError(instruction, instruction.getOpcode().name().substring(1) + " requires object reference from stack");
                    return;
                }
                stack.pop();
                break;
            default:
                assert false;
        }
        // If we get here that means everything was successful and we can complete the process
        if(descriptor.charAt(descriptor.length() - 1) == 'V') {
            // If the method returns void, we don't have to do anything further since it doesn't push anything to the stack
            return;
        }
        // Construct method call expression
        final StringBuilder expression = new StringBuilder();
        if(instruction.getOpcode() == Opcode._invokestatic) {
            expression.append(((TagRef) entry).getTagClass(pool).getContentString(pool));
        } else {
            assert ref != null;
            expression.append(ref.getValue());
        }
        expression.append(".").append(((TagRef) entry).getTagNameAndType(pool).getTagName(pool).getValue()).append("(");
        for(int i = 0; i < parameters.length; i++) {
            expression.append(parameters[i].getValue());
            if(i < parameters.length - 1) {
                expression.append(", ");
            }
        }
        expression.append(")");

        final String decoded = Descriptor.decode(descriptor), returnType = decoded.substring(decoded.lastIndexOf(')') + 1);
        final int dimensions = BytecodeInterpreter.getDimensionsCount(returnType);
        final PrimitiveType type = PrimitiveType.get(returnType);
        if(dimensions > 0) {
            BytecodeInterpreter.processStackPush(instruction, stack, new ArrayRefItem(instruction, expression.toString(), type, dimensions));
        } else {
            if(type == PrimitiveType.OBJECT) {
                BytecodeInterpreter.processStackPush(instruction, stack, new ObjectItem(instruction, expression.toString()));
            } else {
                BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, expression.toString(), type));
            }
        }
    }

    private static void processGetAndPut(final Instruction instruction, final MethodStack stack, final ConstantPool pool) {
        if(instruction.getOperandCount() == 0) {
            BytecodeInterpreter.setError(instruction, "ConstantPool index operand required");
            return;
        }
        final int index = instruction.getOperand(0).getValue();
        if(index <= 0 || index >= pool.getEntryCount()) {
            BytecodeInterpreter.setError(instruction, "Invalid ConstantPool index");
            return;
        }
        final PoolTag entry = pool.getEntry(index);
        if(!(entry instanceof TagRef) || ((TagRef) entry).getType() != TagRef.TagRefType.FIELD) {
            BytecodeInterpreter.setError(instruction, "ConstantPool index must be of type " + TagRef.FIELD_NAME);
            return;
        }
        final TagNameAndType entryNameAndType = ((TagRef) entry).getTagNameAndType(pool);

        final MethodItem ref, item;
        String expression = "";
        // PutField and PutStatic both require an item from the stack to put into the field
        if(instruction.getOpcode() == Opcode._putfield || instruction.getOpcode() == Opcode._putstatic) {
            item = stack.peek();
            if(item == null) {
                BytecodeInterpreter.setError(instruction, "Value required from stack");
                return;
            }
            stack.pop();
        } else {
            item = null;
        }
        // GetField and PutField both require an object reference which is obtained from the stack
        if(instruction.getOpcode() == Opcode._getfield || instruction.getOpcode() == Opcode._putfield) {
            ref = stack.peek();
            if(ref == null) {
                BytecodeInterpreter.setError(instruction, "Object reference required from stack");
                return;
            }
            if(ref.getType() != PrimitiveType.OBJECT) {
                BytecodeInterpreter.setError(instruction, "Object reference must be of type " + PrimitiveType.OBJECT + ", not " + ref.getType());
                return;
            }
            stack.pop();

            expression += ref.getValue() + ".";
        }

        switch(instruction.getOpcode()) {
            case _getfield:
                expression += entryNameAndType.getTagName(pool).getValue();
                break;
            case _getstatic:
                expression += ((TagRef) entry).getTagClass(pool).getContentString(pool) + "." + entryNameAndType.getTagName(pool).getValue();
                break;
            case _putfield:
                assert item != null;
                expression += entryNameAndType.getTagName(pool).getValue() + " = " + item.getValue();
                break;
            case _putstatic:
                assert item != null;
                expression += ((TagRef) entry).getTagClass(pool).getContentString(pool) + "." + entryNameAndType.getTagName(pool).getValue() + " = " + item.getValue();
                break;
        }
        final PrimitiveType type = PrimitiveType.get(Descriptor.decode(entryNameAndType.getTagDescriptor(pool).getValue()));
        if(type == PrimitiveType.OBJECT) {
            BytecodeInterpreter.processStackPush(instruction, stack, new ObjectItem(instruction, expression));
        } else {
            BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, expression, type));
        }
    }

    private static void processMath(final Instruction instruction, final MethodStack stack) {
        final MethodItem item1 = stack.peek();
        if(item1 == null) {
            BytecodeInterpreter.setError(instruction, "Stack is empty");
            return;
        }
        final PrimitiveType type = PrimitiveType.get(instruction.getOpcode());
        assert type != null : "Bad opcode: " + instruction.getOpcode().name();
        if(item1.getType() != type) {
            BytecodeInterpreter.setError(instruction, item1.getType() + " cannot be used with type " + type);
            return;
        }
        stack.pop(); //our first item has been validated, remove it from the stack

        final String name = instruction.getOpcode().name().substring(2);
        // is comparing a string here really the best way?
        switch(name) {
            case "neg":  //if we only need to pop one stack item
                BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, "-" + item1.getValue(), type));
                break;
            case "inc":
                BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction, item1.getValue() + "++", type));
                break;
            default:
                final MethodItem item2 = stack.peek();
                if(item2.getType() != type) {
                    stack.push(item1); //re-add the item we validated because this item is not valid
                    BytecodeInterpreter.setError(instruction, item2.getType() + " cannot be used with type " + type);
                    return;
                }
                stack.pop(); //item2 was validated, pop it and finish interpreting the opcode

                final String operator;
                switch(name) {
                    case "add":
                        operator = "+";
                        break;
                    case "sub":
                        operator = "-";
                        break;
                    case "mul":
                        operator = "*";
                        break;
                    case "div":
                        operator = "/";
                        break;
                    case "rem":
                        operator = "%";
                        break;
                    case "and":
                        operator = "&";
                        break;
                    case "or":
                        operator = "|";
                        break;
                    case "shl":
                        operator = "<<";
                        break;
                    case "shr":
                        operator = ">>";
                        break;
                    case "ushr":
                        operator = ">>>";
                        break;
                    case "xor":
                        operator = "^";
                        break;
                    default:
                        operator = null;
                }
                assert operator != null : "Bad opcode: " + instruction.getOpcode();

                BytecodeInterpreter.processStackPush(instruction, stack, new NumberItem(instruction,
                        "(" + item2.getValue() + " " + operator + " " + item1.getValue() + ")", type));
                break;
        }
    }
}
