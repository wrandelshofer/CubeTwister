/* @(#)Exporter.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.impexp;

import ch.randelshofer.cubetwister.doc.DocumentModel;
import ch.randelshofer.gui.ProgressObserver;

import javax.swing.JComponent;
import java.io.File;
import java.io.IOException;
/**
 * Exporter.
 * @author Werner Randelshofer
 */
public interface Exporter {
    /**
     * Sets the document model.
     */
    public void setDocumentModel(DocumentModel model);
    
    /**
     * Exports the DocumentModel to the specified file.
     */
    public void exportFile(File file, ProgressObserver p)
    throws IOException;
    
    public JComponent getComponent();
}
