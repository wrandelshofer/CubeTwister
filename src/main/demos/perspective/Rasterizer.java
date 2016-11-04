/*
 * @(#)UndoableObjectEdit.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
/*
 * @(#)Rasterizer.java  1.0  2010-06-05
 * 
 * Copyright (c) 2010 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package demos.perspective;

/**
 * Rasterizer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Rasterizer {

    float dizdx, dtxizdx, dtyizdx, dizdy, dtxizdy, dtyizdy;
    float xa, xb, iza, uiza, viza;
    float dxdya, dxdyb, dizdya, duizdya, dvizdya;
    int[] texture;
    int[] screen;

    void render(Triangle tri) {
        float x1, y1, x2, y2, x3, y3;
        float iz1, txiz1, tyiz1, iz2, txiz2, tyiz2, iz3, txiz3, tyiz3;
        float dxdy1, dxdy2, dxdy3;
        float tempf;
        float denom;
        float dy;
        int y1i, y2i, y3i;
        boolean side;

        // Shift XY coordinate system (+0.5, +0.5) to match the subpixeling
        //  technique

        x1 = tri.x1 + 0.5f;
        y1 = tri.y1 + 0.5f;
        x2 = tri.x2 + 0.5f;
        y2 = tri.y2 + 0.5f;
        x3 = tri.x3 + 0.5f;
        y3 = tri.y3 + 0.5f;

        // Calculate alternative 1/Z, U/Z and V/Z values which will be
        //  interpolated

        iz1 = 1 / tri.z1;
        iz2 = 1 / tri.z2;
        iz3 = 1 / tri.z3;
        txiz1 = tri.tx1 * iz1;
        tyiz1 = tri.ty1 * iz1;
        txiz2 = tri.tx2 * iz2;
        tyiz2 = tri.ty2 * iz2;
        txiz3 = tri.tx3 * iz3;
        tyiz3 = tri.ty3 * iz3;

        texture = tri.texture;

        // Sort the vertices in ascending Y order

//#define swapfloat(x, y) tempf = x; x = y; y = tempf;
        if (y1 > y2) {
            tempf = x1;
            x1 = x2;
            x2 = tempf;
            tempf = y1;
            y1 = y2;
            y2 = tempf;
            tempf = iz1;
            iz1 = iz2;
            iz2 = tempf;
            tempf = txiz1;
            txiz1 = txiz2;
            txiz2 = tempf;
            tempf = tyiz1;
            tyiz1 = tyiz2;
            tyiz2 = tempf;
        }
        if (y1 > y3) {
            tempf = x1;
            x1 = x3;
            x3 = tempf;
            tempf = y1;
            y1 = y3;
            y3 = tempf;
            tempf = iz1;
            iz1 = iz3;
            iz3 = tempf;
            tempf = txiz1;
            txiz1 = txiz3;
            txiz3 = tempf;
            tempf = tyiz1;
            tyiz1 = tyiz3;
            tyiz3 = tempf;
        }
        if (y2 > y3) {
            tempf = x2;
            x2 = x3;
            x3 = tempf;
            tempf = y2;
            y2 = y3;
            y3 = tempf;
            tempf = iz2;
            iz2 = iz3;
            iz3 = tempf;
            tempf = txiz2;
            txiz2 = txiz3;
            txiz3 = tempf;
            tempf = tyiz2;
            tyiz2 = tyiz3;
            tyiz3 = tempf;
        }
//#undef swapfloat

        y1i = (int) y1;
        y2i = (int) y2;
        y3i = (int) y3;

        // Skip poly if it's too thin to cover any pixels at all

        if ((y1i == y2i && y1i == y3i)
                || ((int) x1 == (int) x2 && (int) x1 == (int) x3)) {
            return;
        }

        // Calculate horizontal and vertical increments for UV axes (these
        //  calcs are certainly not optimal, although they're stable
        //  (handles any dy being 0)

        denom = ((x3 - x1) * (y2 - y1) - (x2 - x1) * (y3 - y1));

        if (denom == 0) // Skip poly if it's an infinitely thin line
        {
            return;
        }

        denom = 1 / denom;	// Reciprocal for speeding up
        dizdx = ((iz3 - iz1) * (y2 - y1) - (iz2 - iz1) * (y3 - y1)) * denom;
        dtxizdx = ((txiz3 - txiz1) * (y2 - y1) - (txiz2 - txiz1) * (y3 - y1)) * denom;
        dtyizdx = ((tyiz3 - tyiz1) * (y2 - y1) - (tyiz2 - tyiz1) * (y3 - y1)) * denom;
        dizdy = ((iz2 - iz1) * (x3 - x1) - (iz3 - iz1) * (x2 - x1)) * denom;
        dtxizdy = ((txiz2 - txiz1) * (x3 - x1) - (txiz3 - txiz1) * (x2 - x1)) * denom;
        dtyizdy = ((tyiz2 - tyiz1) * (x3 - x1) - (tyiz3 - tyiz1) * (x2 - x1)) * denom;

        // Calculate X-slopes along the edges
        dxdy1 = dxdy2 = dxdy3 = 0;
        if (y2 > y1) {
            dxdy1 = (x2 - x1) / (y2 - y1);
        }
        if (y3 > y1) {
            dxdy2 = (x3 - x1) / (y3 - y1);
        }
        if (y3 > y2) {
            dxdy3 = (x3 - x2) / (y3 - y2);
        }

        // Determine which side of the poly the longer edge is on

        side = dxdy2 > dxdy1;

        if (y1 == y2) {
            side = x1 > x2;
        }
        if (y2 == y3) {
            side = x3 > x2;
        }

        if (!side) // Longer edge is on the left side
        {
            // Calculate slopes along left edge

            dxdya = dxdy2;
            dizdya = dxdy2 * dizdx + dizdy;
            duizdya = dxdy2 * dtxizdx + dtxizdy;
            dvizdya = dxdy2 * dtyizdx + dtyizdy;

            // Perform subpixel pre-stepping along left edge

            dy = 1 - (y1 - y1i);
            xa = x1 + dy * dxdya;
            iza = iz1 + dy * dizdya;
            uiza = txiz1 + dy * duizdya;
            viza = tyiz1 + dy * dvizdya;

            if (y1i < y2i) // Draw upper segment if possibly visible
            {
                // Set right edge X-slope and perform subpixel pre-
                //  stepping

                xb = x1 + dy * dxdy1;
                dxdyb = dxdy1;

                renderSegment(y1i, y2i);
            }
            if (y2i < y3i) // Draw lower segment if possibly visible
            {
                // Set right edge X-slope and perform subpixel pre-
                //  stepping

                xb = x2 + (1 - (y2 - y2i)) * dxdy3;
                dxdyb = dxdy3;

                renderSegment(y2i, y3i);
            }
        } else // Longer edge is on the right side
        {
            // Set right edge X-slope and perform subpixel pre-stepping

            dxdyb = dxdy2;
            dy = 1 - (y1 - y1i);
            xb = x1 + dy * dxdyb;

            if (y1i < y2i) // Draw upper segment if possibly visible
            {
                // Set slopes along left edge and perform subpixel
                //  pre-stepping

                dxdya = dxdy1;
                dizdya = dxdy1 * dizdx + dizdy;
                duizdya = dxdy1 * dtxizdx + dtxizdy;
                dvizdya = dxdy1 * dtyizdx + dtyizdy;
                xa = x1 + dy * dxdya;
                iza = iz1 + dy * dizdya;
                uiza = txiz1 + dy * duizdya;
                viza = tyiz1 + dy * dvizdya;

                renderSegment(y1i, y2i);
            }
            if (y2i < y3i) // Draw lower segment if possibly visible
            {
                // Set slopes along left edge and perform subpixel
                //  pre-stepping

                dxdya = dxdy3;
                dizdya = dxdy3 * dizdx + dizdy;
                duizdya = dxdy3 * dtxizdx + dtxizdy;
                dvizdya = dxdy3 * dtyizdx + dtyizdy;
                dy = 1 - (y2 - y2i);
                xa = x2 + dy * dxdya;
                iza = iz2 + dy * dizdya;
                uiza = txiz2 + dy * duizdya;
                viza = tyiz2 + dy * dvizdya;

                renderSegment(y2i, y3i);
            }
        }
    }

    void renderSegment(int y1, int y2) {
        if (screen==null) return;
        
        int[] scr;
        int x1, x2;
        float iiz, u, v, dx;
        float iz, uiz, viz;

        while (y1 < y2) // Loop through all lines in the segment
        {
            x1 = (int) xa;
            x2 = (int) xb;

            // Perform subtexel pre-stepping on 1/Z, U/Z and V/Z

            dx = 1 - (xa - x1);
            iz = iza + dx * dizdx;
            uiz = uiza + dx * dtxizdx;
            viz = viza + dx * dtyizdx;

            //scr = screen[y1 * 320 + x1];

            while (x1++ < x2) // Draw horizontal line
            {
                // Calculate U and V from 1/Z, U/Z and V/Z

                iiz = 1 / iz;
                u = uiz * iiz;
                v = viz * iiz;

                // Copy pixel from texture to screen

                screen[y1 * 320 + x1] = texture[((((int) v) & 0xff) << 8) + (((int) u) & 0xff)];

                // Step 1/Z, U/Z and V/Z horizontally

                iz += dizdx;
                uiz += dtxizdx;
                viz += dtyizdx;
            }

            // Step along both edges

            xa += dxdya;
            xb += dxdyb;
            iza += dizdya;
            uiza += duizdya;
            viza += dvizdya;

            y1++;
        }

    }
}
