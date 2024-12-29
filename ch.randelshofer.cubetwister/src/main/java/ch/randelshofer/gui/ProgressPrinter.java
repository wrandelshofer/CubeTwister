/*
 * @(#)ProgressPrinter.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui;

import org.jhotdraw.annotation.Nonnull;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Formatter;
import java.util.Locale;

/**
 * ProgressPrinter.
 *
 * @author Werner Randelshofer
 */
public class ProgressPrinter implements ProgressObserver {
private boolean isPrint=true;
    private String note;
    private Runnable doCancel;
    private BoundedRangeModel model;
    private boolean isCancelable;
    private boolean isCanceled;
    private boolean isCompleted;
    private boolean isClosed;
    private boolean isIndeterminate;
    private String warning;
    private String error;
    private Formatter formatter;
    @Nonnull
    private ChangeListener changeHandler = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (model != null && model.getValue() >= model.getMaximum()) {
                complete();
            }
        }
    };

    public ProgressPrinter() {
        setModel(new DefaultBoundedRangeModel());
    }

    @Override
    public void setNote(String note) {
        this.note = note;
        if (isPrint)
        System.out.println(note);
    }

    @Override
    public void printf(@Nonnull String format, Object... args) {
        if ((formatter == null)//
                || (formatter.locale() != Locale.getDefault())) {
            formatter = new Formatter();

        }
        formatter.format(Locale.getDefault(), format, args);
        StringBuilder buf = (StringBuilder) formatter.out();
        setNote(buf.toString());
        buf.setLength(0);
    }

    @Override
    public void setDoCancel(Runnable doCancel) {
        this.doCancel = doCancel;
    }

    @Override
    public void setProgress(int nv) {
        model.setValue(nv);
    }

    @Override
    public void setMaximum(int m) {
        model.setMaximum(m);
    }

    @Override
    public void setMinimum(int m) {
        model.setMinimum(m);
    }

    @Override
    public void setCancelable(boolean b) {
        isCancelable = b;
    }

    @Override
    public void setModel(BoundedRangeModel brm) {
        if (model != null) {
            model.removeChangeListener(changeHandler);
        }
        model = brm;
        if (model != null) {
            model.addChangeListener(changeHandler);
        }
    }

    @Override
    public void cancel() {
        isCanceled = true;
        if (doCancel != null) {
            doCancel.run();
        }
    }

    @Override
    public void complete() {
        isClosed = true;
    }

    @Override
    public int getMaximum() {
        return model.getMaximum();
    }

    @Override
    public int getMinimum() {
        return model.getMinimum();
    }

    @Override
    public BoundedRangeModel getModel() {
        return model;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public int getProgress() {
        return model.getValue();
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public void setIndeterminate(boolean newValue) {
        isIndeterminate = newValue;
    }

    @Override
    public boolean isIndeterminate() {
        return isIndeterminate;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void setWarning(String message) {
        this.warning = message;
    }

    @Override
    public String getWarning() {
        return warning;
    }

    @Override
    public void setError(String message) {
        this.error = message;
    }

    @Override
    public String getError() {
        return error;
    }

    @Override
    public void setPrint(boolean newValue) {
        isPrint = newValue;
    }

    @Override
    public boolean isPrint() {
        return isPrint;
    }
}
