/*
 * @(#)CubeMarkup.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import java.net.URL;

public class CubeMarkup {
    public static URL getSchema() {
        return CubeMarkup.class.getResource("CubeMarkup.xsd");
    }
}
