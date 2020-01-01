package idx3d.debug;

import idx3d.idx3d_Light;
import org.jhotdraw.annotation.Nonnull;

public class idx3d_Light_Inspector extends InspectorFrame {
    private final static long serialVersionUID = 1L;

    public idx3d_Light_Inspector(@Nonnull idx3d_Light l, String id) {
        super(l, id);

        addEntry(new InspectorFrameEntry(this, "int", "diffuse", "0x" + Integer.toHexString(l.diffuse)));
        addEntry(new InspectorFrameEntry(this, "int", "specular", "0x" + Integer.toHexString(l.specular)));
        addEntry(new InspectorFrameEntry(this, "int", "highlightSheen", "" + l.highlightSheen));
        addEntry(new InspectorFrameEntry(this, "int", "highlightSpread", "" + l.highlightSpread));
        addEntry(new InspectorFrameEntry(this, l.v, "v"));
        addEntry(new InspectorFrameEntry(this, l.v2, "v2"));
    }
	
}