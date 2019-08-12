/* @(#)Main.java
 * Copyright (c) 2000 Werner Randelshofer, Switzerland. MIT License.
 *
 * This software has been derived from the 'Kociemba
 * Cube Solver 1.0' (KCube) (c) Greg Schmidt.
 * KCube is a C++ implementation of the cube solver
 * algorithm from Herbert Kociemba.
 */
package ch.randelshofer.rubik.solver;

import ch.randelshofer.gui.ProgressObserver;
import ch.randelshofer.gui.ProgressPrinter;
import ch.randelshofer.gui.ProgressView;
import ch.randelshofer.rubik.notation.DefaultNotation;
import ch.randelshofer.rubik.notation.Notation;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.IOException;

/**
 * Main class of the solver. Use this class to invoke the solver from the
 * command line.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Main extends Object {

    /**
     *
     * Command line format:
     * <p>
     * &gt;java ch.randelshofer.rubik.solver.Main FaceSpecifier1 FaceSpecifier2 ... FaceSpecifier6
     * <p>
     * where each face specifier is of the form f:mmmmmmmmm
     * <p>
     * and f (face) must be one of {U,D,F,B,L,R} and m (marker)
     * can be any printable ASCII character.  Grasp the left or
     * right side of the cube with one hand and rotate it so that
     * the face under consideration is in front of you.  The order
     * of the markers is left-to-right, top-to-bottom with respect
     * to each face.  For example, consider the following "unfolded"
     * cube:
     * <pre>
     *              Red Red Wht
     *              Yel Wht Wht
     *              Wht Red Red
     *
     * Wht Wht Grn  Org Yel Yel  Blu Red Org  Yel Grn Grn
     * Red Org Org  Yel Yel Yel  Blu Red Org  Blu Grn Blu
     * Yel Wht Grn  Org Wht Org  Yel Blu Blu  Red Grn Red
     *
     *              Blu Org Blu
     *              Grn Blu Grn
     *              Wht Org Grn
     * </pre>
     * With the faces above corresponding to:
     * <pre>
     *      Up
     * Left Front Right Back
     *      Down
     * </pre>
     * Then one possible way to enter this configuration is:
     * <p>
     * &gt;java ch.randelshofer.rubik.solver.Main L:WWGROOYWG R:BROBROYBB U:RRWYWWWRR D:BOBGBGWOG
     *  F:OYYYYYOWO B:YGGBGBRGR
     * <p>
     * Note: that the choice of marker characters is completely
     * arbitrary, one must only use them consistently within
     * any given cube configuration specification.  Also, the
     * order of each face specification is arbitrary.
     * <p>
     * When run, the program will first load the move mapping
     * and pruning tables (it will regenerate them if not found)
     * then perform the search.  Successively shorter solutions
     * will be printed out until an optimal solution is found
     * (that may take a very long time) and the program terminates,
     * or the program is aborted by the user.
     *
     */
    public static void main(String[] args) {
        //args = new String[] {"L:WWGROOYWG R:BROBROYBB U:RRWYWWWRR D:BOBGBGWOG F:OYYYYYOWO B:YGGBGBRGR"};
        if (args.length == 0) {
            startGUI();
        } else {
            startBatch(args);
        }
    }

    public static void startBatch(String[] args) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                buf.append(' ');
            }
            buf.append(args[i]);
        }
        String faceSpecification = buf.toString();


        // The FaceletCube represents the cube by the markings

        FaceletCube faceletCube = new FaceletCube();
        CubeParser cubeParser = new CubeParser();
        int status;
        if ((status = cubeParser.parseInput(faceSpecification, faceletCube)) != CubeParser.VALID) {
            System.out.println("Error parsing input: " + cubeParser.getErrorText(status));
            System.exit(10);
        }

        // Validate the facelet representation in terms  of
        //  legal cubie markings, permutation, and parity
        //   and initialize a "standard" cube.  The standard
        //   cube represents the cube state in terms of cubie
        //   permutation and parity.
        KociembaCube cube = new KociembaCube();
        if ((status = faceletCube.validate(cube)) != FaceletCube.VALID) {
            System.out.println("Error validating cube: " + faceletCube.getErrorText(status));
            System.exit(10);
        }

        ProgressObserver progressMonitor = new ProgressPrinter();

        // Create a solver, initialize the move mapping and
        //   pruning tables, and invoke the search for a
        //   solution.  Since the cube is in a valid configuration
        //   at this point, a solution should always be found.
        Solver solver = new Solver();
        solver.initializeTables(progressMonitor);

        Notation notation = new DefaultNotation();
        try {
            switch (solver.solve(progressMonitor, cube, notation)) {
                case Solver.ABORT:
                    System.out.println("Solver aborted");
                    System.exit(10);
                    break;
                case Solver.FOUND:
                    System.out.println("Solution found: " + solver.getSolution().toString(notation, null));
                    System.exit(0);
                    break;
                case Solver.NOT_FOUND:
                    System.out.println("No solution found");
                    System.exit(2);
                    break;
                case Solver.OPTIMUM_FOUND:
                    System.out.println("Optimal Solution found: " + solver.getSolution().toString(notation, null));
                    System.exit(1);
                    break;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void startGUI() {
        Solver solver = null;
        while (true) {
            String faceSpecification;
            int status;

            faceSpecification = (String) JOptionPane.showInputDialog(
                    new JFrame(),
                    "Enter cube faces or press Cancel to abort",
                    "Kociemba Solver",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "L:WWGROOYWG R:BROBROYBB U:RRWYWWWRR D:BOBGBGWOG F:OYYYYYOWO B:YGGBGBRGR");
            if (faceSpecification == null) {
                break;
            }

            // Parse the input and initialize a "FaceletCube".
            // The FaceletCube represents the cube by the markings
            //   of the 54 individual facelets.
            FaceletCube faceletCube = new FaceletCube();
            CubeParser cubeParser = new CubeParser();

            if ((status = cubeParser.parseInput(faceSpecification, faceletCube)) != CubeParser.VALID) {
                // System.out.println(cubeParser.getErrorText(status));
                JOptionPane.showMessageDialog(
                        new JFrame(),
                        "Error parsing input: " + cubeParser.getErrorText(status),
                        "Kociemba Two-Phase Solver",
                        JOptionPane.PLAIN_MESSAGE);
                continue; //System.exit(1);
            }

            // Validate the facelet representation in terms  of
            //  legal cubie markings, permutation, and parity
            //   and initialize a "standard" cube.  The standard
            //   cube represents the cube state in terms of cubie
            //   permutation and parity.

            KociembaCube cube = new KociembaCube();
            if ((status = faceletCube.validate(cube)) != FaceletCube.VALID) {
                //System.out.println(faceletCube.getErrorText(status));
                JOptionPane.showMessageDialog(
                        new JFrame(),
                        "Error validating input: " + cubeParser.getErrorText(status),
                        "Kociemba Two-Phase Solver",
                        JOptionPane.PLAIN_MESSAGE);
                continue; //System.exit(1);
            }

            ProgressObserver progressView = new ProgressView("Kociemba Two-Phase Solver", "Initializing...", 0, Integer.MAX_VALUE);

            // Create a solver, initialize the move mapping and
            //   pruning tables, and invoke the search for a
            //   solution.  Since the cube is in a valid configuration
            //   at this point, a solution should always be found.
            if (solver == null) {
                solver = new Solver();
                solver.initializeTables(null);
            }

            Notation notation = new DefaultNotation();
            switch (solver.solve(progressView, cube, notation)) {
                case Solver.ABORT:
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            "Solver aborted",
                            "Kociemba Two-Phase Solver",
                            JOptionPane.PLAIN_MESSAGE);
                    break;
                case Solver.FOUND: {
                    String solution;
                    try {
                        solution = solver.getSolution().toString(notation, null);
                    } catch (IOException e) {
                        solution = solver.getSolution().toString();
                    }
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            "Solution found: " + solution,
                            "Kociemba Two-Phase Solver",
                            JOptionPane.PLAIN_MESSAGE);
                    break;
                }
                case Solver.NOT_FOUND:
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            "No solution found",
                            "Kociemba Two-Phase Solver",
                            JOptionPane.PLAIN_MESSAGE);
                    break;
                case Solver.OPTIMUM_FOUND: {
                    String solution;
                    try {
                        solution = solver.getSolution().toString(notation);
                    } catch (IOException e) {
                        solution = solver.getSolution().toString();
                    }
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            "Optimal solution found: " + solution,
                            "Kociemba Two-Phase Solver",
                            JOptionPane.PLAIN_MESSAGE);
                    break;
                }
            }
        }
        System.exit(0);
    }
}
