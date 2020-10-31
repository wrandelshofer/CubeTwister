/*
 * @(#)CubeMarkupTest.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CubeMarkupTest {
    @Nonnull
    @TestFactory
    public List<DynamicTest> testAgainstXmlSchema() {
        return Arrays.asList(
                // token, isToken
                DynamicTest.dynamicTest("Template", () -> toTestAgainstXmlSchema(Template.getTemplate())),
                DynamicTest.dynamicTest("Extended Template", () -> toTestAgainstXmlSchema(
                        Paths.get("../../../../resources/examples/Extended Template.xml").toUri().toURL()
                ))
        );
    }

    private void toTestAgainstXmlSchema(URL docUrl) throws Exception {
        URL schemaUrl = CubeMarkup.getSchema();
        try (InputStream docStream = docUrl.openStream();
             InputStream schemaStream = schemaUrl.openStream()) {

            validate(new StreamSource(docStream), new StreamSource(schemaStream));
        }
    }

    public static void validate(Source docSource, Source schemaSource) throws IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = factory.newSchema(schemaSource);
            Validator validator = schema.newValidator();
            validator.validate(docSource);
        } catch (SAXException e) {
            throw new IOException("The document is invalid.\n" + e.getMessage(), e);
        }
    }
}
