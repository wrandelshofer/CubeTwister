/* @(#)ImportWizardModel.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.impexp;

import ch.randelshofer.beans.AbstractBean;
import ch.randelshofer.cubetwister.CubeTwisterView;
import ch.randelshofer.cubetwister.doc.DocumentModel;
import ch.randelshofer.cubetwister.doc.EntityModel;
import ch.randelshofer.cubetwister.doc.ScriptModel;
import ch.randelshofer.gui.Fonts;
import ch.randelshofer.gui.ProgressObserver;
import ch.randelshofer.gui.ProgressView;
import ch.randelshofer.gui.WizardModel;
import ch.randelshofer.rubik.impexp.csv.CSVImporter;
import ch.randelshofer.rubik.impexp.cubeexplorer.CubeExplorerImporter;
import ch.randelshofer.util.RunnableWorker;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * ImportWizardModel.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ImportWizardModel extends AbstractBean implements WizardModel {
    private final static long serialVersionUID = 1L;
    private JFileChooser fileChooser;
    private String[] titles;
    private File file;
    private CubeTwisterView view;
    /**
     * key = FileFilter;
     * value = Importer
     */
    private HashMap<javax.swing.filechooser.FileFilter,Importer> filterToImporterMap;
    
    /** Creates a new instance of ExportWizard */
    public ImportWizardModel(CubeTwisterView view) {
        fileChooser = new JFileChooser();
        fileChooser.setControlButtonsAreShown(false);
        fileChooser.setDialogType(javax.swing.JFileChooser.OPEN_DIALOG);
        fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                String name = event.getPropertyName();
                if (name.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                    setFile(fileChooser.getSelectedFile());
                }
            }
        });
        
        Importer importer;
        javax.swing.filechooser.FileFilter filter;
        filterToImporterMap = new HashMap<javax.swing.filechooser.FileFilter,Importer>();
        
        importer = new CSVImporter(',');
        filter = new ExtensionFileFilter("csv", "Comma Separated Values");
        filterToImporterMap.put(filter, importer);
        fileChooser.addChoosableFileFilter(filter);
        
        importer = new CSVImporter('\t');
        filter = new ExtensionFileFilter("tab", "Tabulator Separated Values");
        filterToImporterMap.put(filter, importer);
        fileChooser.addChoosableFileFilter(filter);
        
        importer = new CubeExplorerImporter();
        filter = new ExtensionFileFilter("txt", "CubeExplorer");
        filterToImporterMap.put(filter, importer);
        fileChooser.addChoosableFileFilter(filter);
        
        titles = new String[] {
            "Choose File", "Options"
        };
        
        setDocumentView(view);
    }
    
    public void setFile(File newValue) {
        File oldValue = file;
        this.file = newValue;
        
        firePropertyChange("canFinish", oldValue != null, newValue != null);
    }
    
    public boolean canFinish() {
        return file != null;
    }
    
    public void setDocumentView(CubeTwisterView view) {
        this.view = view;
        for (Iterator i=filterToImporterMap.values().iterator(); i.hasNext(); ) {
            Importer importer = (Importer) i.next();
            importer.setDocumentModel(view.getModel());
        }
    }
    
    public void cancel() {
    }
    
    public void finish() {
        view.setEnabled(false);
        view.getModel().dispatch(new RunnableWorker() {
            public Object construct() {
                ProgressObserver p = new ProgressView("Import "+file,"...", 0, 100);
                Object result;
                try {
                    result = getSelectedImporter().importFile(file, p);
                } catch (Throwable t) {
                    result = t;
                } finally {
                    p.complete();
                }
                return result;
            }
            public void finished(Object result) {
                if (result instanceof Throwable) {
                    Throwable t = (Throwable) result;
                    t.printStackTrace();
                    String message = t.getMessage();
                    if (message == null) message = t.toString();
                    JOptionPane.showMessageDialog(
                    view,
                    "<html>"+Fonts.emphasizedDialogFontTag("Couldn't Import!")
                    +"<br>"+Fonts.smallDialogFontTag(
                    "Couldn't import to file \""+file.getName()
                    +"\".<br>"+ message),
                    "CubeTwister: Import File",
                    JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    ArrayList list = (ArrayList) result;
                    DocumentModel doc = view.getModel();
                    EntityModel scripts = doc.getScripts();
                    for (Iterator i=list.iterator(); i.hasNext(); ) {
                        doc.insertNodeInto((ScriptModel) i.next(), scripts, scripts.getChildCount());
                    }
                }
                view.setEnabled(true);
            }
        });
    }
    
    public Importer getSelectedImporter() {
        return filterToImporterMap.get(fileChooser.getFileFilter());
    }
    public ExtensionFileFilter getSelectedFilter() {
        return (ExtensionFileFilter) fileChooser.getFileFilter();
    }
    
    @Override
    public JComponent getPanel(int index) {
        switch (index) {
            case 0 : return fileChooser;
            case 1 :
                getSelectedImporter().setPreviewFile(file);
                return getSelectedImporter().getComponent();
            default : throw new IndexOutOfBoundsException();
        }
    }
    
    public int getPanelCount() {
        return 2;
    }
    
    public String getPanelTitle(int index) {
        return titles[index];
    }
    
    public String getTitle() {
        return "CubeTwister: Import File";
    }
}
