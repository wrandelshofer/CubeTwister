package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.rubik.notation.Move;
import ch.randelshofer.rubik.notation.ScriptNotation;
import ch.randelshofer.rubik.notation.Symbol;
import nanoxml.XMLElement;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class DocumentModelXmlWriter {

    private final Map<Object, String> objectToId = new IdentityHashMap<>();

    private String getOrCreateId(Object o) {
        return objectToId.computeIfAbsent(o, k -> "o" + (objectToId.size() + 1));
    }

    public static final int DOCUMENT_VERSION = 9;
    /**
     * This set is used to determine which entities should
     * be written by method writeXML.
     *
     * @see #writeXml(DocumentModel, OutputStream, Object[])
     */
    @Nullable
    private Set<Object> writeMembersOnly;

    /**
     * Writes the indicated entities of the DocumentModel into the
     * output stream.
     * For peak performance, the output stream should be buffered.
     */
    public void writeXml(DocumentModel model, @Nonnull OutputStream out, Object[] entities)
            throws IOException {
        if (entities != null) {
            writeMembersOnly = Collections.newSetFromMap(new IdentityHashMap<>());
            writeMembersOnly.addAll(Arrays.asList(entities));
        }
        PrintWriter w = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        writeXml(model, w);
        w.flush();
        writeMembersOnly = null;
    }

    private final static String[] axisTable = {"x", "y", "z"};

    /**
     * Writes the contents of the DocumentModel into the output stream.
     * For peak performance, the output stream should be buffered.
     */
    public void writeXml(DocumentModel model, @Nonnull PrintWriter out) throws IOException {

        // Create the DOM XMLElement. Our Markup language
        // is CubeMarkup.
        objectToId.clear();
        XMLElement doc = new XMLElement(null, false, false);
        doc.setName("CubeMarkup");

        XMLElement docRoot = doc;
        docRoot.setAttribute("version", Integer.toString(DOCUMENT_VERSION));

        // ------------------------------------
        // Write Cubes
        for (EntityModel child : model.getChild(model.getRoot(), DocumentModel.CUBE_INDEX).getChildren()) {
            writeCube(model, doc, docRoot, (CubeModel) child);
        }

        // ------------------------------------
        // Write Notations
        for (EntityModel child : model.getChild(model.getRoot(), DocumentModel.NOTATION_INDEX).getChildren()) {
            writeNotation(model, doc, docRoot, (NotationModel) child);
        }

        // ------------------------------------
        // Write Scripts
        for (EntityModel child : model.getChild(model.getRoot(), DocumentModel.SCRIPT_INDEX).getChildren()) {
            writeScript(model, doc, docRoot, (ScriptModel) child);
        }

        // ------------------------------------
        // Write Texts
        for (EntityModel child : model.getChild(model.getRoot(), DocumentModel.TEXT_INDEX).getChildren()) {
            writeText(model, doc, docRoot, (TextModel) child);
        }

        // Write the document to the stream
        doc.print(out);
        out.flush();
    }

    private void writeText(DocumentModel model, XMLElement doc, XMLElement docRoot, TextModel child) {
        XMLElement elem;
        XMLElement elem2;
        TextModel item = child;
        if (!isWriteMember(item)) {
            return;
        }

        // Create the document element.
        elem = doc.createElement("Text");
        elem.setAttribute("id", getOrCreateId(child));

        if (item.getName() != null) {
            elem2 = doc.createElement("Title");
            elem2.setContent(item.getName());
            elem.addChild(elem2);
        }
        if (item.getDescription() != null) {
            elem2 = doc.createElement("Body");
            elem2.setContent(item.getDescription());
            elem.addChild(elem2);
        }
        if (item.getAuthor() != null) {
            elem2 = doc.createElement("Author");
            elem2.setContent(item.getAuthor());
            elem.addChild(elem2);
        }
        if (item.getDate() != null) {
            elem2 = doc.createElement("Date");
            elem2.setContent(item.getDate());
            elem.addChild(elem2);
        }
        docRoot.addChild(elem);
    }

    private void writeScript(DocumentModel model, XMLElement doc, XMLElement docRoot, ScriptModel child) {
        XMLElement elem;
        XMLElement elem2;
        XMLElement elem3;
        ScriptModel item = child;
        if (!isWriteMember(item)) {
            return;
        }

        // Create the document element.
        elem = doc.createElement("Script");
        elem.setAttribute("id", getOrCreateId(child));
        if (item.getNotationModel() != null) {
            elem.setAttribute("notationRef", "o" + getOrCreateId(item.getNotationModel()));
        }
        if (item.getCubeModel() != null) {
            elem.setAttribute("cubeRef", "o" + getOrCreateId(item.getCubeModel()));
        }
        elem.setAttribute("scriptType", item.isGenerator() ? "generator" : "solver");

        if (item.getName() != null) {
            elem2 = doc.createElement("Name");
            elem2.setContent(item.getName());
            elem.addChild(elem2);
        }
        if (item.getDescription() != null) {
            elem2 = doc.createElement("Description");
            elem2.setContent(item.getDescription());
            elem.addChild(elem2);
        }
        if (item.getScript() != null) {
            elem2 = doc.createElement("Source");
            if (item.getScript() != null) {
                elem2.setContent(Normalizer.normalize(item.getScript(), Normalizer.Form.NFC));
            }
            elem.addChild(elem2);
        }
        if (item.getAuthor() != null) {
            elem2 = doc.createElement("Author");
            elem2.setContent(item.getAuthor());
            elem.addChild(elem2);
        }
        if (item.getDate() != null) {
            elem2 = doc.createElement("Date");
            elem2.setContent(item.getDate());
            elem.addChild(elem2);
        }
        for (EntityModel child2 : item.getMacroModels().getChildren()) {
            MacroModel macro = (MacroModel) child2;
            elem2 = doc.createElement("Macro");
            elem2.setAttribute("identifier", Normalizer.normalize(macro.getIdentifier(), Normalizer.Form.NFC));

            if (macro.getScript() != null && !macro.getScript().isEmpty()) {
                elem3 = doc.createElement("Source");
                elem3.setContent(Normalizer.normalize(macro.getScript(), Normalizer.Form.NFC));
                elem2.addChild(elem3);
            }
            if (macro.getDescription() != null && !macro.getDescription().isEmpty()) {
                elem3 = doc.createElement("Description");
                elem3.setContent(macro.getDescription());
                elem2.addChild(elem3);
            }
            elem.addChild(elem2);
        }
        docRoot.addChild(elem);
    }

    private void writeNotation(DocumentModel model, XMLElement doc, XMLElement docRoot, NotationModel child) {
        if (!isWriteMember(child)) {
            return;
        }

        Map<String, String> macroDescriptions = new LinkedHashMap<>();
        for (EntityModel entityModel : child.getMacroModels().getChildren()) {
            if (entityModel instanceof MacroModel mm) {
                macroDescriptions.put(mm.getIdentifier(), mm.getDescription());
            }
        }
        writeNotation(doc, docRoot, child,
                (ScriptNotation) child, child.isDefaultNotation(), child.getDescription(), child.getAuthor(), child.getDate(),
                macroDescriptions::get
        );
    }

    private void writeNotation(XMLElement doc, XMLElement docRoot, Object object,
                               ScriptNotation notation, boolean isDefaultNotation, String description, String author, String date, Function<String, String> macroDescriptions) {
        XMLElement elem;
        XMLElement elem2Enabled;
        XMLElement elem3;
        XMLElement elem4;
        ScriptNotation item = notation;

        // Create the document element.
        elem = doc.createElement("Notation");
        elem.setAttribute("id", getOrCreateId(object));
        elem.setAttribute("layerCount", item.getLayerCount());


        if (isDefaultNotation) {
            elem.setAttribute("default", "true");
        }

        if (item.getName() != null) {
            elem2Enabled = doc.createElement("Name");
            elem2Enabled.setContent(item.getName());
            elem.addChild(elem2Enabled);
        }
        if (description != null) {
            elem2Enabled = doc.createElement("Description");
            elem2Enabled.setContent(description);
            elem.addChild(elem2Enabled);
        }
        if (author != null) {
            elem2Enabled = doc.createElement("Author");
            elem2Enabled.setContent(author);
            elem.addChild(elem2Enabled);
        }
        if (date != null) {
            elem2Enabled = doc.createElement("Date");
            elem2Enabled.setContent(date);
            elem.addChild(elem2Enabled);
        }

        List<Symbol> statements = new ArrayList<Symbol>();
        statements.add(Symbol.COMMENT);
        statements.addAll(Symbol.STATEMENT.getSubSymbols());
        statements.sort(null);
        for (Symbol s : statements) {
            elem2Enabled = doc.createElement("Statement");
            elem2Enabled.setAttribute("symbol", s.toString());
            if (s.isTerminalSymbol()) {
                if (!item.isSupported(s)) {
                    elem2Enabled.setAttribute("enabled", false);
                }
                List<String> allTokens = item.getAllTokens(s);
                if (!allTokens.isEmpty()) {
                    elem3 = doc.createElement("Token");
                    elem3.setAttribute("symbol", s.toString());
                    elem3.setContent(Normalizer.normalize(String.join(" ", allTokens), Normalizer.Form.NFC));
                    elem2Enabled.addChild(elem3);
                }
            } else {
                if (!item.isSupported(s)) {
                    elem2Enabled.setAttribute("enabled", false);
                }
                if (item.getSyntax(s) != null) {
                    elem2Enabled.setAttribute("syntax", item.getSyntax(s).toString());
                }
                for (Symbol t : s.getSubSymbols()) {
                    List<String> allTokens = item.getAllTokens(t);
                    if (!allTokens.isEmpty()) {
                        elem3 = doc.createElement("Token");
                        elem3.setAttribute("symbol", t.toString());
                        elem3.setContent(Normalizer.normalize(String.join(" ", allTokens), Normalizer.Form.NFC));
                        elem2Enabled.addChild(elem3);
                    }
                }
            }
            elem.addChild(elem2Enabled);
        }

        // Write moves
        {
            elem2Enabled = doc.createElement("Statement");
            elem2Enabled.setAttribute("symbol", Symbol.MOVE.toString());
            elem2Enabled.setAttribute("enabled", true);

            var elem2Disabled = doc.createElement("Statement");
            elem2Disabled.setAttribute("symbol", Symbol.MOVE.toString());
            elem2Disabled.setAttribute("enabled", false);

            // We sort move symbols to generate XML files with a
            // consistent output which can be easily diffed.
            ArrayList<Move> moveSymbols = new ArrayList<>(item.getAllMoveSymbols());
            Collections.sort(moveSymbols);
            for (Move ts : moveSymbols) {
                List<String> allTokens = (ts == null) ? null : item.getAllMoveTokens(ts);
                if (!allTokens.isEmpty()) {
                    elem4 = doc.createElement("Token");
                    elem4.setAttribute("axis", axisTable[ts.getAxis()]);
                    elem4.setAttribute("angle", ts.getAngle() * 90);
                    elem4.setAttribute("layerList", ts.getLayerList());
                    elem4.setContent(Normalizer.normalize(String.join(" ", allTokens), Normalizer.Form.NFC));
                    if (item.isMoveSupported(ts)) {
                        elem2Enabled.addChild(elem4);
                    } else {
                        elem2Disabled.addChild(elem4);
                    }
                }
            }
            // write enabled moves and then disabled moves
            if (!elem2Enabled.getChildren().isEmpty()) {
                elem.addChild(elem2Enabled);
            }
            if (!elem2Disabled.getChildren().isEmpty()) {
                elem.addChild(elem2Disabled);
            }
        }


        // Write macros
        for (Map.Entry<String, String> macro : item.getAllMacros().entrySet()) {
            String identifier = macro.getKey();
            String script = macro.getValue();
            elem2Enabled = doc.createElement("Macro");
            elem2Enabled.setAttribute("identifier", Normalizer.normalize(identifier, Normalizer.Form.NFC));

            if (script != null && !script.isEmpty()) {
                elem3 = doc.createElement("Source");
                elem3.setContent(Normalizer.normalize(script, Normalizer.Form.NFC));
                elem2Enabled.addChild(elem3);
            }
            String macroDescription = macroDescriptions.apply(identifier);
            if (macroDescription != null && !macroDescription.isEmpty()) {
                elem3 = doc.createElement("Description");
                elem3.setContent(macroDescription);
                elem2Enabled.addChild(elem3);
            }
            elem.addChild(elem2Enabled);
        }
        docRoot.addChild(elem);
    }

    private void writeCube(DocumentModel model, XMLElement doc, XMLElement docRoot, CubeModel child) {
        XMLElement elem;
        XMLElement elem2;
        CubeModel item = child;
        if (!isWriteMember(item)) {
            return;
        }


        // Create the document element.
        elem = doc.createElement("Cube");
        elem.setAttribute("id", getOrCreateId(child));

        elem.setAttribute("kind", item.getKind().getAlternativeName(0));

        if (item == model.getDefaultCube()) {
            elem.setAttribute("default", "true");
        }
        elem.setAttribute("scale", Integer.toString(item.getIntScale()));
        elem.setAttribute("explode", Integer.toString(item.getIntExplode()));
        elem.setAttribute("alpha", Integer.toString(item.getIntAlpha()));
        elem.setAttribute("beta", Integer.toString(item.getIntBeta()));
        elem.setAttribute("twistDuration", Integer.toString(item.getTwistDuration()));
        elem.setColorAttribute("backgroundColor", item.getFrontBgColor(), Color.WHITE);
        elem.setColorAttribute("rearBackgroundColor", item.getRearBgColor(), Color.WHITE);
        if (item.getName() != null) {
            elem2 = doc.createElement("Name");
            elem2.setContent(item.getName());
            elem.addChild(elem2);
        }
        if (item.getDescription() != null) {
            elem2 = doc.createElement("Description");
            elem2.setContent(item.getDescription());
            elem.addChild(elem2);
        }
        if (item.getAuthor() != null) {
            elem2 = doc.createElement("Author");
            elem2.setContent(item.getAuthor());
            elem.addChild(elem2);
        }
        if (item.getDate() != null) {
            elem2 = doc.createElement("Date");
            elem2.setContent(item.getDate());
            elem.addChild(elem2);
        }
        for (EntityModel child2 : item.getColors().getChildren()) {
            CubeColorModel color = (CubeColorModel) child2;

            // Create the document element
            elem2 = doc.createElement("Color");
            elem2.setAttribute("id", getOrCreateId(color));
            if (color.getColor() != null) {
                elem2.setAttribute("argb", Integer.toHexString(color.getColor().getRGB()));
                elem2.setContent(color.getName());
                elem.addChild(elem2);
            }
        }
        int j = 0;
        for (EntityModel child2 : item.getParts().getChildren()) {
            CubePartModel part = (CubePartModel) child2;

            elem2 = doc.createElement("Part");
            elem2.setAttribute("index", Integer.toString(j++));
            elem2.setAttribute("visible", Boolean.toString(part.isVisible()));
            elem2.setAttribute("fillColorRef", "o" + getOrCreateId(part.getFillColorModel()));
            elem2.setAttribute("outlineColorRef", "o" + getOrCreateId(part.getOutlineColorModel()));
            elem.addChild(elem2);
        }
        j = 0;
        for (EntityModel child2 : item.getStickers().getChildren()) {
            CubeStickerModel sticker = (CubeStickerModel) child2;

            elem2 = doc.createElement("Sticker");
            elem2.setAttribute("index", Integer.toString(j++));
            elem2.setAttribute("visible", Boolean.toString(sticker.isVisible()));
            elem2.setAttribute("fillColorRef", "o" + getOrCreateId(sticker.getFillColorModel()));
            elem.addChild(elem2);
        }

        if (item.getStickersImageModel().hasImage()) {
            elem2 = doc.createElement("StickersImage");
            elem2.setAttribute("visible", Boolean.toString(item.isStickersImageVisible()));
            elem2.setContent(item.getStickersImageModel().getBase64Image());
            elem.addChild(elem2);
        }
        if (item.getFrontBgImageModel().hasImage()) {
            elem2 = doc.createElement("FrontBgImage");
            elem2.setAttribute("visible", Boolean.toString(item.isFrontBgImageVisible()));
            elem2.setContent(item.getFrontBgImageModel().getBase64Image());
            elem.addChild(elem2);
        }
        if (item.getRearBgImageModel().hasImage()) {
            elem2 = doc.createElement("RearBgImage");
            elem2.setAttribute("visible", Boolean.toString(item.isRearBgImageVisible()));
            elem2.setContent(item.getRearBgImageModel().getBase64Image());
            elem.addChild(elem2);
        }
        docRoot.addChild(elem);
    }

    public boolean isWriteMember(EntityModel m) {
        if (writeMembersOnly == null) {
            return true;
        }
        return writeMembersOnly.contains(m);
    }
}
