package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.notation.Symbol;

public class CountNode extends Node {
    private int count;
    /**
     * Creates a node which represents a symbol at the indicated position in the
     * source code.
     *
     * @param symbol
     * @param layerCount
     * @param startpos   The start position of the symbol.
     * @param endpos     The end position of the symbol.
     */
    public CountNode(Symbol symbol, int layerCount, int startpos, int endpos) {
        super(symbol, startpos, endpos);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
