/*
 * @(#)DefaultCellEditor2.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.table;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.text.PlainDocument;
import java.awt.Color;

/**
 * DefaultCellEditor2.
 *
 * @author Werner Randelshofer
 */
public class DefaultCellEditor2 extends DefaultCellEditor {
    private final static long serialVersionUID = 1L;

    /**
     * Constructs a DefaultCellEditor that uses a text field.
     *
     * @param textField a JTextField object
     */
    public DefaultCellEditor2(@Nonnull final JTextField textField) {
        super(textField);
        textField.setBorder(new LineBorder(Color.black));
        delegate = new EditorDelegate() {
            private final static long serialVersionUID = 1L;

            @Override
            public void setValue(@Nullable Object value) {
                // Setting the document is needed to prevent
                // IllegalArgumentException in java.awt.font.LineBreakMeasurer
                // and in java.text.RuleBasedBreakIterator when
                // composing a character which consists of multiple unicode
                // code points. Such as a umlaut.
                textField.setDocument(new PlainDocument());
                textField.setText((value != null) ? value.toString() : "");
            }

            @Override
	    public Object getCellEditorValue() {
		return textField.getText();
	    }
        };

    }

    /**
     * Constructs a DefaultCellEditor object that uses
     * a check box.
     *
     * @param checkBox  a JCheckBox object
     */
    public DefaultCellEditor2(@Nonnull JCheckBox checkBox) {
        super(checkBox);
        checkBox.setBorder(new LineBorder(Color.black));
    }

    /**
     * Constructs a DefaultCellEditor object that uses a
     * combo box.
     *
     * @param comboBox  a JComboBox object
     */
    public DefaultCellEditor2(@Nonnull JComboBox comboBox) {
        super(comboBox);
        comboBox.setBorder(new LineBorder(Color.black));
    }
}
