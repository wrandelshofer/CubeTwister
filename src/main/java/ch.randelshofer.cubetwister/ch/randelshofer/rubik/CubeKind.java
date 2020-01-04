/* @(#)CubeKind.java
 * Copyright (c) 2006 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Typesafe enum of cube kinds used by CubeModel.
 *
 * @author Werner Randelshofer
 */
public enum CubeKind {

    /**
     * CubeKinds
     */
    POCKET("2x2 Pocket Cube", 2, new String[]{"PocketCube", "Pocket", "2x2 Cube"}),
    RUBIK("3x3 Rubik's Cube", 3, new String[]{"RubiksCube", "Rubik", "3x3 Cube", "Cube"}),
    REVENGE("4x4 Revenge Cube", 4, new String[]{"RevengeCube", "Revenge", "4x4 Cube"}),
    PROFESSOR("5x5 Professor Cube", 5, new String[]{"ProfessorCube", "Professor", "5x5 Cube"}),
    VCUBE_6("6x6 V-Cube", 6, new String[]{"V-Cube 6", "VCube6", "6x6 V-Cube"}),
    VCUBE_7("7x7 V-Cube", 7, new String[]{"V-Cube 7", "VCube7", "7x7 V-Cube"}),
    BARREL("3x3 Rubik's Barrel", 3, new String[]{"RubiksBarrel", "Barrel", "3x3 Barrel"}),
    DIAMOND("3x3 Rubik's Diamond", 3, new String[]{"RubiksDiamond", "Diamond", "3x3 Diamond"}),
    CUBOCTAHEDRON("3x3 Rubik's Cuboctahedron", 3, new String[]{"RubiksCuboctahedron", "Cuboctahedron", "Octahedron", "3x3 Octahedron"}),
    CUBE_6("6x6 Cube", 6, new String[]{"Cube 6", "Cube6", "6x6 Cube"}),
    CUBE_7("7x7 Cube", 7, new String[]{"Cube 7", "Cube6", "7x7 Cube"});

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
    @Nonnull
    private final String[] alternativeNames;
    /**
     * The number of layers.
     */
    private final int layerCount;
    /**
     * Kind map. This is used for mappying an alternative name to a CubeKind.
     */
    @Nullable
    private static HashMap<String, CubeKind> kindMap = null;

    private CubeKind(String name, int layerCount, @Nonnull String[] alternativeNames) {
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
    public boolean isNameOfKind(@Nonnull String name) {
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

    @Nonnull
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
