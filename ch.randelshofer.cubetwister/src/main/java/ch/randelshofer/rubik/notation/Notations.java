/*
 * @(#)Notations.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.notation;

import java.util.List;

public class Notations {
    public static String dumpNotation(Notation notation) {
        StringBuffer buf = new StringBuffer();
        int layerCount = notation.getLayerCount();
        int[] faceToAxis = {0, 1, 2, 0, 1, 2};
        String[] angleToString = {"180°", "90°", "", "-90°", "-180°"};
        boolean[] faceToFlipMask = {true, true, true, false, false, false};
        for (int rawMask = 1, n = 1 << layerCount; rawMask < n; rawMask++) {
            //for (int axis = 0; axis < 3; axis++) {
            for (int rawAngle : new int[]{-1, 1, -2, 2}) {
                String binary = Integer.toBinaryString(rawMask);
                for (int i = binary.length(); i < layerCount; i++) {
                    buf.append('○');
                }
                buf.append(binary.replace('0', '○').replace('1', '●'));
                buf.append(' ');
                buf.append(angleToString[rawAngle + 2]);
                for (int face = 0; face < 6; face++) {
                    int axis = faceToAxis[face];
                    int angle;
                    boolean flip = faceToFlipMask[face];
                    int mask;
                    if (flip) {
                        angle = -rawAngle;
                        mask = 0;
                        for (int i = 0; i < layerCount; i++) {
                            if ((rawMask & (1 << i)) != 0) {
                                mask |= 1 << (layerCount - i - 1);
                            }
                        }
                    } else {
                        angle = rawAngle;
                        mask = rawMask;
                    }
                    Move move = new Move(layerCount, axis, mask, angle);
                    List<String> moveTokens = notation.getMoveTokens(move);
                    if (!moveTokens.isEmpty()) {
                        buf.append(' ');
                        buf.append(moveTokens);
                    }
                }
                buf.append('\n');
            }
            // }
        }
        return buf.toString();
    }
}
