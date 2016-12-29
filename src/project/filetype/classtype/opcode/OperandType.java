package project.filetype.classtype.opcode;

/**
 * Created by Ryan Thomson on 28/12/2016.
 */
public enum OperandType {

    NO_OPERAND, CONSTANT, INDEX_LOCAL, INDEX_POOL, BRANCH_OFFSET,
    UNDEFINED // behavior for this opcode must be handled separately
}
