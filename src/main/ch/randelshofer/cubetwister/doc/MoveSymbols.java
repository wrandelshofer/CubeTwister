/*
 * @(#)MoveSymbols.java  1.1  2011-01-22
 *
 * Copyright (c) 2006-2011 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.rubik.parser.Move;
import java.util.*;
/**
 * MoveSymbols.
 * 
 * 
 * @author Werner Randelshofer.
 * @version 1.1 2011-01-22 Use generics.
 * <br>1.0.1 2008-02-23 Added missing "2i" suffix.
 * <br>1.0 May 1, 2006 Created.
 */
public class MoveSymbols {
    private static HashMap<String,Move> tvs2, tvs3, tvs4, tvs5;
    
    public static HashMap<String,Move> getMoveValueSet(int layerCount) {
        switch (layerCount) {
            case 2 :
                if (tvs2 == null) {
                    tvs2 = createTwistValueSet2();
                }
                return tvs2;
                //break;
            case 3 :
                if (tvs3 == null) {
                    tvs3 = createTwistValueSet3();
                }
                return tvs3;
                //break;
            case 4 :
                if (tvs4 == null) {
                    tvs4 = createTwistValueSet4();
                }
                return tvs4;
                //break;
            case 5 :
                if (tvs5 == null) {
                    tvs5 = createTwistValueSet5();
                }
                return tvs5;
                //break;
        }
        return new HashMap<String,Move>();
    }
    
    private static HashMap<String,Move> createTwistValueSet2() {
        HashMap<String,Move> twistValueSet = new HashMap<String,Move>();
        String[] prefixes = {"", "M", "C", "T", "S"};
        String[] faces = {"R", "U", "F", "L", "D", "B"};
        String[] suffixes = {"", "i", "2", "2i"};
        for (int p=0; p < prefixes.length; p++) {
            for (int f=0; f < faces.length; f++) {
                for (int s=0; s < suffixes.length; s++) {
                    int axis = f % 3;
                    int layerMask = 0;
                    switch (p) {
                        case 0 : layerMask = (f < 3) ? 2 : 1; break;
                        case 1 : layerMask = 0; break;
                        case 2 : layerMask = 3; break;
                        case 3 : layerMask = (f < 3) ? 2 : 1; break;
                        case 4 : layerMask = 3; break;
                    }
                    int angle = 0;
                    switch (s) {
                        case 0 : angle = (f < 3) ? 1 : -1; break;
                        case 1 : angle = (f < 3) ? -1 : 1; break;
                        case 2 : angle = 2; break;
                        case 3 : angle = -2; break;
                    }
                    String token = prefixes[p]+faces[f]+suffixes[s];
                    twistValueSet.put(token, new Move(axis,layerMask,angle));
                }
            }
        }
        return twistValueSet;
    }
    private static HashMap<String,Move> createTwistValueSet3() {
        HashMap<String,Move> twistValueSet = new HashMap<String,Move>();
        String[] prefixes = {"", "M", "C", "T", "S"};
        String[] faces = {"R", "U", "F", "L", "D", "B"};
        String[] suffixes = {"", "i", "2", "2i"};
        for (int p=0; p < prefixes.length; p++) {
            for (int f=0; f < faces.length; f++) {
                for (int s=0; s < suffixes.length; s++) {
                    int axis = f % 3;
                    int layerMask = 0;
                    switch (p) {
                        case 0 : layerMask = (f < 3) ? 4 : 1; break;
                        case 1 : layerMask = 2; break;
                        case 2 : layerMask = 7; break;
                        case 3 : layerMask = (f < 3) ? 6 : 3; break;
                        case 4 : layerMask = 5; break;
                    }
                    int angle = 0;
                    switch (s) {
                        case 0 : angle = (f < 3) ? 1 : -1; break;
                        case 1 : angle = (f < 3) ? -1 : 1; break;
                        case 2 : angle = 2; break;
                        case 3 : angle = -2; break;
                    }
                    String token = prefixes[p]+faces[f]+suffixes[s];
                    twistValueSet.put(token, new Move(axis,layerMask,angle));
                }
            }
        }
        return twistValueSet;
    }
    private static HashMap<String,Move> createTwistValueSet4() {
        HashMap<String,Move> twistValueSet = new HashMap<String,Move>();
        String[] prefixes = {"", "M", "C", "T", "S"};
        String[] faces = {"R", "U", "F", "L", "D", "B"};
        String[] suffixes = {"", "i", "2", "2i"};
        for (int p=0; p < prefixes.length; p++) {
            for (int f=0; f < faces.length; f++) {
                for (int s=0; s < suffixes.length; s++) {
                    int axis = f % 3;
                    int layerMask = 0;
                    switch (p) {
                        case 0 : layerMask = (f < 3) ? 8 : 1; break;
                        case 1 : layerMask = (f < 3) ? 4 : 2; break;
                        case 2 : layerMask = 15; break;
                        case 3 : layerMask = (f < 3) ? 8+4 : 3; break;
                        case 4 : layerMask = 8+1; break;
                    }
                    int angle = 0;
                    switch (s) {
                        case 0 : angle = (f < 3) ? 1 : -1; break;
                        case 1 : angle = (f < 3) ? -1 : 1; break;
                        case 2 : angle = 2; break;
                        case 3 : angle = -2; break;
                    }
                    String token = prefixes[p]+faces[f]+suffixes[s];
                    twistValueSet.put(token, new Move(axis,layerMask,angle));
                }
            }
        }
        return twistValueSet;
    }
    private static HashMap<String,Move> createTwistValueSet5() {
        HashMap<String,Move> twistValueSet = new HashMap<String,Move>();
        String[] prefixes = {"", "M", "C", "T", "S"};
        String[] faces = {"R", "U", "F", "L", "D", "B"};
        String[] suffixes = {"", "i", "2", "2i"};
        for (int p=0; p < prefixes.length; p++) {
            for (int f=0; f < faces.length; f++) {
                for (int s=0; s < suffixes.length; s++) {
                    int axis = f % 3;
                    int layerMask = 0;
                    switch (p) {
                        case 0 : layerMask = (f < 3) ? 16 : 1; break;
                        case 1 : layerMask = 4; break;
                        case 2 : layerMask = 31; break;
                        case 3 : layerMask = (f < 3) ? 8+16 : 3; break;
                        case 4 : layerMask = 17; break;
                    }
                    int angle = 0;
                    switch (s) {
                        case 0 : angle = (f < 3) ? 1 : -1; break;
                        case 1 : angle = (f < 3) ? -1 : 1; break;
                        case 2 : angle = 2; break;
                        case 3 : angle = -2; break;
                    }
                    String token = prefixes[p]+faces[f]+suffixes[s];
                    twistValueSet.put(token, new Move(axis,layerMask,angle));
                }
            }
        }
        return twistValueSet;
    }
}
