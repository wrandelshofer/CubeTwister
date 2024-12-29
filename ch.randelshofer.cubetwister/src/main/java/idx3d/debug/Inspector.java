package idx3d.debug;

import idx3d.idx3d_InternalMaterial;
import idx3d.idx3d_Light;
import idx3d.idx3d_Matrix;
import idx3d.idx3d_Object;
import idx3d.idx3d_Scene;
import idx3d.idx3d_Texture;
import idx3d.idx3d_Triangle;
import idx3d.idx3d_Vector;
import idx3d.idx3d_Vertex;
import org.jhotdraw.annotation.Nullable;

import java.util.Hashtable;
import java.util.Vector;


public class Inspector {
    private Inspector() {
    }

    public static void inspect(Object obj) {
        inspect(obj, null);
    }

    public static void inspect(Object obj, @Nullable String name) {
        String id = (name != null) ? name : "";
        if (obj instanceof idx3d_Vector) {
            new idx3d_Vector_Inspector((idx3d_Vector) obj, id);
        }
        if (obj instanceof idx3d_Vertex) {
            new idx3d_Vertex_Inspector((idx3d_Vertex) obj, id);
        }
        if (obj instanceof idx3d_Object) {
            new idx3d_Object_Inspector((idx3d_Object) obj, id);
        }
        if (obj instanceof idx3d_Matrix) {
            new idx3d_Matrix_Inspector((idx3d_Matrix) obj, id);
        }
        if (obj instanceof idx3d_Triangle) {
            new idx3d_Triangle_Inspector((idx3d_Triangle) obj, id);
        }
        if (obj instanceof idx3d_Scene) {
            new idx3d_Scene_Inspector((idx3d_Scene) obj, id);
        }
        if (obj instanceof idx3d_Texture) {
            new idx3d_Texture_Inspector((idx3d_Texture) obj, id);
        }
        if (obj instanceof idx3d_Light) {
            new idx3d_Light_Inspector((idx3d_Light) obj, id);
        }
		if (obj instanceof idx3d_InternalMaterial) new idx3d_Material_Inspector((idx3d_InternalMaterial)obj,id);

		if (obj instanceof Vector) new Vector_Inspector((Vector)obj,id);
		if (obj instanceof Hashtable) new Hashtable_Inspector((Hashtable)obj,id);
	}
	
}
