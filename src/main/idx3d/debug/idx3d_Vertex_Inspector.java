package idx3d.debug;

import idx3d.*;

public class idx3d_Vertex_Inspector extends InspectorFrame {

    private final static long serialVersionUID = 1L;

    public idx3d_Vertex_Inspector(idx3d_Vertex v, String id) {
        super(v, id);
        addEntry(new InspectorFrameEntry(this, v.parent, "parent"));
        addEntry(new InspectorFrameEntry(this, "int", "id", v.id + ""));
        addEntry(new InspectorFrameEntry(this, v.pos, "pos"));
        addEntry(new InspectorFrameEntry(this, v.n, "n"));
        addEntry(new InspectorFrameEntry(this, v.pos2, "pos2"));
        addEntry(new InspectorFrameEntry(this, v.n2, "n2"));
	//	addEntry(new InspectorFrameEntry(this,"float","u",v.u+""));
        //	addEntry(new InspectorFrameEntry(this,"float","v",v.v+""));
        addEntry(new InspectorFrameEntry(this, "int", "x", v.x + ""));
        addEntry(new InspectorFrameEntry(this, "int", "y", v.y + ""));
        addEntry(new InspectorFrameEntry(this, "int", "z", v.z + ""));

    }

}
