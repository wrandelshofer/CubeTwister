/*
 * @(#)Importer.java
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
 * Importer.
 * @author  werni
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
    public ArrayList importFile(File file, ProgressObserver p)
    throws IOException;
    
    public JComponent getComponent();
}
