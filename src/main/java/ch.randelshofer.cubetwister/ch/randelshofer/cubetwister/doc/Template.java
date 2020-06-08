/*
 * @(#)Template.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import java.net.URL;

public class Template {
    public static URL getTemplate() {
        return Template.class.getResource("template.xml");
    }
}
