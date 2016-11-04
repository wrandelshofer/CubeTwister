/*
 * @(#)CompositeModel.java 1.0  2001-10-04
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.undo;

import javax.swing.undo.*;

/**
 * This is basically the same like javax.swing.undo.CompoundEdit but
 * it has a slightly different behaviour:
 * The compound edit ends, when it is added to itself. This way it
 * can be fired two times to an UndoManager: The first time, when 
 * a sequence of compuondable edits starts, end the last time, when
 * the sequence is over.
 * <p>
 * For example:
 * <pre>
 * // fire CompositeEdit at start of sequence
 * CompositeEdit ce = new CompositeEdit();
 * fireUndoableEditEvent(ce);
 * 
 * ...fire edits which shall compounded here...
 *
 * // fire CompositeEdit at end of sequence again, to end it.
 * fireUndoableEditEvent(ce);
 * </pre>
 *
 * @author  Werner Randelshofer
 * @version 1.0 2001-10-04
 */
public class CompositeEdit extends CompoundEdit {
    private final static long serialVersionUID = 1L;
    private String presentationName;
    private boolean isSignificant;
    private boolean isVerbose;
    public void setVerbose(boolean b) {
        isVerbose = b;
    }
    /** 
     * Creates new CompositeEdit. 
     * Which uses CompoundEdit.getPresentatioName.
     *
     * @see javax.swing.undo.CompoundEdit#getPresentationName()
     */
    public CompositeEdit() {
        isSignificant = true;
    }
    /** 
     * Creates new CompositeEdit. 
     * Which uses CompoundEdit.getPresentatioName.
     *
     * @see javax.swing.undo.CompoundEdit#getPresentationName()
     */
    public CompositeEdit(boolean isSignificant) {
        this.isSignificant = isSignificant;
    }
    /** 
     * Creates new CompositeEdit. 
     * Which uses the given presentation name.
     * If the presentation name is null, then CompoundEdit.getPresentatioName
     * is used.
     * @see javax.swing.undo.CompoundEdit#getPresentationName()
     */
    public CompositeEdit(String presentationName) {
        this.presentationName = presentationName;
        isSignificant = true;
    }
    /** 
     * Creates new CompositeEdit. 
     * Which uses the given presentation name.
     * If the presentation name is null, then CompoundEdit.getPresentatioName
     * is used.
     * @see javax.swing.undo.CompoundEdit#getPresentationName()
     */
    public CompositeEdit(String presentationName, boolean isSignificant) {
        this.presentationName = presentationName;
        this.isSignificant = isSignificant;
    }
    
    /**
     * Returns the presentation name.
     * If the presentation name is null, then CompoundEdit.getPresentatioName
     * is returned.
     * @see javax.swing.undo.CompoundEdit#getPresentationName()
     */
    public String getPresentationName() {
        return (presentationName != null) ? presentationName : super.getPresentationName();
    }
    /**
     * Returns the undo presentation name.
     * If the presentation name is null, then CompoundEdit.getUndoPresentationName
     * is returned.
     * @see javax.swing.undo.CompoundEdit#getUndoPresentationName()
     */
    public String getUndoPresentationName() {
        return ((presentationName != null) ? "Undo "+presentationName : super.getUndoPresentationName());
    }
    /**
     * Returns the redo presentation name.
     * If the presentation name is null, then CompoundEdit.getRedoPresentationName
     * is returned.
     * @see javax.swing.undo.CompoundEdit#getRedoPresentationName()
     */
    public String getRedoPresentationName() {
        return ((presentationName != null) ? "Redo "+presentationName : super.getRedoPresentationName());
    }

    /**
     * If this edit is inProgress, accepts anEdit and returns
     * true.
     *
     * <p>The last edit added to this CompositeEdit is given a
     * chance to addEdit(anEdit). If it refuses (returns false), anEdit is
     * given a chance to replaceEdit the last edit. If anEdit returns
     * false here, it is added to edits.</p>
     *
     * <p>If the CompositeEdit is added to itself, then method end()
     * is called, and true is returned.</p>
     */
    public boolean addEdit(UndoableEdit anEdit) {
        if (anEdit == this) {
            end();
            return true;
        } else {
            return super.addEdit(anEdit);
        }
    }
    
    /**
     * Returns false if this edit is insignificant - for example one
     * that maintains the user's selection, but does not change
     * any model state.
     */
    public boolean isSignificant() {
        return (isSignificant) ? super.isSignificant() : false;
        //return isSignificant;
    }
}
