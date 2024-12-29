package idx3d.debug;

import idx3d.idx3d_Object;
import org.jhotdraw.annotation.Nonnull;

public class idx3d_Object_Inspector extends InspectorFrame {
    private final static long serialVersionUID = 1L;

    public idx3d_Object_Inspector(@Nonnull idx3d_Object obj, String id) {
        super(obj, id);
        addEntry(new InspectorFrameEntry(this, obj.parent, "parent"));
        addEntry(new InspectorFrameEntry(this, "String", "name", obj.name));
        addEntry(new InspectorFrameEntry(this, "int", "id", obj.id + ""));
        addEntry(new InspectorFrameEntry(this, obj.material, "material"));

        addEntry(new InspectorFrameEntry(this, obj.matrix, "matrix"));
        addEntry(new InspectorFrameEntry(this, obj.normalmatrix, "normalmatrix"));
        addEntry(new InspectorFrameEntry(this, "int", "vertices", obj.vertices + ""));
		addEntry(new InspectorFrameEntry(this,"int","triangles",obj.triangles+""));
		addEntry(new InspectorFrameEntry(this,obj.vertexData,"vertexData"));
		addEntry(new InspectorFrameEntry(this,obj.triangleData,"triangleData"));
	}
}