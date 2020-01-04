package ch.randelshofer.cubetwister.doc;

import java.net.URL;

public class Template {
    public static URL getTemplate() {
        return Template.class.getResource("template.xml");
    }
}
