/*
 * @(#)Exporter.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik.impexp;

import ch.randelshofer.cubetwister.doc.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import ch.randelshofer.gui.*;
import ch.randelshofer.util.*;
/**
 * Exporter.
 * @author  werni
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
