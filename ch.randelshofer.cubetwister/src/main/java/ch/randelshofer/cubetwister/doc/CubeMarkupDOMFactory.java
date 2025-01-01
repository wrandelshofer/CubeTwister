/*
 * @(#)CubeMarkupDOMFactory.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.xml.DOMFactory;
import ch.randelshofer.xml.DOMStorable;
import org.jhotdraw.annotation.Nonnull;

import java.util.HashMap;
/**
 * CubeMarkupDOMFactory.
 *
 * @author Werner Randelshofer
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
            error.initCause(e);
            throw error;
        } catch (Exception e) {
            InternalError error = new InternalError(e.toString());
            e.printStackTrace();
            error.initCause(e);
            throw error;
        }
    }

    @Override
    public String getTagName(@Nonnull DOMStorable o) {
        return classTagMap.get(o.getClass());
    }

}
