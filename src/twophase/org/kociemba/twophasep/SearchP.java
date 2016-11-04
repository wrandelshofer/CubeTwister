package org.kociemba.twophasep;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

import org.kociemba.twophase.Search;

/**
 * Class SearchP implements the Two-Phase-Algorithm for a picture cube.
 * In a  picture cube, the orientation of the side part is relevant.
 * <p>
 * The algorithm is similar to the one in {@link Search}. However we only 
 * transition from Phase 1 to Phase 2 if the side parts at R,F,L,B are oriented
 * up or down. Because in Phase2 only R2, F2, L2 and B2 moves are performed.
 * We only finish Phase 2 if the side parts are properly oriented.
 * 
 */
public class SearchP extends Search {
}
