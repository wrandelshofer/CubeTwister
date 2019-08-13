package ch.randelshofer.rubik.parser;

public class BinaryNode extends Node {
    private final static long serialVersionUID = 1L;
    protected Node operand1;

    public BinaryNode() {
    }

    public BinaryNode(int startpos, int endpos) {
        super(startpos, endpos);
    }

    public Node getOperand1() {
        return operand1;
    }

    public void setOperand1(Node newValue) {
        operand1 = newValue;
    }
}
