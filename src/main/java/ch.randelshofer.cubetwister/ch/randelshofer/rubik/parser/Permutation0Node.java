package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.parser.Node;

import javax.swing.tree.MutableTreeNode;

public class Permutation0Node extends Node {
    private Symbol permutationSign;
    public Permutation0Node() {
        super(Symbol.PERMUTATION);
    }

    public void insert(MutableTreeNode newChild, int childIndex) {
        if (newChild instanceof PermutationItem0Node) {
            super.insert(newChild, childIndex);
        } else throw new IllegalArgumentException("Illegal child: "+newChild);
    }

    public void setPermutationSign(Symbol permutationSign) {
        this.permutationSign=permutationSign;
    }
}
