package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.parser.Node;

import java.util.List;

public class PermutationItem0Node extends Node {
    private List<Symbol> faces;
    private int partNumber;
    private Symbol permutationSign;
    public PermutationItem0Node() {
        super(Symbol.PERMUTATION_ITEM);
    }

    public List<Symbol> getFaces() {
        return faces;
    }

    public void setFaces(List<Symbol> faces) {
        this.faces = faces;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public void setPermutationSign(Symbol permutationSign) {
        this.permutationSign=permutationSign;
    }
}
