/*
 * @(#)StructTableModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.binary;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Table model for structured binary data.
 *
 * @author Werner Randelshofer
 * @version $Id: StructTableModel.java 331 2013-12-14 13:23:29Z werner $
 */
public class StructTableModel extends AbstractTableModel {
    private final static long serialVersionUID = 1L;
    protected ArrayList<StructTableModel.Value> data;
    protected StructParser.TypedefDeclaration typedef;
    private final char[] HEX = { '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f' };

    /**
     * Vector provides the data of the model.
     * Even entries are represented by the first column of the table.
     * Odd entries are represented by the second column of the table.
     */
    public StructTableModel(StructParser.TypedefDeclaration typedef, ArrayList<StructTableModel.Value> data) {
        this.typedef = typedef;
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }
    @Override
    public int getColumnCount() {
        return 2;
    }

    @Nullable
    @Override
    public Object getValueAt(int row, int column) {
        StructTableModel.Value elem = data.get(row);
        Object value;
        if (column == 0) {
            // value = identifierToString(((elem.index == null) ? elem.declaration : elem.declaration+elem.index).toString());
            int p = elem.qualifiedIdentifier.indexOf('.');
            String identifier = (p == -1) ? elem.qualifiedIdentifier : elem.qualifiedIdentifier.substring(p + 1);
            value = identifierToString(((elem.index == null) ? identifier : identifier + elem.index).toString());
        } else {
            value = elem.value;
        }
        return value;
    }

    @Nonnull
    public static String identifierToString(@Nonnull String s) {
        StringBuilder b = new StringBuilder();
        boolean wasUpperCase = true;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isUpperCase(s.charAt(i))) {
                if (!wasUpperCase) {
                    b.append(' ');
                }
                wasUpperCase = true;
            } else {
                wasUpperCase = false;
            }
            b.append(s.charAt(i));

        }
        return b.toString();
    }

    @Nonnull
    @Override
    public String getColumnName(int column) {
        return (column == 0) ? "Name" : "Value";
    }
    /**
     * Represents one typed value of the data structure represented by the StructTableModel.
     */
    public static class Value {
        public String qualifiedIdentifier;
        public Object declaration;
        public String index;
        @Nullable
        public Object value;
        public int intValue;
        public Value() {

        }
        public Value(String qualfiedIdentifier, String index, Object declaration, Object value, int intValue) {
            this.qualifiedIdentifier = qualifiedIdentifier;
            this.index = index;
            this.declaration = declaration;
            this.value = value;
            this.intValue=intValue;
        }
    }

    @Override
    public String toString() {
        return (typedef != null) ? typedef.toString() : super.toString();
    }
}

