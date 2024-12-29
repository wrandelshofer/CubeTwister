// | -----------------------------------------------------------------
// | idx3d III is (c)1999/2000 by Peter Walser
// | -----------------------------------------------------------------
// | idx3d is a 3d engine written in 100% pure Java (1.1 compatible)
// | and provides a fast and flexible API for software 3d rendering
// | on the Java platform.
// |
// | Feel free to use the idx3d API / classes / source code for
// | non-commercial purposes (of course on your own risk).
// | If you intend to use idx3d for commercial purposes, please
// | contact me with an e-mail [proxima@active.ch].
// |
// | Thanx & greetinx go to:
// | * Wilfred L. Guerin, 	for testing, bug report, and tons
// |			of brilliant suggestions
// | * Sandy McArthur,	for reverse loops
// | * Dr. Douglas Lyons,	for mentioning idx3d1 in his book
// | * Hugo Elias,		for maintaining his great page
// | * the comp.graphics.algorithms people,
// | 			for scientific concerns
// | * Tobias Hill,		for inspiration and awakening my
// |			interest in java gfx coding
// | * Kai Krause,		for inspiration and hope
// | * Incarom & Parisienne,	for keeping me awake during the
// |			long coding nights
// | * Doris Langhard,	for being the sweetest girl on earth
// | * Etnica, Infinity Project, X-Dream and "Space Night"@BR3
// | 			for great sound while coding
// | and all coderz & scenerz out there (keep up the good work, ppl :)
// |
// | Peter Walser
// | proxima@active.ch
// | http://www2.active.ch/~proxima
// | "On the eigth day, God started debugging"
// | -----------------------------------------------------------------

package idx3d;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

/**
 * Used to create instances of idx3d_Object for given object types.
 */
@SuppressWarnings("cast")
public class idx3d_ObjectFactory {
    public final static double pi = 3.1415926535;
    public final static double deg2rad = pi / 180;
    
    // F A C T O R Y   M E T H O D S

    @Nonnull
    public static idx3d_Object FIELD3D(int resolution, float height) {
        float x,y;
        float map[][] = new float[resolution][resolution];
        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {
                x = (float) i / (float) resolution * 2 - 1;
                y = (float) j / (float) resolution * 2 - 1;
                map[i][j] = x * x * x * y - y * y * y * x + (float) (Math.sin(x * y * 10) / 4 - 0.2);
            }
        }
        return idx3d_ObjectFactory.HEIGHTFIELD(map, height, true);
    }

    @Nonnull
    public static idx3d_Object HEIGHTFIELD(@Nonnull idx3d_Texture heightmap, float height, boolean doubleSided) {
        heightmap.toAverage();
        float data[][] = new float[heightmap.width][heightmap.height];
        int offset;

        for (int j = heightmap.height - 1; j >= 0; j--) {
            offset = j * heightmap.width;
            for (int i = heightmap.width - 1; i >= 0; i--) {
                data[i][j] = (float) (heightmap.pixel[offset + i] - 127) / 127f;
            }
        }
        return idx3d_ObjectFactory.HEIGHTFIELD(data, height, doubleSided);
    }

    @Nonnull
    public static idx3d_Object HEIGHTFIELD(@Nonnull float[][] data, float height, boolean doubleSided) {
        idx3d_Object newObject = new idx3d_Object();
        idx3d_Vertex vertex = null;
        float xtemp, ytemp, ztemp;
        int q1, q2, q3, q4;
        float u1, u4, v1, v4; // coordinates for texture mapping

        int xmax = data.length;
        int ymax = data[0].length;

        float xscale = 2 / (float) (xmax - 1);
        float yscale=2/(float)(ymax-1);
        int doubleSideOffset=xmax*ymax;
        
        for (int i=0; i<xmax; i++) {
            //u=(float)i/(float)(xmax-1);
            for (int j=0; j<ymax; j++) {
                //v=(float)j/(float)(ymax-1);
                xtemp=-1+xscale*(float)i;
                ytemp=-1+yscale*(float)j;
                ztemp=data[i][j]*height;
                vertex=new idx3d_Vertex(xtemp,ytemp,ztemp);
                //vertex.u=u; vertex.v=v;
                newObject.addVertex(vertex);
                
            }
        }
        if (doubleSided)
            for (int i=0; i<xmax; i++) {
            //u=(float)(xmax-i)/(float)xmax;
            for (int j=0; j<ymax; j++) {
                //v=(float)j/(float)(ymax-1);
                xtemp=-1+xscale*(float)i;
                ytemp=-1+yscale*(float)j;
                ztemp=data[i][j]*height;
                vertex=new idx3d_Vertex(xtemp,ytemp,ztemp);
                //vertex.u=u; vertex.v=v;
                newObject.addVertex(vertex);
                
            }
            }
        
        for (int i=0; i<(xmax-1); i++) {
            for (int j=0; j<(ymax-1); j++) {
                q1=j+xmax*i;
                //q2=j+1+xmax*i;
                q2=q1+1;
                q3=j+xmax*(i+1);
                //q4=j+1+xmax*(i+1);
                q4=q3+1;
                u1=(float)i/(float)(xmax-1);
                v1=(float)j/(float)(ymax-1);
                u4=(float)(i+1)/(float)(xmax-1);
                v4=(float)(j+1)/(float)(ymax-1);
                
                newObject.addTriangle(q1,q2,q3, u1,v1,u1,v4,u4,v1);
                newObject.addTriangle(q2,q4,q3, u1,v4,u4,v4,u4,v1);
                if (doubleSided) {
                    newObject.addTriangle(doubleSideOffset+q1,doubleSideOffset+q3,doubleSideOffset+q2, u1,v1,u4,v1,u1,v4);
                    newObject.addTriangle(doubleSideOffset+q2,doubleSideOffset+q3,doubleSideOffset+q4, u1,v4,u4,v1,u4,v4);
                }
            }
        }

        return newObject;
    }

    @Nonnull
    public static idx3d_Object CUBE(float size) {
        return BOX(size, size, size);
    }

    @Nonnull
    public static idx3d_Object BOX(@Nonnull idx3d_Vector size) {
        return BOX(size.x, size.y, size.z);
    }

    @Nonnull
    public static idx3d_Object BOX(float xsize, float ysize, float zsize) {
        float x = (float) Math.abs(xsize / 2);
        float y = (float) Math.abs(ysize / 2);
        float z = (float) Math.abs(zsize / 2);

        float xx, yy, zz;

        idx3d_Object n=new idx3d_Object();
        int[] xflag=new int[6];
        int[] yflag=new int[6];
        int[] zflag=new int[6];

        xflag[0]=10; yflag[0]=3; zflag[0]=0;
        xflag[1]=10; yflag[1]=15; zflag[1]=3;
        xflag[2]=15; yflag[2]=3; zflag[2]=10;
        xflag[3]=10; yflag[3]=0; zflag[3]=12;
        xflag[4]=0; yflag[4]=3; zflag[4]=5;
        xflag[5]=5; yflag[5]=3; zflag[5]=15;

        for (int side=0; side<6; side++) {
            for (int i=0; i<4; i++) {
                xx=((xflag[side]&(1<<i))>0)?x:-x;
                yy=((yflag[side]&(1<<i))>0)?y:-y;
                zz=((zflag[side]&(1<<i))>0)?z:-z;
                //n.addVertex(xx,yy,zz,i&1,(i&2)>>1);
                n.addVertex(xx,yy,zz);
            }
            int t=side<<2;
            n.addTriangle(t,t+2,t+3,0f,0f,0f,1f,1f,1f);
            n.addTriangle(t,t+3,t+1,0f,0f,1f,1f,1f,0f);
        }

        return n;
    }

    @Nonnull
    public static idx3d_Object CONE(float height, float radius, int segments) {
        idx3d_Vector[] path=new idx3d_Vector[4];
        float h=height/2;
        path[0]=new idx3d_Vector(0,h,0);
        path[1]=new idx3d_Vector(radius,-h,0);
        path[2]=new idx3d_Vector(radius,-h,0);
        path[3]=new idx3d_Vector(0,-h,0);

        return ROTATIONOBJECT(path,segments);
    }

    @Nonnull
    public static idx3d_Object CYLINDER(float height, float radius, int segments) {
                /*
                idx3d_Vector[] path=new idx3d_Vector[6];
                float h=height/2;
                path[0]=new idx3d_Vector(0,h,0);
                path[1]=new idx3d_Vector(radius,h,0);
                path[2]=new idx3d_Vector(radius,h,0);
                path[3]=new idx3d_Vector(radius,-h,0);
                path[4]=new idx3d_Vector(radius,-h,0);
                path[5]=new idx3d_Vector(0,-h,0);
                return ROTATIONOBJECT(path,segments);
                 */
        return CYLINDER(height, radius, segments, true, true);
    }

    @Nullable
    public static idx3d_Object CYLINDER(float height, float radius, int segments, boolean withCeiling, boolean withFloor) {
        idx3d_Vector[] path=new idx3d_Vector[2];
        float h=height/2;
        idx3d_Object walls, floor, cylinder;

        cylinder = null;

        if (withCeiling) {
            path[0]=new idx3d_Vector(0,h,0);
            path[1]=new idx3d_Vector(radius,h,0);
            cylinder = ROTATIONOBJECT(path,segments);
        }

        path[0]=new idx3d_Vector(radius,h,0);
        path[1]=new idx3d_Vector(radius,-h,0);
        walls = ROTATIONOBJECT(path,segments);
        if (cylinder == null) cylinder = walls;
        else cylinder.incorporateGeometry(walls);

        if (withFloor) {
            path[0]=new idx3d_Vector(radius,-h,0);
            path[1]=new idx3d_Vector(0,-h,0);
            floor = ROTATIONOBJECT(path,segments);
            cylinder.incorporateGeometry(floor);
        }

        return cylinder;
    }

    @Nonnull
    public static idx3d_Object SPHERE(float radius, int segments) {
        idx3d_Vector[] path=new idx3d_Vector[segments];

        float x,y,angle;

        path[0]=new idx3d_Vector(0,radius,0);
        path[segments-1]=new idx3d_Vector(0,-radius,0);

        for (int i = 1; i < segments - 1; i++) {
            angle = -(((float) i / (float) (segments - 2)) - 0.5f) * 3.14159265f;
            x = (float) Math.cos(angle) * radius;
            y = (float) Math.sin(angle) * radius;
            path[i] = new idx3d_Vector(x, y, 0);
        }

        return ROTATIONOBJECT(path, segments);
    }

    @Nonnull
    public static idx3d_Object ROTATIONOBJECT(@Nonnull idx3d_Vector[] path, int sides) {
        int steps = sides + 1;
        idx3d_Object newObject = new idx3d_Object();
        double alpha;
        alpha = 2 * pi / ((double) sides);
        float qx, qz;
        int nodes = path.length;
        idx3d_Vertex vertex = null;
        float u1, v1, u4, v4;    // Texture coordinates

        // If there is an exact match for the last and the first entry of the
        // path, we merge it down.
        boolean merge = (path[0].x == path[nodes - 1].x &&
                path[0].y == path[nodes - 1].y &&
                path[0].z == path[nodes - 1].z);
        if (merge) nodes--;
        
        for (int j=0; j < sides; j++) {
            //u=(float)(steps-j-1)/(float)(steps-1);
            for (int i=0;i < nodes ;i++) {
                //v=(float)i/(float)(nodes-1);
                qx=(float)(path[i].x*Math.cos(j*alpha)+path[i].z*Math.sin(j*alpha));
                qz=(float)(path[i].z*Math.cos(j*alpha)-path[i].x*Math.sin(j*alpha));
                vertex=new idx3d_Vertex(qx,path[i].y,qz);
                //vertex.u=u; vertex.v=v;
                newObject.addVertex(vertex);
            }
        }
        
        for (int j=0; j < sides - 1; j++) {
            u1=(float)(sides-j-1)/(float)(sides);
            u4=(float)(sides-j-2)/(float)(sides);
            
            for (int i=0;i < path.length - 1; i++) {
                v1=(float)i/(float)(path.length-1);
                v4=(float)(i+1)/(float)(path.length-1);
                
                if (merge && i == path.length - 2) {
                    newObject.addTriangle(i+nodes*j,i+nodes*(j+1),nodes*j,u1,v1,u4,v1,u1,v4);
                    newObject.addTriangle(i+nodes*(j+1),nodes*(j+1),nodes*j,u4,v1,u4,v4,u1,v4);
                } else {
                    newObject.addTriangle(i+nodes*j,i+nodes*(j+1),i+1+nodes*j,u1,v1,u4,v1,u1,v4);
                    newObject.addTriangle(i+nodes*(j+1),i+1+nodes*(j+1),i+1+nodes*j,u4,v1,u4,v4,u1,v4);
                }
            }
        }
        
        
        //u1=1f/(float)(sides);
        //u4=0f;
        u1=1f;
        u4=(sides - 1f)/(float)(sides);
        for (int i=0;i < path.length - 1;i++) {
            v1=(float)i/(float)(path.length-1);
            v4=(float)(i+1)/(float)(path.length-1);
            if (merge && i == path.length - 2) {
                newObject.addTriangle(i+nodes*(sides-1),i,nodes*(sides-1),u1,v1,u4,v1,u1,v4);
                newObject.addTriangle(i,i+1,nodes*(sides-1), u4,v1,u4,v4,u1,v4);
            } else {
                newObject.addTriangle(i + nodes * (sides - 1), i, i + 1 + nodes * (sides - 1), u1, v1, u4, v1, u1, v4);
                newObject.addTriangle(i, i + 1, i + 1 + nodes * (sides - 1), u4, v1, u4, v4, u1, v4);
            }
        }

        return newObject;

    }

    @Nonnull
    public static idx3d_Object TORUSKNOT(float p, float q, float r_tube, float r_out, float r_in, float h, int segments, int steps) {
        float x, y, z, r, t, theta, temp;

        idx3d_Vector[] path = new idx3d_Vector[segments + 1];
        for (int i = 0; i < segments + 1; i++) {
            t = 2 * 3.14159265f * i / (float) segments;
            r = r_out + r_in * idx3d_Math.cos(p * t);
            z = h * idx3d_Math.sin(p * t);
            theta = q * t;
            x = r * idx3d_Math.cos(theta);
            y = r * idx3d_Math.sin(theta);
            path[i] = new idx3d_Vector(x, y, z);
        }
        return TUBE(path, r_tube, steps, true);
    }

    @Nonnull
    public static idx3d_Object SPIRAL(float h, float r_out, float r_in, float r_tube, float w, float f, int segments, int steps) {
        float x, y, z, r, t, theta, temp;

        idx3d_Vector[] path = new idx3d_Vector[segments + 1];
        for (int i = 0; i < segments + 1; i++) {
            t = (float) i / (float) segments;
            r = r_out + r_in * idx3d_Math.sin(2 * 3.14159265f * f * t);
            z = (h / 2) + h * t;
            theta = 2 * 3.14159265f * w * t;
            x = r * idx3d_Math.cos(theta);
            y = r * idx3d_Math.sin(theta);
            path[i] = new idx3d_Vector(x, y, z);
        }
        return TUBE(path, r_tube, steps, false);
    }

    @Nonnull
    public static idx3d_Object TUBE(@Nonnull idx3d_Vector[] path, float r, int steps, boolean closed) {
        idx3d_Vector[] circle = new idx3d_Vector[steps];
        float angle;
        for (int i = 0; i < steps; i++) {
            angle = 2 * 3.14159265f * (float) i / (float) steps;
            circle[i] = new idx3d_Vector(r * idx3d_Math.cos(angle), r * idx3d_Math.sin(angle), 0f);
        }

        idx3d_Object newObject = new idx3d_Object();
        int segments = path.length;
        idx3d_Vector forward, up, right;
        idx3d_Matrix frenetmatrix;
        idx3d_Vertex tempvertex;
        float relx,rely;
        int a,b,c,d;
        
        for (int i=0;i<segments;i++) {
            // Calculate frenet frame matrix
            
            if (i!=segments-1) forward=idx3d_Vector.sub(path[i+1],path[i]);
            else {
                if (!closed) forward=idx3d_Vector.sub(path[i],path[i-1]);
                else  forward=idx3d_Vector.sub(path[1],path[0]);
            }
            
            forward.normalize();
            up=new idx3d_Vector(0f,0f,1f);
            right=idx3d_Vector.getNormal(forward,up);
            up=idx3d_Vector.getNormal(forward,right);
            frenetmatrix=new idx3d_Matrix(right,up,forward);
            frenetmatrix.shift(path[i].x,path[i].y,path[i].z);
            
            // Add nodes
            
            relx=(float)i/(float)(segments-1);
            for (int k=0; k<steps; k++) {
                rely=(float)k/(float)steps;
                tempvertex=new idx3d_Vertex(circle[k].transform(frenetmatrix));
                // XXX - Texture mapping is broken
                //        tempvertex.u=relx; tempvertex.v=rely;
                newObject.addVertex(tempvertex);
            }
        }
        
        for (int i=0;i<segments-1;i++) {
            for (int k=0; k<steps-1; k++) {
                a=i*steps+k; b=a+1; c=a+steps; d=b+steps;
                newObject.addTriangle(a,c,b);
                newObject.addTriangle(b,c,d);
            }
            a=(i+1)*steps-1; b=a+1-steps; c=a+steps; d=b+steps;
            newObject.addTriangle(a,c,b);
            newObject.addTriangle(b,c,d);
        }
        
        return newObject;
    }
}