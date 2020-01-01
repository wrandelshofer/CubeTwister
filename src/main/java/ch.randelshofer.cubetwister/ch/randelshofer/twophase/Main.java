package ch.randelshofer.twophase;

import ch.randelshofer.rubik.Cubes;
import ch.randelshofer.rubik.RubiksCube;
import ch.randelshofer.rubik.notation.DefaultNotation;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.parser.Node;
import ch.randelshofer.rubik.parser.ScriptParser;
import org.jhotdraw.annotation.Nonnull;
import org.kociemba.twophase.Search;
import org.kociemba.twophase.Tools;

import java.io.IOException;


/**
 * Main class for Kociemba's two-phase solver.
 *
 * @author Werner Randelshofer
 */
public class Main {

    public static void main(@Nonnull String[] args) throws IOException {
        if (args.length == 0) {
            doRandom(1);
        }
    }

    public static void doRandom(int count) throws IOException {
        for (int i = 0; i < count; i++) {
            String facelets = Tools.randomCube();
            String iStr = Integer.toString(i);
            String indent = "          ".substring(0, iStr.length());
            System.out.println(iStr + ". Facelets = " + facelets);

            //
            String stickers = faceletsToStickers(facelets);
            RubiksCube cube = new RubiksCube();
            System.out.println(stickers);
            Cubes.setToStickersString(cube, stickers, "RUFLDB");
            Notation notation = new DefaultNotation();
            ScriptParser parser = new ScriptParser(notation);

            String solution = new Search().solution(facelets, 21, 1000, true);
            System.out.println(indent + "  Solution = " + solution);
            Node script = parser.parse(solution);
            System.out.println(indent + "  Parsed   = " + script.toString(notation));
            System.out.println(indent + "  #Moves   = " + solution.split("[ .]+").length);
            script.applyTo(cube, false);
            System.out.println(indent + " Perm = " + Cubes.toPermutationString(cube));

        }
    }

    @Nonnull
    public static String faceletsToStickers(@Nonnull String facelets) {
        StringBuilder stickers = new StringBuilder();
        String faces = "URFDLB";
        for (int i = 0; i < 6; i++) {
            stickers.append(faces.charAt(i));
            stickers.append(':');
            for (int j = 0; j < 9; j++) {
                stickers.append(facelets.charAt(i * 9 + j));
            }
            stickers.append('\n');
        }
        return stickers.toString();
    }
}
