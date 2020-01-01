package idx3d.debug;

import idx3d.idx3d_Triangle;
import org.jhotdraw.annotation.Nonnull;

public class idx3d_Triangle_Inspector extends InspectorFrame {
    private final static long serialVersionUID = 1L;

    public idx3d_Triangle_Inspector(@Nonnull idx3d_Triangle tri, String id) {
        super(tri, id);

        addEntry(new InspectorFrameEntry(this, tri.parent, "parent"));
        addEntry(new InspectorFrameEntry(this, "int", "id", tri.id + ""));
        addEntry(new InspectorFrameEntry(this, tri.p1, "p1"));
        addEntry(new InspectorFrameEntry(this, tri.p2, "p2"));
        addEntry(new InspectorFrameEntry(this, tri.p3, "p3"));
        addEntry(new InspectorFrameEntry(this, tri.n, "n"));
        addEntry(new InspectorFrameEntry(this, tri.n2, "n2"));
	}
	
}