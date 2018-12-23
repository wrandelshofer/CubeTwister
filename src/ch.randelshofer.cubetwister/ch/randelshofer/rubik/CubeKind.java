/* @(#)CubeKind.java
 * Copyright (c) 2006 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Typesafe enum of cube kinds used by CubeModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>2.0 2008-12-20 Moved from ch.randelshofer.cubetwister into
 * ch.randelshofer.rubik package. Added method isNameOfKind.
 * <br>1.1 2008-01-03 Added method getLayerCount.
 * <br>1.0 January 9, 2006 Created.
 */
public enum CubeKind {

    /**
     * CubeKinds
     */
    POCKET("2x2 Pocket Cube", 2, new String[]{"PocketCube", "PocketCube", "Pocket", "2x2 Cube"}),
    RUBIK("3x3 Rubik's Cube", 3, new String[]{"RubiksCube", "RubiksCube", "Rubik", "3x3 Cube", "Cube"}),
    REVENGE("4x4 Revenge Cube", 4, new String[]{"RevengeCube", "RevengeCube", "Revenge", "4x4 Cube"}),
    PROFESSOR("5x5 Professor Cube", 5, new String[]{"ProfessorCube", "ProfessorCube", "Professor", "5x5 Cube"}),
    VCUBE_6("6x6 V-Cube", 6, new String[]{"V-Cube 6", "VCube6", "VCube6", "6x6 V-Cube"}),
    VCUBE_7("7x7 V-Cube", 7, new String[]{"V-Cube 7", "VCube7", "VCube7", "7x7 V-Cube"}),
    BARREL("3x3 Rubik's Barrel", 3, new String[]{"RubiksBarrel", "RubiksBarrel", "Barrel", "3x3 Barrel"}),
    DIAMOND("3x3 Rubik's Diamond", 3, new String[]{"RubiksDiamond", "RubiksDiamond", "Diamond", "3x3 Diamond"}),
    CUBOCTAHEDRON("3x3 Rubik's Cuboctahedron", 3, new String[]{"RubiksCuboctahedron", "RubiksCuboctahedron", "Cuboctahedron", "Octahedron", "3x3 Octahedron"}),
    CUBE_6("6x6 Cube", 6, new String[]{"Cube 6", "Cube6", "Cube6", "6x6 Cube"}),
    CUBE_7("7x7 Cube", 7, new String[]{"Cube 7", "Cube6", "Cube7", "7x7 Cube"});

    /**
     * User name of the type. This is a Locale dependent text.
     */
    private final String name;
    /**
     * ID name of the type. This is a Locale independent text.
     */
    private final String id;
    /**
     * Alternative names. This is used for file reading/writing. The names
     * listed here must be Locale independent, and they must be unique for this
     * cube type. The first name in this array is used for writing.
     */
    private final String[] alternativeNames;
    /**
     * The number of layers.
     */
    private final int layerCount;
    /**
     * Kind map. This is used for mappying an alternative name to a CubeKind.
     */
    private static HashMap<String, CubeKind> kindMap = null;

    private CubeKind(String name, int layerCount, String[] alternativeNames) {
        this.id = alternativeNames[0];
        this.name = name;
        this.alternativeNames = alternativeNames;
        this.layerCount = layerCount;
    }

    public String getAlternativeName(int index) {
        return alternativeNames[index];
    }

    /**
     * Returns true if the provided name matches with the name or one of the
     * alternative names of this kind.
     *
     * @param name A name.
     * @return True on match.
     */
    public boolean isNameOfKind(String name) {
        if (this.name.equals(name)) {
            return true;
        }
        for (int i = 0; i < alternativeNames.length; i++) {
            if (name.equals(alternativeNames[i])) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, CubeKind> getKindMap() {
        if (kindMap == null) {
            kindMap = new HashMap<String, CubeKind>();
            for (CubeKind ck : values()) {
                for (String alternativeName : ck.alternativeNames) {
                    kindMap.put(alternativeName, ck);
                }
            }
        }
        return Collections.unmodifiableMap(kindMap);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getLayerCount() {
        return layerCount;
    }
}
