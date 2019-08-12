/* @(#)ImportFileAction.java
 * Copyright (c) 2010 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.app.action;

import ch.randelshofer.cubetwister.CubeTwisterView;
import ch.randelshofer.cubetwister.doc.DocumentModel;
import ch.randelshofer.cubetwister.doc.ScriptModel;
import ch.randelshofer.gui.ProgressObserver;
import ch.randelshofer.gui.ProgressView;
import ch.randelshofer.rubik.impexp.csv.CSVImporter;
import org.jhotdraw.annotation.Nullable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.app.action.file.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.gui.event.*;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.*;

/**
 * Lets the user save unsaved changes of the active view, then presents
 * an {@code URIChooser} and loads the selected URI into the active view.
 * <p>
 * This action is called when the user selects the Load item in the File
 * menu. The menu item is automatically created by the application.
 * A Recent Files sub-menu is also automatically generated.
 * <p>
 * If you want this behavior in your application, you have to create it
 * and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 * <p>
 * This action is designed for applications which do not automatically
 * create a new view for each opened file. This action goes together with
 * {@link ClearFileAction}, {@link NewWindowAction}, {@link LoadFileAction},
 * {@link LoadDirectoryAction} and {@link CloseFileAction}.
 * This action should not be used together with {@link OpenFileAction}.
 *
 * <hr>
 * <b>Features</b>
 *
 * <p><em>Open last URI on launch</em><br>
 * When the application is started, the last opened URI is opened in a view.<br>
 * {@code LoadFileAction} supplies data for this feature by calling
 * {@link Application#addRecentURI} when it successfully loaded a file.
 * See {@link org.jhotdraw.app} for a description of the feature.
 * </p>
 *
 * <p><em>Allow multiple views per URI</em><br>
 * When the feature is disabled, {@code LoadFileAction} prevents exporting to an URI which
 * is opened in another view.<br>
 * See {@link org.jhotdraw.app} for a description of the feature.
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ImportFileAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "file.import";
    private Component oldFocusOwner;

    /**
     * Creates a new instance.
     */
    public ImportFileAction(Application app, @Nullable View view) {
        super(app, view);
        ResourceBundleUtil labels = new ResourceBundleUtil(
                ResourceBundle.getBundle("ch.randelshofer.app.Labels",
                        ModuleLayer.boot().findModule("ch.randelshofer.cubetwister").get()));
        labels.configureAction(this, ID);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        final View view = getActiveView();
        if (view == null) {
            return;
        }
        if (view.isEnabled()) {
            oldFocusOwner = SwingUtilities.getWindowAncestor(view.getComponent()).getFocusOwner();
            view.setEnabled(false);

            {
                URIChooser fileChooser = getChooser(view);

                JSheet.showOpenSheet(fileChooser, view.getComponent(), new SheetListener() {

                    @Override
                    public void optionSelected(final SheetEvent evt) {
                        if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                            final URI uri = evt.getChooser().getSelectedURI();

                            importViewFromURI(view, uri, evt.getChooser());
                        } else {
                            view.setEnabled(true);
                            if (oldFocusOwner != null) {
                                oldFocusOwner.requestFocus();
                            }
                        }
                    }
                });
            }
        }
    }


    protected URIChooser getChooser(View view) {
        URIChooser chsr = (URIChooser) (view.getComponent()).getClientProperty("importChooser");
        if (chsr == null) {
            chsr = getApplication().getModel().createImportChooser(getApplication(), view);
            view.getComponent().putClientProperty("importChooser", chsr);
        }
        return chsr;
    }


    public void importViewFromURI(final View view, final URI uri, final URIChooser chooser) {
        JFileURIChooser fileChooser = (JFileURIChooser) chooser;
        FileFilter fileFilter = fileChooser.getFileFilter();
        if (fileFilter.getDescription().contains("CSV")) {
            importCsvFromURI(view, uri, chooser);
        } else {
            importFileFromURI(view, uri, chooser);
        }
    }

    public void importCsvFromURI(final View view, final URI uri, final URIChooser chooser) {
        System.out.println("CSV YAY!");
        CSVImporter csvImporter = new CSVImporter(',');
        csvImporter.getComponent();

        JPanel borderPane = new JPanel();
        borderPane.setLayout(new BorderLayout());
        JDialog dialog = new JDialog((JFrame) view.getComponent().getRootPane().getParent());
        borderPane.add(BorderLayout.CENTER, csvImporter);
        JPanel flowPane = new JPanel();
        flowPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton();
        JButton importButton = new JButton();
        flowPane.add(cancelButton);
        flowPane.add(importButton);
        borderPane.add(BorderLayout.SOUTH, flowPane);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        AbstractAction cancelAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                view.setEnabled(true);
            }
        };
        cancelAction.putValue(Action.NAME, "Cancel");
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelAction.actionPerformed(null);
            }
        });
        cancelButton.setAction(cancelAction);
        AbstractAction importAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                doImportCsvFromURI(view, uri, csvImporter);
                view.setEnabled(true);
            }
        };
        importAction.putValue(Action.NAME, "Import");
        importButton.setAction(importAction);
        dialog.getContentPane().add(borderPane);
        dialog.pack();
        dialog.setLocationRelativeTo(view.getComponent());
        dialog.setVisible(true);
    }

    public void importFileFromURI(final View view, final URI uri, final URIChooser chooser) {
        view.setEnabled(false);

        // Open the file
        view.execute(new BackgroundTask() {

            @Override
            protected void construct() throws IOException {
                view.read(uri, chooser);
            }

            @Override
            protected void done() {
                view.setURI(uri);
                view.setEnabled(true);
                getApplication().addRecentURI(uri);
            }

            @Override
            protected void failed(Throwable value) {
                value.printStackTrace();

                ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.app.Labels"));
                JSheet.showMessageSheet(view.getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css")
                                + "<b>" + labels.getFormatted("file.load.couldntLoad.message", URIUtil.getName(uri)) + "</b><p>"
                                + ((value == null) ? "" : value),
                        JOptionPane.ERROR_MESSAGE, new SheetListener() {

                            @Override
                            public void optionSelected(SheetEvent evt) {
                                view.clear();
                                view.setEnabled(true);
                            }
                        });
            }
        });
    }

    public void doImportCsvFromURI(final View view, final URI uri, final CSVImporter csvImporter) {
        view.setEnabled(false);

        ProgressView p = new ProgressView("Import CSV", "Importing " + uri);
        CompletableFuture<ArrayList<ScriptModel>> f = new CompletableFuture<>();
        // Open the file
        view.execute(new BackgroundTask() {

            @Override
            protected void construct() throws IOException {
                ArrayList<ScriptModel> scriptModels = csvImporter.importFile(Paths.get(uri).toFile(), p);
                f.complete(scriptModels);
            }

            @Override
            protected void done() {
                p.close();
                try {
                    ArrayList<ScriptModel> scriptModels = f.get();
                    CubeTwisterView v = (CubeTwisterView) view;
                    DocumentModel model = v.getModel();
                    for (ScriptModel script : scriptModels) {
                        model.addTo(script, model.getScripts());
                    }
                    view.setEnabled(true);
                } catch (InterruptedException | ExecutionException e) {
                    view.setEnabled(true);
                    e.printStackTrace();
                }
            }

            @Override
            protected void failed(Throwable value) {
                p.close();
                value.printStackTrace();

                ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.app.Labels"));
                JSheet.showMessageSheet(view.getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css")
                                + "<b>" + labels.getFormatted("file.load.couldntLoad.message", URIUtil.getName(uri)) + "</b><p>"
                                + ((value == null) ? "" : value),
                        JOptionPane.ERROR_MESSAGE, new SheetListener() {

                            @Override
                            public void optionSelected(SheetEvent evt) {
                                view.clear();
                                view.setEnabled(true);
                            }
                        });
            }
        });
    }
}
