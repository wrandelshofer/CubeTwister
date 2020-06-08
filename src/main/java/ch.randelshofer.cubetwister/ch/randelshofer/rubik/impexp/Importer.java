/*
 * @(#)Importer.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.impexp;

import ch.randelshofer.cubetwister.doc.DocumentModel;
import ch.randelshofer.gui.ProgressObserver;
import org.jhotdraw.annotation.Nonnull;

import javax.swing.JComponent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Importer.
 * @author Werner Randelshofer
 */
public interface Importer {
    /**
     * Sets the document model.
     */
    public void setDocumentModel(DocumentModel model);

    /**
     * Sets a file for previewing by the panels of the importer.
     */
    public void setPreviewFile(File file);

    /**
     * Imports the contents of the specified file into the DocumentModel.
     *
     * @return Returns an ArrayList of ScriptModel's.
     */
    @Nonnull
    public ArrayList importFile(File file, ProgressObserver p)
    throws IOException;

    @Nonnull
    public JComponent getComponent();
}
