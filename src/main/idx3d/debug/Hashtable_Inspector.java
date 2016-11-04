package idx3d.debug;

import idx3d.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.applet.*;

public class Hashtable_Inspector extends InspectorFrame {

    private final static long serialVersionUID = 1L;

    public Hashtable_Inspector(java.util.Hashtable hash, String id) {
        super(hash, id);
        addEntry(new InspectorFrameEntry(this, "int", "size", hash.size() + ""));

        java.util.Enumeration enumer = hash.keys();
        int index = 0;
        Object key;
        while (enumer.hasMoreElements()) {
            key = enumer.nextElement();
            addEntry(new InspectorFrameEntry(this, hash.get(key), key.toString()));
        }
    }
}
