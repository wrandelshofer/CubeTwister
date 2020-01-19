/* @(#)CubeTwisterApplicationModel.java
 * Copyright (c) 2007 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister;

import ch.randelshofer.app.action.DebugAction;
import ch.randelshofer.app.action.HelpAction;
import ch.randelshofer.app.action.ImportFileAction;
import ch.randelshofer.gui.plaf.AlphaColorUIResource;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.MenuBuilder;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.edit.RedoAction;
import org.jhotdraw.app.action.edit.UndoAction;
import org.jhotdraw.app.action.file.ExportFileAction;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * CubeTwisterApplicationModel.
 *
 * @author Werner Randelshofer
 */
public class CubeTwisterApplicationModel extends DefaultApplicationModel {
    public final static long serialVersionUID=1L;

    /** Creates a new instance. */
    public CubeTwisterApplicationModel() {
        setOpenLastURIOnLaunch(true);
    }

    @Nonnull
    @Override
    public List<JToolBar> createToolBars(Application a, View p) {
        LinkedList<JToolBar> list = new LinkedList<JToolBar>();
        return list;
    }

    @Override
    public void initApplication(Application a) {
        if ("Windows".equals(UIManager.getLookAndFeel().getID())) {
            UIManager.put("Tree.leafIcon", new ImageIcon(CubeTwisterApplicationModel.class.getResource("/ch/randelshofer/gui/images/WindowsFileIcon.png")));
            UIManager.put("Tree.rowHeight", 20);
            UIManager.put("Tree.textBackground", new AlphaColorUIResource(0x0));
        }
    }

    @Override
    public ActionMap createActionMap(@Nonnull Application a, View v) {
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

    @Nonnull
    @Override
    protected MenuBuilder createMenuBuilder() {
        CubeTwisterMenuBuilder mb= new CubeTwisterMenuBuilder();
        mb.setSuppressIcons(true);
        return mb;
    }

    @Nonnull
    @Override
    public JFileURIChooser createImportChooser(Application a, View v) {
        JFileURIChooser c = new JFileURIChooser();
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("ch.randelshofer.app.Labels"));
        c.setApproveButtonText(labels.getString("import.button"));
        c.setDialogType(JFileChooser.OPEN_DIALOG);
        c.setFileSelectionMode(JFileChooser.FILES_ONLY);
        c.setDialogTitle(labels.getString("import.title"));
        c.setAcceptAllFileFilterUsed(false);
        ExtensionFileFilter ff = new ExtensionFileFilter("CSV", new String[]{"csv", "txt"});
        c.addChoosableFileFilter(ff);
        ff = new ExtensionFileFilter("CubeExplorer","txt");
        c.addChoosableFileFilter(ff);
        c.setFileFilter(ff);

        return c;
    }

    @Nonnull
    @Override
    public JFileURIChooser createExportChooser(Application a, View v) {
        JFileURIChooser c = new JFileURIChooser();
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("org.jhotdraw.app.Labels"));
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

    @Nonnull
    @Override
    public JFileURIChooser createSaveChooser(Application a, View v) {
        JFileURIChooser c = new JFileURIChooser();

        ExtensionFileFilter ff = new ExtensionFileFilter("CubeTwister XML","xml");
        c.addChoosableFileFilter(ff);
        c.setFileFilter(ff);

        return c;
    }



}
