/*
 * @(#)CubeTwisterMenuBuilder.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister;

import ch.randelshofer.app.action.ImportFileAction;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultMenuBuilder;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.file.ExportFileAction;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JMenu;

public class CubeTwisterMenuBuilder extends DefaultMenuBuilder {

    @Override
    public void addExportFileItems(@Nonnull JMenu m, @Nonnull Application app, @Nullable View v) {
        ActionMap am = app.getActionMap(v);
        Action a;
        if (null != (a = am.get(ExportFileAction.ID))) {
            add(m, a);
        }
        if (null != (a = am.get(ImportFileAction.ID))) {
            add(m, a);
        }
    }
}
