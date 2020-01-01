package idx3d.debug;

import idx3d.idx3d_Scene;
import org.jhotdraw.annotation.Nonnull;

public class idx3d_Scene_Inspector extends InspectorFrame {
    private final static long serialVersionUID = 1L;

    public idx3d_Scene_Inspector(@Nonnull idx3d_Scene scene, String id) {
        super(scene, id);
        //addEntry(new InspectorFrameEntry(this,"int","width",scene.width+""));
        //addEntry(new InspectorFrameEntry(this,"int","height",scene.height+""));
        addEntry(new InspectorFrameEntry(this, scene.matrix, "matrix"));
        addEntry(new InspectorFrameEntry(this, scene.normalmatrix, "normalmatrix"));

        addEntry(new InspectorFrameEntry(this, scene.objectData, "objectData"));
        addEntry(new InspectorFrameEntry(this, scene.lightData, "lightData"));
        addEntry(new InspectorFrameEntry(this, scene.materialData, "materialData"));
		addEntry(new InspectorFrameEntry(this,scene.cameraData,"cameraData"));
	}
	
}