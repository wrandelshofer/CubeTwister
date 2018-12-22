/* @(#)KociembaENGParser.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.impexp.cubeexplorer;

import ch.randelshofer.rubik.*;
import ch.randelshofer.rubik.parser.*;
import java.util.*;

/**
 * KociembaENGParser.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class KociembaENGParser extends ScriptParser {

    /** Creates new BandelowENGParser */
    public KociembaENGParser() {
        super(null);
        /*
        super(
            getTokens(), new HashMap(), 
            ScriptParser.POSITION_UNSUPPORTED, ScriptParser.POSITION_UNSUPPORTED, ScriptParser.POSITION_UNSUPPORTED, 
            ScriptParser.POSITION_UNSUPPORTED, ScriptParser.POSITION_UNSUPPORTED, 
            ScriptParser.POSITION_UNSUPPORTED, false, ScriptParser.POSITION_UNSUPPORTED
        );*/
    }

    private static String[] getTokens() {
        /*
        String[] t = new String[ScriptParser.TOKEN_COUNT];
        int i = 0;
        StringTokenizer st = new StringTokenizer(COMPRESSED_TOKENS, ";", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (";".equals(token)) i++;
            else t[i] = token;
        }
        return t;
         */
        return null;
    }
    
    private final static String COMPRESSED_TOKENS =
    // Basic Twists 90° clockwise and counter-clockwise 12
    "R;U;F;L;D;B;"+
    "R';U';F';L';D';B';"+
    
    // Basic Twists 180° clockwise and counter-clockwise 12
    "R2;U2;F2;L2;D2;B2;"+
    ";;;;;;"+
    
    // Midlayer Twists 90° clockwise and counter-clockwise 6
    ";;;;;;"+
    
    // Midlayer Twists 180° clockwise and counter-clockwise 6
    ";;;;;;"+
    
    // Two-Layer Twists 90° clockwise and counter-clockwise 6
    ";;;"+
    ";;;"+
    
    // Two-layer Twists 180° clockwise and counter-clockwise 6
    ";;;"+
    ";;;"+
    
    // Slice Twists 90° clockwise and counter-clockwise 6
    ";;;"+
    ";;;"+
    
    // Slice Twists 180° clockwise and counter-clockwise 6
    ";;;"+
    ";;;"+
    
    // Cube rotation 90° clockwise and counter-clockwise 6
    ";;;"+
    ";;;"+
    
    // Cube rotation 180° clockwise and counter-clockwise 6
    ";;;"+
    ";;;"+
    
    // NOP 1
    ";"+
    
    // Permutation Faces 6
    ";;;;;;"+
    
    // Permutation Rotations 3
    ";;;"+
    
    // Special tokens 23
    // Statement Delimiter
    ";"+
    // Invertor; Reflector;
    ";;"+
    // Sequence Begin; Sequence End
    ";;"+
    // Permutation Delimiter; Permutation Begin; Permutation End
    ";;;"+
    // Repetitor Begin; Repetitor End
    ";;"+
    // Commutator Begin; Commutator End; Commutator Delimiter
    ";;;"+
    // Conjugator Begin; Conjugator End; Conjugator Delimiter
    ";;;"+
    // Rotator Begin; Rotator End; Rotator Delimiter
    ";;;"+
    // SlashStar Comment Begin; SlashStar Comment End; SlashSlash Comment
    ";;;"
    ;
}
