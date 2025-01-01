package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.tree.TreeNodeImpl;
import ch.randelshofer.rubik.CubeKind;
import ch.randelshofer.rubik.notation.Move;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import nanoxml.XMLElement;
import nanoxml.XMLParseException;
import org.jhotdraw.annotation.Nonnull;

import javax.swing.tree.MutableTreeNode;
import java.awt.Color;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;

public class DocumentModelXmlReader {
    public void insertXMLNodeInto(DocumentModel model, @Nonnull XMLElement doc, @Nonnull MutableTreeNode parent, int index)
            throws IOException {
        // We keep track of all the objects we read.
        // The hash map key is a String representation of
        // the object's id, the hash map value is the object.
        HashMap<String, Object> objects = new HashMap<String, Object>();

        String attrValue;

        // Read XMLElement root
        if (!doc.getName().equals("CubeMarkup")) {
            throw new IOException("Unsupported document type: " + doc);
        }
        attrValue = doc.getStringAttribute("version");
        int documentVersion = 0;
        if (attrValue != null && !attrValue.isEmpty()) {
            try {
                documentVersion = Integer.parseInt(attrValue);
            } catch (NumberFormatException e) {
                documentVersion = Integer.MAX_VALUE;
            }
        }
        if (documentVersion > DocumentModelXmlWriter.DOCUMENT_VERSION) {
            throw new IOException("Unsupported document version: " + attrValue);
        }
        var root = model.getRoot();
        int realCubeIndex = root.getChildAt(DocumentModel.CUBE_INDEX).getIndex(parent) + 1;
        int realNotationIndex = root.getChildAt(DocumentModel.NOTATION_INDEX).getIndex(parent) + 1;
        int realScriptIndex = root.getChildAt(DocumentModel.SCRIPT_INDEX).getIndex(parent) + 1;
        int realTextIndex = root.getChildAt(DocumentModel.TEXT_INDEX).getIndex(parent) + 1;

        // Read Cube elements
        // ------------------
        for (XMLElement elem : doc.iterableChildren()) {
            if ("Cube".equals(elem.getName())) {

                attrValue = elem.getStringAttribute("id");
                CubeModel item = null;
                if (attrValue != null && !"".equals(attrValue) && objects.get(attrValue) instanceof CubeModel) {
                    item = (CubeModel) objects.get(attrValue);
                }
                if (item == null) {
                    item = new CubeModel(null);
                    if (attrValue != null && !"".equals(attrValue)) {
                        objects.put(attrValue, item);
                    }
                    if (parent == root.getChildAt(DocumentModel.CUBE_INDEX)) {
                        model.insertNodeInto(item, parent, index++);
                    } else if (parent.getParent() == root.getChildAt(DocumentModel.CUBE_INDEX)) {
                        model.insertNodeInto(item, (TreeNodeImpl) parent.getParent(), realCubeIndex++);
                    } else {
                        model.addTo(item, root.getChildAt(DocumentModel.CUBE_INDEX));
                    }
                }
                if (elem.getBooleanAttribute("default", false)) {
                    model.setDefaultCube(item);
                }

                item.basicSetKind(elem.getAttribute("kind", CubeKind.getKindMap(), CubeKind.RUBIK.getAlternativeName(0), false));

                item.basicSetIntScale(elem.getIntAttribute("scale", 25, 300, 100));
                item.basicSetIntExplode(elem.getIntAttribute("explode", 0, 200, 0));
                item.basicSetIntAlpha(elem.getIntAttribute("alpha", -90, 90, -25));
                item.basicSetIntBeta(elem.getIntAttribute("beta", -90, 90, 45));
                item.setTwistDuration(elem.getIntAttribute("twistDuration", 0, 2000, 400));
                item.setFrontBgColor(elem.getColorAttribute("backgroundColor", Color.WHITE));
                item.setRearBgColor(elem.getColorAttribute("rearBackgroundColor", Color.WHITE));

                boolean isFirstColor = true;

                for (XMLElement elem2 : elem.iterableChildren()) {

                    String name = elem2.getName();
                    if ("Name".equals(name)) {
                        item.basicSetName(elem2.getContent());
                    } else if ("Description".equals(name)) {
                        item.basicSetDescription(elem2.getContent());
                    } else if ("Author".equals(name)) {
                        item.basicSetAuthor(elem2.getContent());
                    } else if ("Date".equals(name)) {
                        item.basicSetDate(elem2.getContent());
                    } else if ("Color".equals(name)) {
                        if (isFirstColor) {
                            item.getColors().removeAllChildren();
                            isFirstColor = false;
                        }
                        attrValue = elem2.getStringAttribute("id");
                        CubeColorModel color = null;
                        if (objects.get(attrValue) instanceof CubeColorModel) {
                            color = (CubeColorModel) objects.get(attrValue);
                        }
                        if (color == null) {
                            color = new CubeColorModel();
                            objects.put(attrValue, color);
                            item.getColors().add(color);
                            color.addPropertyChangeListener(item);
                        }
                        color.basicSetName(elem2.getContent());
                        try {
                            color.basicSetColor(new Color((int) Long.parseLong(elem2.getStringAttribute("argb", "ff000000"), 16), true));
                        } catch (NumberFormatException e) {/*e.printStackTrace();*/

                        }
                        if (elem2.getAttribute("red") != null) {
                            float red = (float) elem2.getDoubleAttribute("red");
                            float green = (float) elem2.getDoubleAttribute("green");
                            float blue = (float) elem2.getDoubleAttribute("blue");
                            float alpha = (float) elem2.getDoubleAttribute("alpha");
                            color.basicSetColor(new Color(red, green, blue, alpha));
                        }
                    } else if ("Part".equals(name)) {
                        int partIndex = elem2.getIntAttribute("index");
                        // In documents prior version 4, the parts were
                        // numbered differently.
                        if (documentVersion < 4) {
                            if (partIndex < 8) {
                                partIndex = (partIndex + 2) % 8;
                            } else {
                                switch (item.getKind().getLayerCount()) {
                                    case 3:
                                        if (partIndex < 8 + 12) {
                                            partIndex = (partIndex - 8 + 3) % 12 + 8;
                                        } else if (partIndex < 8 + 12 + 6) {
                                            switch (partIndex - 8 - 12) {
                                                case 0:
                                                    partIndex = 2 + 8 + 12;
                                                    break;
                                                case 1:
                                                    partIndex = 0 + 8 + 12;
                                                    break;
                                                case 2:
                                                    partIndex = 4 + 8 + 12;
                                                    break;
                                                case 3:
                                                    partIndex = 5 + 8 + 12;
                                                    break;
                                                case 4:
                                                    partIndex = 3 + 8 + 12;
                                                    break;
                                                case 5:
                                                    partIndex = 1 + 8 + 12;
                                                    break;
                                            }
                                        }
                                        break;
                                    case 4:
                                        if (partIndex < 8 + 24) {
                                            partIndex = (partIndex - 8 + 3) % 24 + 8;
                                        } else if (partIndex < 8 + 24 + 24) {
                                            switch ((partIndex - 8 - 24) % 6) {
                                                case 0:
                                                    partIndex = (partIndex - 8 - 24) + 2 + 8 + 24;
                                                    break;
                                                case 1:
                                                    partIndex = (partIndex - 8 - 24 - 1) + 0 + 8 + 24;
                                                    break;
                                                case 2:
                                                    partIndex = (partIndex - 8 - 24 - 2) + 4 + 8 + 24;
                                                    break;
                                                case 3:
                                                    partIndex = (partIndex - 8 - 24 - 3) + 5 + 8 + 24;
                                                    break;
                                                case 4:
                                                    partIndex = (partIndex - 8 - 24 - 4) + 3 + 8 + 24;
                                                    break;
                                                case 5:
                                                    partIndex = (partIndex - 8 - 24 - 5) + 1 + 8 + 24;
                                                    break;
                                            }
                                        }
                                        break;
                                    case 5:
                                        if (partIndex < 8 + 36) {
                                            partIndex = (partIndex - 8 + 3) % 36 + 8;
                                        } else if (partIndex < 8 + 36 + 54) {
                                            switch ((partIndex - 8 - 36) % 6) {
                                                case 0:
                                                    partIndex = (partIndex - 8 - 36) + 2 + 8 + 36;
                                                    break;
                                                case 1:
                                                    partIndex = (partIndex - 8 - 36 - 1) + 0 + 8 + 36;
                                                    break;
                                                case 2:
                                                    partIndex = (partIndex - 8 - 36 - 2) + 4 + 8 + 36;
                                                    break;
                                                case 3:
                                                    partIndex = (partIndex - 8 - 36 - 3) + 5 + 8 + 36;
                                                    break;
                                                case 4:
                                                    partIndex = (partIndex - 8 - 36 - 4) + 3 + 8 + 36;
                                                    break;
                                                case 5:
                                                    partIndex = (partIndex - 8 - 36 - 5) + 1 + 8 + 36;
                                                    break;
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                        if (0 <= partIndex && partIndex < item.getPartCount()) {
                            CubePartModel part = (CubePartModel) item.getParts().getChildAt(partIndex);
                            part.basicSetVisible(elem2.getBooleanAttribute("visible", true));
                            part.basicSetFillColorModel((CubeColorModel) objects.get(elem2.getStringAttribute("fillColorRef")));
                            part.basicSetOutlineColorModel((CubeColorModel) objects.get(elem2.getStringAttribute("outlineColorRef")));
                        }
                    } else if ("Sticker".equals(name)) {
                        int stickerIndex = elem2.getIntAttribute("index");

                        // In documents prior version 4, the stickers were
                        // numbered differently for Pocket, Rubi, Revenge and Professor
                        if (documentVersion < 4) {
                            if (item.getKind() == CubeKind.POCKET
                                    || item.getKind() == CubeKind.RUBIK
                                    || item.getKind() == CubeKind.REVENGE
                                    || item.getKind() == CubeKind.PROFESSOR) {
                                int stickersPerFace = item.getStickerCount() / item.getFaceCount();
                                int face = stickerIndex / stickersPerFace;
                                // front,right,down,back,left,up
                                //   2,    0,    4,   5,   3,  1
                                // right,up,front,left,down,back
                                switch (face) {
                                    case 0:
                                        face = 2;
                                        break; // front
                                    case 1:
                                        face = 0;
                                        break; // right
                                    case 2:
                                        face = 4;
                                        break; // down
                                    case 3:
                                        face = 5;
                                        break; // back
                                    case 4:
                                        face = 3;
                                        break; // left
                                    case 5:
                                        face = 1;
                                        break; // up
                                }
                                stickerIndex = face * stickersPerFace + (stickerIndex % stickersPerFace);
                            }
                        }

                        if (0 <= stickerIndex && stickerIndex < item.getStickerCount()) {
                            CubeStickerModel sticker = (CubeStickerModel) item.getStickers().getChildAt(stickerIndex);
                            sticker.basicSetVisible(elem2.getBooleanAttribute("visible", true));
                            Object fillColor = objects.get(elem2.getStringAttribute("fillColorRef"));
                            if (fillColor != null && fillColor instanceof CubeColorModel) {
                                sticker.basicSetFillColorModel((CubeColorModel) fillColor);
                            }
                        }
                    } else if ("StickersImage".equals(name)) {
                        item.basicSetStickersImageVisible(elem2.getBooleanAttribute("visible", true));
                        item.getStickersImageModel().setBase64Image(elem2.getContent());
                    } else if ("FrontBgImage".equals(name)) {
                        item.basicSetFrontBgImageVisible(elem2.getBooleanAttribute("visible", true));
                        item.getFrontBgImageModel().setBase64Image(elem2.getContent());
                    } else if ("RearBgImage".equals(name)) {
                        item.basicSetRearBgImageVisible(elem2.getBooleanAttribute("visible", true));
                        item.getRearBgImageModel().setBase64Image(elem2.getContent());
                    }
                }
            } else if ("Notation".equals(elem.getName())) {

                // read Notation elements
                attrValue = elem.getStringAttribute("id");
                NotationModel item = null;
                if (attrValue != null && !"".equals(attrValue)
                        && objects.get(attrValue) instanceof NotationModel) {
                    item = (NotationModel) objects.get(attrValue);
                }
                if (item == null) {
                    item = new NotationModel();
                    if (attrValue != null && !"".equals(attrValue)) {
                        objects.put(attrValue, item);
                    }
                    if (parent == root.getChildAt(DocumentModel.NOTATION_INDEX)) {
                        model.insertNodeInto(item, parent, index++);
                    } else if (parent.getParent() == root.getChildAt(DocumentModel.NOTATION_INDEX)) {
                        model.insertNodeInto(item, (TreeNodeImpl) parent.getParent(), realNotationIndex++);
                    } else {
                        model.addTo(item, root.getChildAt(DocumentModel.NOTATION_INDEX));
                    }
                }

                // Read layer count
                item.setLayerCount(elem.getIntAttribute("layerCount", 2, 7, 3));
                HashMap<String, Move> tvs = MoveSymbols.getMoveValueSet(item.getLayerCount());

                // Must be done only after we have read the layer count!
                if (elem.getBooleanAttribute("default", false)) {
                    model.setDefaultNotation(item);
                }


                // Read legacy switches. These switches are only supported for
                // the 3x3 cube.
                boolean b;
                b = elem.getBooleanAttribute("quarterTurnTwists", true);
                for (int axis = 0; axis < 3; axis++) {
                    item.basicSetMoveSupported(new Move(3, axis, 1, 1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 1, -1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 4, 1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 4, -1), b);
                }
                b = elem.getBooleanAttribute("halfTurnTwists", true);
                for (int axis = 0; axis < 3; axis++) {
                    item.basicSetMoveSupported(new Move(3, axis, 1, 2), b);
                    item.basicSetMoveSupported(new Move(3, axis, 1, -2), b);
                    item.basicSetMoveSupported(new Move(3, axis, 4, 2), b);
                    item.basicSetMoveSupported(new Move(3, axis, 4, -2), b);
                }
                b = elem.getBooleanAttribute("midLayerTwists", true);
                for (int axis = 0; axis < 3; axis++) {
                    item.basicSetMoveSupported(new Move(3, axis, 2, 1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 2, -1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 2, 2), b);
                    item.basicSetMoveSupported(new Move(3, axis, 2, -2), b);
                }
                b = elem.getBooleanAttribute("twoLayerTwists", true);
                for (int axis = 0; axis < 3; axis++) {
                    item.basicSetMoveSupported(new Move(3, axis, 3, 1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 3, -1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 3, 2), b);
                    item.basicSetMoveSupported(new Move(3, axis, 3, -2), b);
                    item.basicSetMoveSupported(new Move(3, axis, 6, 1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 6, -1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 6, 2), b);
                    item.basicSetMoveSupported(new Move(3, axis, 6, -2), b);
                }
                b = elem.getBooleanAttribute("sliceTwists", true);
                for (int axis = 0; axis < 3; axis++) {
                    item.basicSetMoveSupported(new Move(3, axis, 5, 1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 5, -1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 5, 2), b);
                    item.basicSetMoveSupported(new Move(3, axis, 5, -2), b);
                }
                b = elem.getBooleanAttribute("rotations", true);
                for (int axis = 0; axis < 3; axis++) {
                    item.basicSetMoveSupported(new Move(3, axis, 7, 1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 7, -1), b);
                    item.basicSetMoveSupported(new Move(3, axis, 7, 2), b);
                    item.basicSetMoveSupported(new Move(3, axis, 7, -2), b);
                }

                // Read legacy switches from CubeTwister 1.0
                b = elem.getBooleanAttribute("sequence", true);
                item.basicSetSupported(Symbol.GROUPING, b);
                b = elem.getBooleanAttribute("delimiter", true);
                item.basicSetSupported(Symbol.DELIMITER, b);
                b = elem.getBooleanAttribute("repetition", true);
                item.basicSetSupported(Symbol.REPETITION, b);
                b = elem.getBooleanAttribute("commutation", true);
                item.basicSetSupported(Symbol.COMMUTATION, b);
                b = elem.getBooleanAttribute("conjugation", true);
                item.basicSetSupported(Symbol.CONJUGATION, b);
                b = elem.getBooleanAttribute("permutations", true);
                item.basicSetSupported(Symbol.PERMUTATION, b);
                b = elem.getBooleanAttribute("rotation", true);
                item.basicSetSupported(Symbol.ROTATION, b);
                b = elem.getBooleanAttribute("inversion", true);
                item.basicSetSupported(Symbol.INVERSION, b);
                b = elem.getBooleanAttribute("reflection", true);
                item.basicSetSupported(Symbol.REFLECTION, b);
                b = elem.getBooleanAttribute("comments", true);
                item.basicSetSupported(Symbol.COMMENT, b);

                // Read legacy switches from CubeTwister 1.0
                item.basicSetSyntax(Symbol.COMMUTATION, elem.getAttribute("commutatorPosition", model.syntaxValueSet, "HEADER", false));
                item.basicSetSyntax(Symbol.CONJUGATION, elem.getAttribute("conjugatorPosition", model.syntaxValueSet, "HEADER", false));
                item.basicSetSyntax(Symbol.ROTATION, elem.getAttribute("rotatorPosition", model.syntaxValueSet, "HEADER", false));
                item.basicSetSyntax(Symbol.PERMUTATION, elem.getAttribute("permutationPosition", model.syntaxValueSet, "HEADER", false));
                item.basicSetSyntax(Symbol.INVERSION, elem.getAttribute("invertorPosition", model.syntaxValueSet, "PREFIX", false));
                item.basicSetSyntax(Symbol.REFLECTION, elem.getAttribute("reflectorPosition", model.syntaxValueSet, "PREFIX", false));
                item.basicSetSyntax(Symbol.REPETITION, elem.getAttribute("repetitorPosition", model.syntaxValueSet, "PREFIX", false));

                // Read child elements
                for (XMLElement elem2 : elem.iterableChildren()) {

                    String name = elem2.getName();
                    if ("Name".equals(name)) {
                        item.basicSetName(elem2.getContent());
                    } else if ("Description".equals(name)) {
                        item.basicSetDescription(elem2.getContent());
                    } else if ("Author".equals(name)) {
                        item.basicSetAuthor(elem2.getContent());
                    } else if ("Date".equals(name)) {
                        item.basicSetDate(elem2.getContent());

                    } else if ("Token".equals(name)) {
                        // Read legacy symbols
                        try {
                            Move ts = elem2.getAttribute("symbol", tvs, null, false);
                            if (item.getMoveToken(ts) != null) {
                                item.basicSetMoveToken(ts, item.getMoveToken(ts) + " " + Normalizer.normalize(elem2.getContent(), Normalizer.Form.NFC));
                            } else {
                                item.basicSetMoveToken(ts, Normalizer.normalize(elem2.getContent(), Normalizer.Form.NFC));
                            }
                        } catch (XMLParseException e) {
                            Symbol s = elem2.getAttribute("symbol", Symbol.getSymbolValueSet(), (String) null, false);
                            item.basicSetToken(s, Normalizer.normalize(elem2.getContent(), Normalizer.Form.NFC));
                        }
                    } else if ("Macro".equals(name)) {
                        MacroModel macro = new MacroModel();
                        macro.setIdentifier(Normalizer.normalize(elem2.getStringAttribute("identifier"), Normalizer.Form.NFC));

                        for (Object child : elem2.getChildren()) {
                            XMLElement elem3 = (XMLElement) child;
                            String name3 = elem3.getName();
                            if ("Source".equals(name3)) {
                                macro.setScript(Normalizer.normalize(elem3.getContent(), Normalizer.Form.NFC));
                            } else if ("Description".equals(name3)) {
                                macro.setDescription(elem3.getContent());
                            }
                            item.getMacroModels().add(macro);
                        }

                        // File format up to version 2 used <Construct>
                        // File format starting from version 3 uses <Statement>
                    } else if ("Statement".equals(name) || "Construct".equals(name)) {
                        Symbol s;
                        try {
                            s = elem2.getAttribute("symbol", Symbol.getSymbolValueSet(), null, false);
                        } catch (XMLParseException e) {
                            continue;
                        }
                        boolean isEnabled = elem2.getBooleanAttribute("enabled", true);
                        item.setSupported(s, isEnabled);
                        if (elem2.getAttribute("syntax") != null) {
                            Syntax syntax = elem2.getAttribute("syntax", Syntax.getSyntaxValueSet(), null, false);
                            if (syntax != null) {
                                item.setSyntax(s, syntax);
                            }
                        }
                        for (Object child : elem2.getChildren()) {
                            XMLElement elem3 = (XMLElement) child;
                            String name3 = elem3.getName();
                            if ("Token".equals(name3)) {
                                Symbol s2 = elem3.getAttribute("symbol", Symbol.getSymbolValueSet(), "move", false);
                                if (s2 == null || s2 == Symbol.MOVE) {
                                    int axis = elem3.getAttribute("axis", model.axisValueSet, "x", false);
                                    int angle = elem3.getIntAttribute("angle", -180, 180, 90) / 90;
                                    if (angle == 0) {
                                        angle = 1;
                                    }
                                    String layerListValue = elem3.getStringAttribute("layerList");
                                    int layerMask = layerListValue == null ? 0 : Move.toLayerMask(layerListValue);
                                    if (layerMask == 0) {
                                        layerMask = 1;
                                    }
                                    int reversedLayerMask = NotationModel.reverseLayerMask(layerMask, item.getLayerCount());

                                    // Add move token, if layerMask is 'useful'
                                    for (int usefulLayerMask : NotationModel.getUsefulLayers(item.getLayerCount())) {
                                        if (layerMask == usefulLayerMask
                                                || reversedLayerMask == usefulLayerMask) {
                                            Move ts = new Move(item.getLayerCount(), axis, layerMask, angle);
                                            item.setAllMoveTokens(ts, Normalizer.normalize(elem3.getContent(), Normalizer.Form.NFC));
                                            item.setMoveSupported(ts, isEnabled);
                                            break;
                                        }
                                    }
                                } else {
                                    item.setAllTokens(s2, Normalizer.normalize(elem3.getContent(), Normalizer.Form.NFC));
                                    item.setSupported(s2, isEnabled);
                                }
                            }
                        }
                    }
                }

            } else if ("Script".equals(elem.getName())) {

                // read Script elements
                attrValue = elem.getStringAttribute("id");
                ScriptModel item = null;
                if (attrValue != null && !attrValue.equals("")
                        && objects.get(attrValue) instanceof ScriptModel) {
                    item = (ScriptModel) objects.get(attrValue);
                }
                if (item == null) {
                    item = new ScriptModel();
                    if (attrValue != null && !"".equals(attrValue)) {
                        objects.put(attrValue, item);
                    }
                    if (parent == root.getChildAt(DocumentModel.SCRIPT_INDEX)) {
                        model.insertNodeInto(item, parent, index++);
                    } else if (parent.getParent() == root.getChildAt(DocumentModel.SCRIPT_INDEX)) {
                        model.insertNodeInto(item, (TreeNodeImpl) parent.getParent(), realScriptIndex++);
                    } else {
                        model.addTo(item, root.getChildAt(DocumentModel.SCRIPT_INDEX));
                    }
                }
                // File format up to version 2 used "generator=true/false"
                // File format up from version 3 uses "scriptType=generator/solver"
                boolean isGenerator;
                if (elem.getAttribute("generator") != null) {
                    isGenerator = elem.getBooleanAttribute("generator", true);
                } else {
                    isGenerator = elem.getAttribute("scriptType", model.scriptTypeValueSet, "generator", false);
                }
                item.setGenerator(isGenerator);

                Object refObj = objects.get(elem.getStringAttribute("cubeRef"));
                if (refObj instanceof CubeModel) {
                    item.setCubeModel((CubeModel) refObj);
                }
                refObj = objects.get(elem.getStringAttribute("notationRef"));
                if (refObj instanceof NotationModel) {
                    item.setNotationModel((NotationModel) refObj);
                }

                for (XMLElement elem2 : elem.iterableChildren()) {

                    String name = elem2.getName();
                    if ("Name".equals(name)) {
                        item.basicSetName(elem2.getContent());
                    } else if ("Description".equals(name)) {
                        item.basicSetDescription(elem2.getContent());
                    } else if ("Author".equals(name)) {
                        item.basicSetAuthor(elem2.getContent());
                    } else if ("Date".equals(name)) {
                        item.basicSetDate(elem2.getContent());
                    } else if ("Source".equals(name)) {
                        item.basicSetScript(elem2.getContent());
                    } else if ("Macro".equals(name)) {
                        MacroModel macro = new MacroModel();
                        macro.setIdentifier(Normalizer.normalize(elem2.getStringAttribute("identifier"), Normalizer.Form.NFC));

                        for (XMLElement elem3 : elem2.iterableChildren()) {
                            String name3 = elem3.getName();
                            if ("Source".equals(name3)) {
                                macro.basicSetScript(Normalizer.normalize(elem3.getContent(), Normalizer.Form.NFC));
                            } else if ("Description".equals(name3)) {
                                macro.basicSetDescription(elem3.getContent());
                            }
                        }
                        item.getMacroModels().add(macro);
                    }
                }

            } else if ("Text".equals(elem.getName())) {
                // read Text elements
                attrValue = elem.getStringAttribute("id");
                TextModel item = null;
                if (attrValue != null && !"".equals(attrValue)
                        && objects.get(attrValue) instanceof TextModel) {
                    item = (TextModel) objects.get(attrValue);
                }
                if (item == null) {
                    item = new TextModel();
                    if (attrValue != null && !"".equals(attrValue)) {
                        objects.put(attrValue, item);
                    }
                    if (parent == root.getChildAt(DocumentModel.TEXT_INDEX)) {
                        model.insertNodeInto(item, parent, index++);
                    } else if (parent.getParent() == root.getChildAt(DocumentModel.TEXT_INDEX)) {
                        model.insertNodeInto(item, (TreeNodeImpl) parent.getParent(), realTextIndex++);
                    } else {
                        model.addTo(item, root.getChildAt(DocumentModel.TEXT_INDEX));
                    }
                }

                for (XMLElement elem2 : elem.iterableChildren()) {

                    String name = elem2.getName();
                    if ("Title".equals(name)) {
                        item.setName(elem2.getContent());
                    } else if ("Body".equals(name)) {
                        item.setDescription(elem2.getContent());
                    } else if ("Author".equals(name)) {
                        item.setAuthor(elem2.getContent());
                    } else if ("Date".equals(name)) {
                        item.setDate(elem2.getContent());
                    }
                }
            }
        }
    }

}
