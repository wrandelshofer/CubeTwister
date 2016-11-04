/*
 * @(#)Polygon3D.java  0.2  1999-01-01
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.geom3d;

import java.awt.Polygon;

/**
A polygon located in three dimensional (x, y, z) coordinate space.
Note: The edges do not have to be on the same plane.

@author Werner Randelshofer
@version
  0.2  1999-1-1  Package renamed.
<br>0.1  1998-12-15  Released.
<br>0.0  1998-02-15  Created.
*/
public class Polygon3D
  {
    /**
     * The total number of points.
     */
    public int npoints = 0;

    /**
     * The array of x coordinates.
     */
    public double xpoints[];

    /**
     * The array of y coordinates.
     */
    public double ypoints[];

    /**
     * The array of z coordinates.
     */
    public double zpoints[];

  /**
   * Creates an empty polygon.
   */
  public Polygon3D()
    {
    setCapacity(4);
    }

  /**
   * Creates an empty polygon with the indicated capacity.
   */
  public Polygon3D(int capacity)
    {
    setCapacity(capacity);
    }

  /**
   * Sets the capacity of the Polygon but drops
   * all data!
   */
  public void setCapacity(int capacity)
    {
    xpoints = new double[capacity];
    ypoints = new double[capacity];
    zpoints = new double[capacity];
    npoints = 0;
    }
    /**
     * Constructs and initializes a Polygon from the specified parameters.
     * @param xpoints the array of x coordinates
     * @param ypoints the array of y coordinates
     * @param zpoints the array of z coordinates
     * @param npoints the total number of points in the Polygon
     */
    public Polygon3D(double xpoints[], double ypoints[], double zpoints[], int npoints)
      {
    this.npoints = npoints;
    this.xpoints = new double[npoints];
    this.ypoints = new double[npoints];
    this.zpoints = new double[npoints];
    System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
    System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);
    System.arraycopy(zpoints, 0, this.zpoints, 0, npoints);
    }

    /**
     * Constructs and initializes a Polygon from the specified parameters.
     * @param points the array of points coordinates; the second index specifies
     *    the axis where [0]=x; [1]=y; [2]=z;
     * @param position the start index of the points in the array
     * @param npoints the total number of points in the Polygon
     */
    public Polygon3D(short points[][], int position, int npoints)
      {
    this.npoints = npoints;
    this.xpoints = new double[npoints];
    this.ypoints = new double[npoints];
    this.zpoints = new double[npoints];

    for (int i = position+npoints-1; i < position; i--)
      {
      this.xpoints[i] = points[i][0];
      this.ypoints[i] = points[i][1];
      this.zpoints[i] = points[i][2];
      }
    }


  /**
     * Appends a point to a polygon.  If inside(x, y) or another
     * operation that calculates the bounding box has already been
     * performed, this method updates the bounds accordingly.

     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @param z the z coordinate of the point
     */
    public void addPoint(double x, double y, double z)
      {
    if (npoints == xpoints.length)
      {
        double tmp[];

        tmp = new double[npoints * 2];
        System.arraycopy(xpoints, 0, tmp, 0, npoints);
        xpoints = tmp;

        tmp = new double[npoints * 2];
        System.arraycopy(ypoints, 0, tmp, 0, npoints);
        ypoints = tmp;

        tmp = new double[npoints * 2];
        System.arraycopy(zpoints, 0, tmp, 0, npoints);
        zpoints = tmp;
      }
    xpoints[npoints] = x;
    ypoints[npoints] = y;
    zpoints[npoints] = y;
    npoints++;
    }
  }
