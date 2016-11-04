/*
 * @(#)CubeTwisterApplicationModel.java  1.0 January 11, 2007
 * Copyright (c) 2007 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.cubetwister;

import ch.randelshofer.app.action.DebugAction;
import ch.randelshofer.app.action.HelpAction;
import ch.randelshofer.quaqua.color.AlphaColorUIResource;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.file.ExportFileAction;
import ch.randelshofer.app.action.ImportFileAction;
import org.jhotdraw.app.action.edit.RedoAction;
import org.jhotdraw.app.action.edit.UndoAction;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * CubeTwisterApplicationModel.
 *
 * @author Werner Randelshofer
 * @version 1.0 January 11, 2007 Created.
 */
public class CubeTwisterApplicationModel extends DefaultApplicationModel {
    public final static long serialVersionUID=1L;

    /** Creates a new instance. */
    public CubeTwisterApplicationModel() {
        setOpenLastURIOnLaunch(true);
    }

    @Override
    public List<JToolBar> createToolBars(Application a, View p) {
        LinkedList<JToolBar> list = new LinkedList<JToolBar>();
        return list;
    }

    @Override
    public void initApplication(Application a) {
        if ("Windows".equals(UIManager.getLookAndFeel().getID())) {
            UIManager.put("Tree.leafIcon", new ImageIcon(CubeTwisterApplicationModel.class.getResource("/ch/randelshofer/gui/images/WindowsFileIcon.png")));
            UIManager.put("Tree.rowHeight", new Integer(20));
            UIManager.put("Tree.textBackground", new AlphaColorUIResource(0x0));
        }
    }

    @Override
    public ActionMap createActionMap(Application a, View v) {
        ActionMap m = super.createActionMap(a, v);
        m.put(ExportFileAction.ID, new ExportFileAction(a, v));
        m.put(ImportFileAction.ID, new ImportFileAction(a, v));
        m.put(HelpAction.ID, new HelpAction());
        m.put(UndoAction.ID, new UndoAction(a, v));
        m.put(RedoAction.ID, new RedoAction(a, v));
        m.put(DebugAction.ID, new DebugAction(a));
m.put(PreferencesAction.ID, new PreferencesAction(a));
        return m;
    }

    @Override
    protected MenuBuilder createMenuBuilder() {
        DefaultMenuBuilder mb= (DefaultMenuBuilder) super.createMenuBuilder();
        mb.setSuppressIcons(true);
        return mb;
    }

    @Override
    public JFileURIChooser createImportChooser(Application a, View v) {
            JFileURIChooser c = new JFileURIChooser();
            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
            c.setApproveButtonText(labels.getString("import.button"));
            c.setDialogType(JFileChooser.OPEN_DIALOG);
            c.setFileSelectionMode(JFileChooser.FILES_ONLY);
            c.setDialogTitle(labels.getString("exportAsHTML.title"));
            c.setAcceptAllFileFilterUsed(false);
        ExtensionFileFilter ff = new ExtensionFileFilter("CSV","csv");
        c.addChoosableFileFilter(ff);
        ff = new ExtensionFileFilter("CubeExplorer","txt");
        c.addChoosableFileFilter(ff);
        c.setFileFilter(ff);

        return c;
    }
    @Override
    public JFileURIChooser createExportChooser(Application a, View v) {
            JFileURIChooser c = new JFileURIChooser();
            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
            c.setApproveButtonText(labels.getString("export.button"));
            c.setDialogType(JFileChooser.SAVE_DIALOG);
            c.setFileSelectionMode(JFileChooser.FILES_ONLY);
            c.setDialogTitle(labels.getString("exportAsHTML.title"));
            c.setAcceptAllFileFilterUsed(false);
        ExtensionFileFilter ff = new ExtensionFileFilter("HTML","html");
        c.addChoosableFileFilter(ff);
        ff = new ExtensionFileFilter("CSV","csv");
        c.addChoosableFileFilter(ff);
        c.setFileFilter(ff);

        return c;
    }
    @Override
    public JFileURIChooser createSaveChooser(Application a, View v) {
            JFileURIChooser c = new JFileURIChooser();

        ExtensionFileFilter ff = new ExtensionFileFilter("CubeTwister XML","xml");
        c.addChoosableFileFilter(ff);
        c.setFileFilter(ff);

        return c;
    }



}
