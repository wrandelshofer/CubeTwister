/**
 * @(#)TripleSearchSolver.java  1.0  Mar 3, 2008
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik.solver;

/**
 * An implementation of Herbert Kociemba's Triple Search solver.
 * <p>
 * Description of the algorithm (by Tom Rokicki):
 * <p>
 * Kociemba's original two phase algorithm works great, but there are a lot of
 * positions that give it some difficulty. He now uses something called 
 * "Triple Search" in his cube explorer, and this is very effective in making it
 * work better on many cubes.
 * <p>
 * The way this works is by solving essentially *three* positions at once,
 * the input position, and two other positions where the L/R and F/B face
 * are mapped, respectively, to U/D.  If the original position gives some 
 * difficulty, chances are the other two will not.  So instead of
 * <pre>
 * for (increasing allowed_phase1_depth) {
 *    trytosolve(allowed_phase1_depth)
 * }
 * </pre>
 * we do
 * <pre>
 * for (increasing allowed_phase1_depth) {
 *    for (three positions of input) {
 *      trytosolve(position, allowed_phase1_depth)
 *    }
 * }
 * </pre>
 * This can give a tremendous speedup; on average, for finding a
 * distance 20 position for 30,000 random positions, I find it
 * is about six times faster.
 * <p>
 * I have made an additional, further improvement.  Instead of
 * just considering three positions, consider 6, where the other
 * three are *inverses* of the first three.  If you do that, you gain
 * another factor of two speed.
 * <p>
 * (You can potentially exploit this to easily use multiple cores
 * in modern processors too, so each core gets a position and
 * depth to work on.  But this is somewhat secondary.)
 * 
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TripleSearchSolver {
}
