/*
 * @(#)TranslateNotationComboBoxModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.impexp;

import ch.randelshofer.cubetwister.doc.DocumentModel;
import org.jhotdraw.annotation.Nonnull;

/**
 * A combo box model which lets the user choose a notation to translate
 * scripts.
 * In addition this combo box model provides a choice "Do not translate".
 *
 * @author Werner Randelshofer
 */
public class TranslateNotationComboBoxModel extends javax.swing.AbstractListModel implements javax.swing.ComboBoxModel {
    private final static long serialVersionUID = 1L;
    private DocumentModel model;
    public  final static String DO_NOT_TRANSLATE = "Do not translate";
    private Object selectedItem = DO_NOT_TRANSLATE;

    /** Creates a new instance of TranslateNotationComboBoxModel */
    public TranslateNotationComboBoxModel() {
        model = new DocumentModel();
    }

    public void setDocumentModel(DocumentModel model) {
        this.model = model;
    }

    @Nonnull
    public Object getElementAt(int index) {
        return (index == 0)
                ? (Object) DO_NOT_TRANSLATE
                : (Object) model.getNotations().getChildAt(index - 1);
    }

    public Object getSelectedItem() {
        return selectedItem;
    }

    public int getSize() {
        return model.getNotations().getChildCount() + 1;
    }

    public void setSelectedItem(Object anItem) {
        selectedItem = anItem;
    }

}
