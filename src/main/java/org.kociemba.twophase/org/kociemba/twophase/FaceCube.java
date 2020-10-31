package org.kociemba.twophase;

import static org.kociemba.twophase.Color.B;
import static org.kociemba.twophase.Color.D;
import static org.kociemba.twophase.Color.F;
import static org.kociemba.twophase.Color.L;
import static org.kociemba.twophase.Color.R;
import static org.kociemba.twophase.Color.U;
import static org.kociemba.twophase.Corner.URF;
import static org.kociemba.twophase.Edge.UR;
import static org.kociemba.twophase.Facelet.B1;
import static org.kociemba.twophase.Facelet.B2;
import static org.kociemba.twophase.Facelet.B3;
import static org.kociemba.twophase.Facelet.B4;
import static org.kociemba.twophase.Facelet.B6;
import static org.kociemba.twophase.Facelet.B7;
import static org.kociemba.twophase.Facelet.B8;
import static org.kociemba.twophase.Facelet.B9;
import static org.kociemba.twophase.Facelet.D1;
import static org.kociemba.twophase.Facelet.D2;
import static org.kociemba.twophase.Facelet.D3;
import static org.kociemba.twophase.Facelet.D4;
import static org.kociemba.twophase.Facelet.D6;
import static org.kociemba.twophase.Facelet.D7;
import static org.kociemba.twophase.Facelet.D8;
import static org.kociemba.twophase.Facelet.D9;
import static org.kociemba.twophase.Facelet.F1;
import static org.kociemba.twophase.Facelet.F2;
import static org.kociemba.twophase.Facelet.F3;
import static org.kociemba.twophase.Facelet.F4;
import static org.kociemba.twophase.Facelet.F6;
import static org.kociemba.twophase.Facelet.F7;
import static org.kociemba.twophase.Facelet.F8;
import static org.kociemba.twophase.Facelet.F9;
import static org.kociemba.twophase.Facelet.L1;
import static org.kociemba.twophase.Facelet.L2;
import static org.kociemba.twophase.Facelet.L3;
import static org.kociemba.twophase.Facelet.L4;
import static org.kociemba.twophase.Facelet.L6;
import static org.kociemba.twophase.Facelet.L7;
import static org.kociemba.twophase.Facelet.L8;
import static org.kociemba.twophase.Facelet.L9;
import static org.kociemba.twophase.Facelet.R1;
import static org.kociemba.twophase.Facelet.R2;
import static org.kociemba.twophase.Facelet.R3;
import static org.kociemba.twophase.Facelet.R4;
import static org.kociemba.twophase.Facelet.R6;
import static org.kociemba.twophase.Facelet.R7;
import static org.kociemba.twophase.Facelet.R8;
import static org.kociemba.twophase.Facelet.R9;
import static org.kociemba.twophase.Facelet.U1;
import static org.kociemba.twophase.Facelet.U2;
import static org.kociemba.twophase.Facelet.U3;
import static org.kociemba.twophase.Facelet.U4;
import static org.kociemba.twophase.Facelet.U6;
import static org.kociemba.twophase.Facelet.U7;
import static org.kociemba.twophase.Facelet.U8;
import static org.kociemba.twophase.Facelet.U9;

/**
 * Cube on the facelet level. 
 * This is a string with 9 x 6 times a sequence made of the characters "URFDLB".
 */
public class FaceCube {

    public Color[] f = new Color[54];

    /** Map the corner positions to facelet positions. 
     * cornerFacelet[URF.ordinal()][0] e.g. gives the position of the
     * facelet in the URF corner position, which defines the orientation.<br>
     * cornerFacelet[URF.ordinal()][1] and cornerFacelet[URF.ordinal()][2] 
     * give the position of the other two facelets
     * of the URF corner (clockwise).
     */
    final static Facelet[][] cornerFacelet = {//
        {U9, R1, F3}, {U7, F1, L3}, {U1, L1, B3}, {U3, B1, R3},
        {D3, F9, R7}, {D1, L9, F7}, {D7, B9, L7}, {D9, R9, B7}};

    /** Map the edge positions to facelet positions.
     * edgeFacelet[UR.ordinal()][0] e.g. gives the position of the facelet in
     * the UR edge position, which defines the orientation.<br>
     * edgeFacelet[UR.ordinal()][1] gives the position of the other facelet
     */
    final static Facelet[][] edgeFacelet = {//
        {U6, R2}, {U8, F2}, {U4, L2}, {U2, B2}, {D6, R8}, {D2, F8},
        {D4, L8}, {D8, B8}, {F6, R4}, {F4, L6}, {B6, L4}, {B4, R6}};

    /** Map the corner positions to facelet colors. */
    final static Color[][] cornerColor = {//
        {U, R, F}, {U, F, L}, {U, L, B}, {U, B, R}, {D, F, R}, {D, L, F},
        {D, B, L}, {D, R, B}};

    /** Map the edge positions to facelet colors. */
    final static Color[][] edgeColor = {//
        {U, R}, {U, F}, {U, L}, {U, B}, {D, R}, {D, F}, {D, L}, {D, B},
        {F, R}, {F, L}, {B, L}, {B, R}};

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    FaceCube() {
        String s = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";
        for (int i = 0; i < 54; i++) {
            f[i] = Color.valueOf(s.substring(i, i + 1));
        }

    }

    /** Construct a facelet cube from a string. */
    public FaceCube(String cubeString) {
        for (int i = 0; i < cubeString.length(); i++) {
            f[i] = Color.valueOf(cubeString.substring(i, i + 1));
        }
    }

    /** Gives string representation of a facelet cube. */
    String to_String() {
        String s = "";
        for (int i = 0; i < 54; i++) {
            s += f[i].toString();
        }
        return s;
    }

    /** Gives CubieCube representation of a faceletcube. */
    public CubieCube toCubieCube() {
        byte ori;
        CubieCube ccRet = new CubieCube();
        for (int i = 0; i < 8; i++) {
            ccRet.cp[i] = URF;// invalidate corners
        }
        for (int i = 0; i < 12; i++) {
            ccRet.ep[i] = UR;// and edges
        }
        Color col1, col2;
        for (Corner i : Corner.values()) {
            // get the colors of the cubie at corner i, starting with U/D
            for (ori = 0; ori < 3; ori++) {
                if (f[cornerFacelet[i.ordinal()][ori].ordinal()] == U || f[cornerFacelet[i.ordinal()][ori].ordinal()] == D) {
                    break;
                }
            }
            col1 = f[cornerFacelet[i.ordinal()][(ori + 1) % 3].ordinal()];
            col2 = f[cornerFacelet[i.ordinal()][(ori + 2) % 3].ordinal()];

            for (Corner j : Corner.values()) {
                if (col1 == cornerColor[j.ordinal()][1] && col2 == cornerColor[j.ordinal()][2]) {
                    // in cornerposition i we have cornercubie j
                    ccRet.cp[i.ordinal()] = j;
                    ccRet.co[i.ordinal()] = (byte) (ori % 3);
                    break;
                }
            }
        }
        for (Edge i : Edge.values()) {
            for (Edge j : Edge.values()) {
                if (f[edgeFacelet[i.ordinal()][0].ordinal()] == edgeColor[j.ordinal()][0]
                        && f[edgeFacelet[i.ordinal()][1].ordinal()] == edgeColor[j.ordinal()][1]) {
                    ccRet.ep[i.ordinal()] = j;
                    ccRet.eo[i.ordinal()] = 0;
                    break;
                }
                if (f[edgeFacelet[i.ordinal()][0].ordinal()] == edgeColor[j.ordinal()][1]
                        && f[edgeFacelet[i.ordinal()][1].ordinal()] == edgeColor[j.ordinal()][0]) {
                    ccRet.ep[i.ordinal()] = j;
                    ccRet.eo[i.ordinal()] = 1;
                    break;
                }
            }
        }
        return ccRet;
    }
;
}
