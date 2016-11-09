/* @(#)CubeMacro.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.cubetwister.doc;

import java.beans.*;
import ch.randelshofer.undo.*;
/**
 *
 * @author  werni
 * @version $Id$
 */
public class MacroModel extends InfoModel  {
    private final static long serialVersionUID = 1L;
    public static final String PROP_IDENTIFIER = "Identifier";
    public static final String PROP_SCRIPT = "Script";

    private String identifier;
    private String script;

    /** Creates new CubeMacro */
    public MacroModel() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String value) {
        String oldValue = identifier;
        if (value.equals(oldValue))
            return;
        
        basicSetIdentifier(value);
        firePropertyChange (PROP_IDENTIFIER, oldValue, value);
        /*
        if (getParent() instanceof ScriptModel) {
            ((ScriptModel) getParent()).uncheck();
        }
        */
        fireUndoableEditHappened(
            new UndoableObjectEdit(this, "Identifier", oldValue, value) {
    private final static long serialVersionUID = 1L;
                public void revert(Object a, Object b) {
                    identifier = (String) b;
                    firePropertyChange(PROP_IDENTIFIER, a, b);
                }
            }
        );
    }
    public void basicSetIdentifier(String value) {
        identifier = value;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String value) {
        String oldValue = script;
        if (value.equals(oldValue))
            return;

        basicSetScript(value);

        firePropertyChange (PROP_SCRIPT, oldValue, value);
        /*
        if (getParent() instanceof ScriptModel) {
            ((ScriptModel) getParent()).uncheck();
        }
        */
        fireUndoableEditHappened(
            new UndoableObjectEdit(this, "Script", oldValue, value) {
    private final static long serialVersionUID = 1L;
                public void revert(Object a, Object b) {
                    script = (String) b;
                    firePropertyChange(PROP_SCRIPT, a, b);
                }
            }
        );
    }
    public void basicSetScript(String value) {
        script = value;
    }
    
    public String toString() {
        return identifier;
    }
    
    public Object getUserObject() {
        return identifier;
    }
    
    public void setUserObject(Object obj) {
        setIdentifier((String) obj);
    }

}
