/*
 * @(#)ProgressView.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui;

import org.jhotdraw.annotation.Nonnull;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Formatter;
import java.util.Locale;

/**
 * A class to monitor the progress of some operation.
 *
 *
 * @author Werner Randelshofer
 */
public class ProgressView extends javax.swing.JPanel implements ProgressObserver {

    private final static long serialVersionUID = 1L;
    private boolean isPrint;
    private boolean isCanceled, isCompleted, isClosed, isCancelable = true;
    private javax.swing.BoundedRangeModel model;
    private Runnable doCancel;
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

    /**
     * Creates a new ProgressView.
     */
    public ProgressView(String message, String note, boolean isIndeterminate) {
        this(message, note, 0, 100, isIndeterminate);
    }

    /**
     * Creates a new ProgressView.
     */
    public ProgressView(String message, String note, int min, int max) {
        this(message, note, min, max, false);
    }

    /**
     * Creates a new ProgressView.
     */
    public ProgressView(final String message, final String note, final int min, final int max, final boolean isIndeterminate) {
        invokeAndWait(new Runnable() {

            @Override
            public void run() {
                initComponents();
                setModel(new DefaultBoundedRangeModel(min, 0, min, max));
                progressBar.setIndeterminate(isIndeterminate);
                messageLabel.setText(message);
                noteLabel.setText(note);
                warningLabel.setVisible(false);
                errorLabel.setVisible(false);
                closeButton.setVisible(false);
                ProgressFrame.getInstance().addProgressView(ProgressView.this);
            }
        });
    }

    /**
     * Creates a new indeterminate ProgressView.
     */
    public ProgressView(String message, String note) {
        this(message, note, 0, 100, false);
    }

    @Override
    public void setModel(BoundedRangeModel brm) {
        if (model != null) {
            model.removeChangeListener(changeHandler);
        }
        model = brm;
        progressBar.setModel(brm);
        if (model != null) {
            model.addChangeListener(changeHandler);
        }
    }

    @Override
    public BoundedRangeModel getModel() {
        return model;
    }

    /**
     * Set cancelable to false if the operation can not be canceled.
     */
    @Override
    public void setCancelable(final boolean b) {
        isCancelable = b;
        invokeAndWait(new Runnable() {

            @Override
            public void run() {
                cancelButton.setVisible(b);
                invalidate();
                validate();
            }
        });
    }

    /**
     * The specified Runnable is executed when the user presses the cancel
     * button.
     */
    @Override
    public void setDoCancel(Runnable doCancel) {
        this.doCancel = doCancel;
    }

    /**
     * Indicate the progress of the operation being monitored. If the specified
     * value is &gt;= the maximum, the progress monitor is closed.
     *
     * @param nv an int specifying the current value, between the maximum and
     * minimum specified for this component
     * @see #setMinimum
     * @see #setMaximum
     * @see #close
     */
    @Override
    public void setProgress(int nv) {
        model.setValue(nv);
    }

    /**
     * Returns the progress of the operation being monitored.
     */
    @Override
    public int getProgress() {
        return model.getValue();
    }

    /**
     * Indicate that the operation is complete. This happens automatically when
     * the value set by setProgress is &gt;= max, but it may be called earlier if
     * the operation ends early.
     */
    @Override
    public void complete() {
        if (!isCompleted) {
            isCompleted = true;
            progressBar.setValue(getMaximum());
            progressBar.setIndeterminate(false);
            setIndeterminate(false);
            cancelButton.setVisible(false);
            closeButton.setVisible(true);
            revalidate();
        }
    }

    /**
     * Closes the progress view.
     */
    @Override
    public void close() {
        if (!isClosed) {
            isClosed = true;
            ProgressFrame.getInstance().removeProgressView(this);
            if (model != null) {
                model.removeChangeListener(changeHandler);
            }
        }
    }

    /**
     * Returns the minimum value -- the lower end of the progress value.
     *
     * @return an int representing the minimum value
     * @see #setMinimum
     */
    @Override
    public int getMinimum() {
        return model.getMinimum();
    }

    /**
     * Specifies the minimum value.
     *
     * @param m an int specifying the minimum value
     * @see #getMinimum
     */
    @Override
    public void setMinimum(int m) {
        model.setMinimum(m);
    }

    /**
     * Returns the maximum value -- the higher end of the progress value.
     *
     * @return an int representing the maximum value
     * @see #setMaximum
     */
    @Override
    public int getMaximum() {
        return model.getMaximum();
    }

    /**
     * Specifies the maximum value.
     *
     * @param m an int specifying the maximum value
     * @see #getMaximum
     */
    @Override
    public void setMaximum(int m) {
        model.setMaximum(m);
    }

    /**
     * Returns true if the user has hit the Cancel button in the progress
     * dialog.
     */
    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    /**
     * Returns true if the operation is completed.
     */
    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Returns true if the progress view is closed.
     */
    @Override
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Cancels the operation. This method must be invoked from the user event
     * dispatch thread.
     */
    @Override
    public void cancel() {
        if (isCancelable) {
            isCanceled = true;
            cancelButton.setEnabled(false);
            noteLabel.setText("Canceling...");
            if (doCancel != null) {
                doCancel.run();
            }
        } else {
            noteLabel.setText("This process can not be canceled!");
        }
    }

    /**
     * Specifies the additional note that is displayed along with the progress
     * message. Used, for example, to show which file the is currently being
     * copied during a multiple-file copy.
     *
     * @param note a String specifying the note to display
     * @see #getNote
     */
    @Override
    public void setNote(String note) {
        if (isPrint) {
            System.out.println(note);
        }
        if (!isCanceled) {
            noteLabel.setText(note);
        }
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

    /**
     * Specifies the additional note that is displayed along with the progress
     * message.
     *
     * @return a String specifying the note to display
     * @see #setNote
     */
    @Override
    public String getNote() {
        return noteLabel.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        messageLabel = new javax.swing.JLabel();
        noteLabel = new javax.swing.JLabel();
        warningLabel = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        cancelButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        separator = new javax.swing.JSeparator();

        setLayout(new java.awt.GridBagLayout());

        messageLabel.setFont(messageLabel.getFont().deriveFont(messageLabel.getFont().getStyle() | java.awt.Font.BOLD));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(messageLabel, gridBagConstraints);

        noteLabel.setFont(noteLabel.getFont().deriveFont(noteLabel.getFont().getSize()-2f));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 12);
        add(noteLabel, gridBagConstraints);

        warningLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ch/randelshofer/gui/images/ProgressView.warningIcon.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 12);
        add(warningLabel, gridBagConstraints);

        errorLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ch/randelshofer/gui/images/ProgressView.errorIcon.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 12);
        add(errorLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(progressBar, gridBagConstraints);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 12);
        add(cancelButton, gridBagConstraints);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 12);
        add(closeButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        add(separator, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void close(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close
        close();
    }//GEN-LAST:event_close

    private void cancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel
        cancel();
    }//GEN-LAST:event_cancel

    @Override
    public void setWarning(String message) {
        warningLabel.setText(message);
        if (!warningLabel.isVisible()) {
            invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    warningLabel.setVisible(getWarning() != null && getError() == null);
                    revalidate();
                }
            });
        }
    }

    private static void invokeAndWait(@Nonnull Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public String getWarning() {
        return warningLabel.getText();
    }

    @Override
    public void setError(String message) {
        errorLabel.setText(message);
        if (!errorLabel.isVisible()) {
            invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    warningLabel.setVisible(getWarning() != null && getError() == null);
                    errorLabel.setVisible(getError() != null);
                    revalidate();
                }
            });
        }
    }

    @Override
    public String getError() {
        return errorLabel.getText();
    }

    @Override
    public void setIndeterminate(boolean newValue) {
        progressBar.setIndeterminate(newValue);
    }

    @Override
    public boolean isIndeterminate() {
        return progressBar.isIndeterminate();
    }

    @Override
    public void setPrint(boolean newValue) {
        isPrint = newValue;
    }

    @Override
    public boolean isPrint() {
        return isPrint;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSeparator separator;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables
}
