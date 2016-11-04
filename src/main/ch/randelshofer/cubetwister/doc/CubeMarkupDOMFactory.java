/*
 * @(#)CubeMarkupDOMFactory.java  1.0  May 16, 2004
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.cubetwister.doc.*;
import ch.randelshofer.xml.*;
import java.util.*;
/**
 * CubeMarkupDOMFactory.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CubeMarkupDOMFactory implements DOMFactory {
    
    /** Creates a new instance. */
    public CubeMarkupDOMFactory() {
    }
    

    private final static int VERSION = 1;
    
    private final static Object[][] classTagArray = {
        { CubeModel.class, "Cube" },
        { CubeColorModel.class, "Color" },
        { NotationModel.class, "Notation" },
        { ScriptModel.class, "Script" },
        { TextModel.class, "Text" },
    };
    private final static HashMap<Class<?>,String> classTagMap = new HashMap<Class<?>,String>();
    private final static HashMap<String,Class<?>> tagClassMap = new HashMap<String,Class<?>>();
    static {
        for (int i=0; i < classTagArray.length; i++) {
            classTagMap.put((Class<?>)classTagArray[i][0], (String) classTagArray[i][1]);
            tagClassMap.put((String)classTagArray[i][1], (Class<?>)classTagArray[i][0]);
        }
    }
    
    public Object create(String tagName) {
        try {
        return ((Class) tagClassMap.get(tagName)).newInstance();
        } catch (NullPointerException e) {
            IllegalArgumentException error = new IllegalArgumentException(tagName);
            //error.initCause(e); <- requires JDK 1.4
            throw error;
        } catch (Exception e) {
            InternalError error = new InternalError(e.toString());
            e.printStackTrace();
            //error.initCause(e); <- requires JDK 1.4
            throw error;
        }
    }
    
    @Override
    public String getTagName(DOMStorable o) {
        return classTagMap.get(o.getClass());
    }
    
}
