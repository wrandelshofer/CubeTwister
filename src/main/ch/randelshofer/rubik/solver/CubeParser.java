/*
 * @(#)CubeParser.java  0.0  2000-07-02
 * Copyright (c) 2000 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 *
 * This software has been derived from the 'Kociemba
 * Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 * KCube is a C++ implementation of the cube solver
 * algorithm from Herbert Kociemba.
 */
package ch.randelshofer.rubik.solver;

import java.util.StringTokenizer;

/**
 * Parse the cube state passed in on the command line
 * checking for gross syntax errors.  For example, all
 * 9 facelet markings for each of the six sides must
 * be specified, and markings must be a printable ASCII
 * character.  If the parse was successful a "FaceletCube"
 * is initialized.  The FaceletCube represents the cube
 * by the markings of the 54 individual facelets.  The
 * FaceletCube can then be asked to validate the cube
 * to determine if it is in a legal, and thus solvable,
 * configuration.
 *
 * This class has been derived from cubepars.cpp and cubepars.h
 * from the 'Kociemba Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 *
 * @author Werner Randelshofer
 * @version 0.0 2000-07-02
 */
public class CubeParser extends Object {
    // Parser return codes
    public final static int VALID = 0;
    public final static int INVALID_FACE = 1;
    public final static int INVALID_MARKER = 2;
    public final static int INCOMPLETE_INPUT = 3;
    public final static int SYNTAX_ERROR = 4;
    public final static int NUMBER_OF_ERRORS = 5;

    private final static String[] ERROR_TEXT = {
        "",
        "Invalid face specifier",
        "Invalid marker",
        "Incomplete input",
        "Syntax error"
    };

    public CubeParser() {
    }

    /** Parse the input and initialize a FaceletCube. */
    public int parseInput(String input, FaceletCube faceletCube) {
        int face;
        int[] faces = new int[6];
        int i;
        int status;

        StringTokenizer scanner = new StringTokenizer(input);


        // All six face specifiers must be present
        if (scanner.countTokens() != 6) {
            return SYNTAX_ERROR;
        }

        // Reset the face count for all faces
        for (i = 0; i < 6; i++) {
            faces[i] = 0;
        }

        // Loop through each face specifier
        char[] markings = new char[9];
        for (i = 0; i < 6; i++) {
            String token = scanner.nextToken();

            // Parse each face
            if ((status = parseFace(faceletCube, token)) > VALID) {
                return status;
            }
            face = -status;

            // Initialize this face in the FaceletCube
            token.getChars(2,11,markings,0);
            faceletCube.setFaceMarkings(face, markings);

            // Count this face
            faces[face]++;
        }

        // Each face specifier must be found exactly once
        for (i = 0; i < 6; i++) {
            if (faces[i] != 1) {
                return INCOMPLETE_INPUT;
            }
        }

        return VALID;
    }

    /** Return the text associated with an error return code. */
    public String getErrorText(int error) {
        if (error >= ERROR_TEXT.length) {
            error = 0;
        }
        return ERROR_TEXT[error];

    }

    /** Return the text associated with an error return code. */
    private int parseFace(FaceletCube faceletCube, String faceString) {
        int face;
        int facelet;

        // Check specifier length f:mmmmmmmmm
        if (faceString.length() != 1+1+9) {
            return SYNTAX_ERROR;
        }

        // Validate face name (f)
        if ((face = faceletCube.faceNameToOffset(faceString.charAt(0))) < 0) {
            return INVALID_FACE;
        }

        // Parse the colon
        if (faceString.charAt(1) != ':') {
            return SYNTAX_ERROR;
        }

        // Check each facelet
        for (facelet = 0; facelet < 9; facelet++) {
            // Only printable characters are allowed
            if (faceString.charAt(2+facelet) <= ' ' ||
                    faceString.charAt(2+facelet) > '~') {
                return SYNTAX_ERROR;
            }
       }
       return -face;
    }
}


