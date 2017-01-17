package project.filetype.classtype.bytecode.opcode;

/**
 * Created by Ryan Thomson on 28/12/2016.
 */
public enum Opcode {

    // LOAD
    _aload_0(0x2a), _aload_1(0x2b), _aload_2(0x2c), _aload_3(0x2d),
    _iload(0x15, 1, OperandType.INDEX_LOCAL), _iload_0(0x1a), _iload_1(0x1b), _iload_2(0x1c), _iload_3(0x1d),
    _fload(0x17, 1, OperandType.INDEX_LOCAL), _fload_0(0x22), _fload_1(0x23), _fload_2(0x24), _fload_3(0x25),
    _dload(0x18, 1, OperandType.INDEX_LOCAL), _dload_0(0x26), _dload_1(0x27), _dload_2(0x28), _dload_3(0x29),
    _lload(0x16, 1, OperandType.INDEX_LOCAL), _lload_0(0x1e), _lload_1(0x1f), _lload_2(0x20), _lload_3(0x21),
    _aload(0x19, 1, OperandType.INDEX_LOCAL), _aaload(0x32), _iaload(0x2e), _baload(0x33),
    _caload(0x34), _saload(0x35), _faload(0x30), _daload(0x31), _laload(0x2f),

    // STORE
    _astore_0(0x4b), _astore_1(0x4c), _astore_2(0x4d), _astore_3(0x4e),
    _istore(0x36, 1, OperandType.INDEX_LOCAL), _istore_0(0x3b), _istore_1(0x3c), _istore_2(0x3d), _istore_3(0x3e),
    _fstore(0x38, 1, OperandType.INDEX_LOCAL), _fstore_0(0x43), _fstore_1(0x44), _fstore_2(0x45), _fstore_3(0x46),
    _dstore(0x39, 1, OperandType.INDEX_LOCAL), _dstore_0(0x47), _dstore_1(0x48), _dstore_2(0x49), _dstore_3(0x4a),
    _lstore(0x37, 1, OperandType.INDEX_LOCAL), _lstore_0(0x3f), _lstore_1(0x40), _lstore_2(0x41), _lstore_3(0x42),
    _astore(0x3a, 1, OperandType.INDEX_LOCAL), _aastore(0x53), _iastore(0x4f), _bastore(0x54),
    _castore(0x55), _sastore(0x56), _fastore(0x51), _dastore(0x52), _lastore(0x50),

    // GET AND INVOKE
    _getfield(0xb4, 2, OperandType.INDEX_POOL), _getstatic(0xb2, 2, OperandType.INDEX_POOL),
    _invokedynamic(0xba, 4, OperandType.UNDEFINED), _invokeinterface(0xb9, 4, OperandType.UNDEFINED),
    _invokespecial(0xb7, 2, OperandType.INDEX_POOL),
    _invokestatic(0xb8, 2, OperandType.INDEX_POOL), _invokevirtual(0xb6, 2, OperandType.INDEX_POOL),

    // PUSH
    _putfield(0xb5, 2, OperandType.INDEX_POOL), _putstatic(0xb3, 2, OperandType.INDEX_POOL),
    _bipush(0x10, 1, OperandType.CONSTANT), _sipush(0x11, 2, OperandType.CONSTANT),
    _ldc(0x12, 1, OperandType.INDEX_POOL), _ldc_w(0x13, 2, OperandType.INDEX_POOL), _ldc2_w(0x14, 2, OperandType.INDEX_POOL),

    _aconst_null(0x01),
    _iconst_m1(0x02), _iconst_0(0x03), _iconst_1(0x04), _iconst_2(0x05), _iconst_3(0x06), _iconst_4(0x07), _iconst_5(0x08),
    _fconst_0(0x0b), _fconst_1(0x0c), _fconst_2(0x0d), _dconst_0(0x0e), _dconst_1(0x0f), _lconst_0(0x09), _lconst_1(0x0a),

    _new(0xbb, 2, OperandType.INDEX_POOL), _pop(0x57), _pop2(0x58), _swap(0x5f),
    _dup(0x59), _dup_x1(0x5a), _dup_x2(0x5b), _dup2(0x5c), _dup2_x1(0x5d), _dup2_x2(0x5e),

    // CAST
    _i2b(0x91), _i2c(0x92), _i2d(0x87), _i2f(0x86), _i2l(0x85), _i2s(0x93),
    _f2d(0x8d), _f2i(0x8b), _f2l(0x8c), _d2f(0x90), _d2i(0x8e), _d2l(0x8f),
    _l2d(0x8a), _l2f(0x89), _l2i(0x88),

    _checkcast(0xc0, 2, OperandType.INDEX_POOL),
    _instanceof(0xc1, 2, OperandType.INDEX_POOL),

    // MATH
    _iand(0x7e), _ior(0x80), _ishl(0x78), _ishr(0x7a), _iushr(0x7c), _ixor(0x82),
    _land(0x7f), _lor(0x81), _lshl(0x79), _lshr(0x7b), _lushr(0x7d), _lxor(0x83),
    _iadd(0x60), _isub(0x64), _imul(0x68), _idiv(0x6c), _ineg(0x74), _irem(0x70),
    _fadd(0x62), _fsub(0x66), _fmul(0x6a), _fdiv(0x6e), _fneg(0x76), _frem(0x72),
    _dadd(0x63), _dsub(0x67), _dmul(0x6b), _ddiv(0x6f), _dneg(0x77), _drem(0x73),
    _ladd(0x61), _lsub(0x65), _lmul(0x69), _ldiv(0x6d), _lneg(0x75), _lrem(0x71),

    _iinc(0x84, 2, OperandType.UNDEFINED),

    // COMPARE
    _ifeq(0x99, 2, OperandType.BRANCH_OFFSET), _ifge(0x9c, 2, OperandType.BRANCH_OFFSET), _ifgt(0x9d, 2, OperandType.BRANCH_OFFSET),
    _ifle(0x9e, 2, OperandType.BRANCH_OFFSET), _iflt(0x9b, 2, OperandType.BRANCH_OFFSET), _ifne(0x9a, 2, OperandType.BRANCH_OFFSET),
    _if_acmpeq(0xa5, 2, OperandType.BRANCH_OFFSET), _if_acmpne(0xa6, 2, OperandType.BRANCH_OFFSET),
    _if_icmpeq(0x9f, 2, OperandType.BRANCH_OFFSET), _if_icmpge(0xa2, 2, OperandType.BRANCH_OFFSET),
    _if_icmpgt(0xa3, 2, OperandType.BRANCH_OFFSET), _if_icmple(0xa4, 2, OperandType.BRANCH_OFFSET),
    _if_icmplt(0xa1, 2, OperandType.BRANCH_OFFSET), _if_icmpne(0xa0, 2, OperandType.BRANCH_OFFSET),
    _ifnonnull(0xc7, 2, OperandType.BRANCH_OFFSET), _ifnull(0xc6, 2, OperandType.BRANCH_OFFSET),
    _fcmpg(0x96), _fcmpl(0x95), _dcmpg(0x98), _dcmpl(0x97), _lcmp(0x94),

    // OTHER
    _nop(0x00), _ret(0xa9, 1, OperandType.INDEX_LOCAL),
    _goto(0xa7, 2, OperandType.BRANCH_OFFSET), _goto_w(0xc8, 4, OperandType.BRANCH_OFFSET),
    _jsr(0xa8, 2, OperandType.BRANCH_OFFSET), _jsr_w(0xc9, 4, OperandType.BRANCH_OFFSET),

    _newarray(0xbc, 1, OperandType.CONSTANT), _anewarray(0xbd, 2, OperandType.INDEX_POOL),
    _multianewarray(0xc5, 3, OperandType.UNDEFINED), _arraylength(0xbe),
    _lookupswitch(0xab, 0, OperandType.UNDEFINED), _tableswitch(0xaa, 0, OperandType.UNDEFINED), _wide(0xc4, 0, OperandType.UNDEFINED), //variable length
    _monitorenter(0xc2), _monitorexit(0xc3),

    _athrow(0xbf),
    _return(0xb1), _areturn(0xb0), _ireturn(0xac), _freturn(0xae), _dreturn(0xaf), _lreturn(0xad),

    // DEBUG - SHOULD NEVER APPEAR
    _impdep1(0xfe), _impdep2(0xff),
    _breakpoint(0xca);

    private static Opcode[] opcodes;

    private final int opcode, otherBytes;
    private final OperandType type;

    Opcode(final int opcode) {
        this(opcode, 0);
    }

    Opcode(final int opcode, final int otherBytes) {
        this(opcode, otherBytes, OperandType.NO_OPERAND);
    }

    Opcode(final int opcode, final int otherBytes, final OperandType type) {
        this.opcode = opcode;
        this.otherBytes = otherBytes;
        this.type = type;
    }

    public int getOpcode() {
        return this.opcode;
    }

    public int getOtherBytes() {
        return this.otherBytes;
    }

    public OperandType getType() {
        return this.type;
    }

    public String getMnemonic() {
        return super.name().substring(1); //skip the underscore at the start
    }

    public static Opcode get(final int opcode) {
        return Opcode.opcodes[opcode];
    }

    static {
        // Place the opcodes into an array for quick access
        Opcode.opcodes = new Opcode[0xff + 1];
        for(final Opcode next : Opcode.values()) {
            assert (next.opcode >= 0 && next.opcode <= 0xff);
            Opcode.opcodes[next.opcode] = next;
        }
    }
}
