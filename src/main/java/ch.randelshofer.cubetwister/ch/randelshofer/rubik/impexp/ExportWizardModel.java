/* @(#)ExportWizard.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.impexp;

import ch.randelshofer.beans.AbstractBean;
import ch.randelshofer.cubetwister.CubeTwisterView;
import ch.randelshofer.gui.Fonts;
import ch.randelshofer.gui.ProgressObserver;
import ch.randelshofer.gui.ProgressView;
import ch.randelshofer.gui.WizardModel;
import ch.randelshofer.rubik.impexp.csv.CSVExporter;
import ch.randelshofer.rubik.impexp.cubeexplorer.CubeExplorerExporter;
import ch.randelshofer.util.RunnableWorker;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
/**
 * ExportWizard.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ExportWizardModel extends AbstractBean implements WizardModel {
    private final static long serialVersionUID = 1L;
    private JFileChooser fileChooser;
    private String[] titles = { "Choose File And Format", "Specify Options"};
    private File file;
    private CubeTwisterView view;
    
    /**
     * key = FileFilter;
     * value = Exporter
     */
    private HashMap<javax.swing.filechooser.FileFilter,Exporter> filterToExporterMap;
    
    /** Creates a new instance of ExportWizard */
    public ExportWizardModel() {
        fileChooser = new JFileChooser();
        fileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                String name = event.getPropertyName();
                if (name.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                    setFile(fileChooser.getSelectedFile());
                }
            }
        });
        fileChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setFile(fileChooser.getSelectedFile());
            }
        });
        fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
        fileChooser.setControlButtonsAreShown(false);
        
        Exporter exporter;
        javax.swing.filechooser.FileFilter filter;
        filterToExporterMap = new HashMap<javax.swing.filechooser.FileFilter,Exporter>();
        
        
        exporter = new CSVExporter(',');
        filter = new ExtensionFileFilter("csv", "Comma Separated Values");
        filterToExporterMap.put(filter, exporter);
        fileChooser.addChoosableFileFilter(filter);
        
        exporter = new CSVExporter('\t');
        filter = new ExtensionFileFilter("tab", "Tabulator Separated Values");
        filterToExporterMap.put(filter, exporter);
        fileChooser.addChoosableFileFilter(filter);

        exporter = new CubeExplorerExporter();
        filter = new ExtensionFileFilter("txt", "CubeExplorer");
        filterToExporterMap.put(filter, exporter);
        fileChooser.addChoosableFileFilter(filter);
    }

    public void setDocumentView(CubeTwisterView view) {
        this.view = view;
        for (Iterator i=filterToExporterMap.values().iterator(); i.hasNext(); ) {
            Exporter exporter = (Exporter) i.next();
            exporter.setDocumentModel(view.getModel());
    }
    }
    
    public void setFile(File newValue) {
        File oldValue = file;
        /*
        if (newValue != null && ! newValue.getName().endsWith("."+getSelectedFilter().getDefaultExtension())) {
            newValue = new File(newValue.getParentFile(), newValue.getName()+"."+getSelectedFilter().getDefaultExtension());
        }*/
        
        this.file = newValue;
        firePropertyChange("canFinish", oldValue != null, newValue != null);
    }
    
    public boolean canFinish() {
        return file != null;
    }
    
    public void cancel() {
    }
    
    public Exporter getSelectedExporter() {
        return filterToExporterMap.get(fileChooser.getFileFilter());
    }
    public ExtensionFileFilter getSelectedFilter() {
        return (ExtensionFileFilter) fileChooser.getFileFilter();
    }
    
    public void finish() {
        view.setEnabled(false);
        view.getModel().dispatch(new RunnableWorker() {
            public Object construct() {
                ProgressObserver p = new ProgressView("Export "+file,"...", 0, 100);
                try {
                    getSelectedExporter().exportFile(file, p);
                    return null;
                } catch (Throwable t) {
                    t.printStackTrace();
                    return t;
                } finally {
                    p.complete();
                }
            }
            public void finished(Object result) {
                if (result instanceof Throwable) {
                    String message = ((Throwable) result).getMessage();
                    if (message == null) message = result.toString();
                    
                    JOptionPane.showMessageDialog(
                    view,
                    "<html>"+Fonts.emphasizedDialogFontTag("Couldn't Export!")
                    +"<br>"+Fonts.smallDialogFontTag(
                    "Couldn't export to file \""+file.getName()
                    +"\".<br>"+ message),
                    "CubeTwister: Export File",
                    JOptionPane.ERROR_MESSAGE
                    );
                }
                view.setEnabled(true);
            }
        });
    }
    
    public JComponent getPanel(int index) {
        switch (index) {
            case 0 : return fileChooser;
            case 1 : return getSelectedExporter().getComponent();
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
        return "CubeTwister: Export File";
    }
}
