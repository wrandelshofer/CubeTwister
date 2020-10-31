package idx3d.debug;

import idx3d.idx3d_Texture;
import org.jhotdraw.annotation.Nonnull;

public class idx3d_Texture_Inspector extends InspectorFrame {
    private final static long serialVersionUID = 1L;

    public idx3d_Texture_Inspector(@Nonnull idx3d_Texture t, String id) {
        super(t, id);

        addEntry(new InspectorFrameEntry(this, "int", "width", t.width + ""));
        addEntry(new InspectorFrameEntry(this, "int", "height", t.height + ""));
    }
	
}