package idx3d.debug;

import idx3d.idx3d_Vector;
import org.jhotdraw.annotation.Nonnull;

public class idx3d_Vector_Inspector extends InspectorFrame {
    private final static long serialVersionUID = 1L;

    public idx3d_Vector_Inspector(@Nonnull idx3d_Vector vec, String id) {
        super(vec, id);

        addEntry(new InspectorFrameEntry(this, "float", "x", vec.x + ""));
        addEntry(new InspectorFrameEntry(this, "float", "y", vec.y + ""));
        addEntry(new InspectorFrameEntry(this, "float", "z", vec.z + ""));
    }
	
}