/* @(#)UIDefaultsTableModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.debug;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Table model for UIDefaults.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * String.
 * <br>1.3.1 2010-04-10 Catch throwables when getting values from UIManager.
 * <br>1.3 2006-07-20 Reworked for Java 1.5.
 * <br>1.2 2004-05-22 UIDefaults key does not necessarily have to be a
 * String.
 * <br>1.1 2003-03-16
 */
public class UIDefaultsTableModel extends AbstractTableModel {
    private final static long serialVersionUID = 1L;

    private Object[][] data;
    private final static String[] columnNames = {"Key", "Value"};

    public UIDefaultsTableModel(UIDefaults p) {
        setUIDefaults(p);
    }

    public void setUIDefaults(UIDefaults p) {
        HashMap<Object,Object> combinedDefaults = new HashMap<Object,Object>();
        // We need this very bogus code to retrieve all the defaults, because
        // the UIDefaults class returned by the UIManager is bogus. :(
        for (Enumeration<Object> iter = UIManager.getDefaults().keys(); iter.hasMoreElements();) {
            Object key = iter.nextElement();
            try {
                combinedDefaults.put(key, UIManager.get(key));
            } catch (Throwable t) {
                combinedDefaults.put(key, t.getMessage());
            }
        }
        for (Iterator<Object> iter = UIManager.getDefaults().keySet().iterator(); iter.hasNext();) {
            Object key = iter.next();
            try {
                combinedDefaults.put(key, UIManager.get(key));
            } catch (Throwable t) {
                combinedDefaults.put(key, t.getMessage());
            }
        }
        for (Enumeration<Object> iter = p.keys(); iter.hasMoreElements();) {
            Object key = iter.nextElement();
            try {
                combinedDefaults.put(key, p.get(key));
            } catch (Throwable t) {
                combinedDefaults.put(key, t.getMessage());
            }
        }
        data = new Object[combinedDefaults.size()][2];
        int i = 0;
        for (Iterator<Map.Entry<Object,Object>> iter2 = combinedDefaults.entrySet().iterator(); iter2.hasNext();) {
            Map.Entry<Object,Object> entry = iter2.next();
            data[i][0] = entry.getKey().toString();
            data[i][1] = entry.getValue();
            i++;
        }
        Arrays.sort(data, new Comparator<Object[]>() {
@SuppressWarnings("unchecked")
            public int compare(Object[] o1, Object[] o2) {
                return ((Comparable) o1[0]).compareTo( (Comparable) o2[0]);
            }
        });
        fireTableDataChanged();
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return data.length;
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }
}
