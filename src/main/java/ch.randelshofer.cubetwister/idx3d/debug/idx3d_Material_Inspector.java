package idx3d.debug;

import idx3d.idx3d_InternalMaterial;
import org.jhotdraw.annotation.Nonnull;

public class idx3d_Material_Inspector extends InspectorFrame
{
	    private final static long serialVersionUID = 1L;

    public idx3d_Material_Inspector(@Nonnull idx3d_InternalMaterial m, String id) {
        super(m, id);
        addEntry(new InspectorFrameEntry(this, m.getTexture(), "texture"));
        addEntry(new InspectorFrameEntry(this, m.getEnvmap(), "envmap"));
        addEntry(new InspectorFrameEntry(this, "int", "color", "0x" + Integer.toHexString(m.getColor())));
        addEntry(new InspectorFrameEntry(this, "int", "transparency", "" + m.getTransparency()));
        addEntry(new InspectorFrameEntry(this, "int", "reflectivity", "" + m.getReflectivity()));
    }
}