/*
 * @(#)DocumentModel.java 3.9  2011-02-22
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.rubik.CubeKind;
import ch.randelshofer.gui.*;
import ch.randelshofer.gui.tree.*;
import ch.randelshofer.gui.datatransfer.*;
import ch.randelshofer.undo.*;
import ch.randelshofer.util.*;
import ch.randelshofer.rubik.parser.*;
import java.awt.Color;
import java.awt.Toolkit;

import java.awt.datatransfer.*;
import java.beans.*;
import java.io.*;
import java.text.Normalizer;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.undo.*;

import nanoxml.*;

/**
 * Holds a CubeTwister document.
 *
 * A document is a tree of EntityModels.
 *
 * DocumentModels must be self-contained, that is, the
 * the EntityModels in this DocumentModel must not have
 * a reference to an EntityModel in another DocumentModel.
 *
 * @author  Werner Randelshofer
 * @version 3.9 2011-01-22 Use TreeNodeImpl instead of TreeNodeImpl.
 * <br>3.8.1 2010-04-05 Removed dependencies to IBM Unicode normalizer.
 * <br>3.8 2010-04-05 Increased version number because the colors
 * are interpreted differently by V-Cube 7.
 * <br>3.7 2010-03-28 Normalize to NFC instead of NFKC.
 * <br>3.6 2010-01-09 Method createNodeAt tries harder to find a good
 * insertion location.
 * <br>3.5 2009-07-05 Skip move tokens with unsupported layerList
 * when reading an XML file.
 * <br>3.4 2009-01-12 Adapted to enhanced syntax support of notations.
 * Normalize notation identifiers and scripts to Unicode NFC when reading and
 * writing a document file.
 * <br>3.3 2008-12-25 Support reading of input streams into a preferred
 * parent + index.
 * <br>3.2.1 2008-12-21 XML attribute "scriptType" was not read from XML file.
 * <br>3.2 2008-04-28 Store twist duration and background colors in XML file.
 * <br>3.1 2007-06-16 Renamed XML attribute "move" to "twist".
 * <br>3.0 2007-06-10 Renamed XML element "Construct" to "Statement".
 * Renamed attribute "generator=true/false" to "scriptType=generator/solver".
 * This is now version 3 of the XML file.
 * <br>2.0 2006-01-08 Reworked to improve startup latency (use nanoxml
 * instead of DOM and parse XML file progressively).
 * <br>1.4 2004-04-03 New features to notation added. Current version
 * number of XML files is 2.
 * <br>1.3 2003-08-03 Version check for XML file added. Current version
 * number is 1.
 * <br>1.2 2003-06-20 Method getNodeActions added. Action translateInto
 * added.
 * <br>1.1.1 2002-12-21 Minor changes.
 * <br>1.1 2002-05-18 Decoupled attribute CubeModel.get/set/Shape
 * from XML Cube.kind attribute. ScriptModel has a new attribute "Generator".
 * Uses now property 'removable' from EntityModel to determine if a node
 * is removable. Class EventDispatcher replaced by SequentialDispatcher.
 * Methods isCloseable and close added.
 * <br>1.0 2001-10-04 Created.
 */
public class DocumentModel extends DefaultTreeModel
        implements MutableTreeModel, Undoable {
    private final static long serialVersionUID = 1L;
    private static final int DOCUMENT_VERSION = 9;
    private final static ConcurrentDispatcher threadPool = new ConcurrentDispatcher(Thread.MIN_PRIORITY, 1/*Runtime.getRuntime().availableProcessors()*/);
    public final static int CUBE_INDEX = 0;
    public final static int NOTATION_INDEX = 1;
    public final static int SCRIPT_INDEX = 2;
    public final static int TEXT_INDEX = 3;
    private final static String[] NODE_TYPES = {
        "Cube", "Notation", "Script", "Note"
    };
    private final static HashMap<String,Syntax> syntaxValueSet = new HashMap<String,Syntax>();

    static {
        syntaxValueSet.put("PREFIX", Syntax.PREFIX);
        syntaxValueSet.put("SUFFIX", Syntax.SUFFIX);
        syntaxValueSet.put("HEADER", Syntax.PRECIRCUMFIX);
        syntaxValueSet.put("TAIL", Syntax.POSTCIRCUMFIX);
    }
    private final static HashMap<String,Integer> axisValueSet = new HashMap<String,Integer>();

    static {
        axisValueSet.put("x", 0);
        axisValueSet.put("y", 1);
        axisValueSet.put("z", 2);
    }
    private final static HashMap<String,Boolean> scriptTypeValueSet = new HashMap<String,Boolean>();

    static {
        scriptTypeValueSet.put("generator", true);
        scriptTypeValueSet.put("solver", false);
    }
    private CubeModel defaultCube;
    /** Key = LayerCount, Value = NotationModel. */
    private HashMap<Integer,NotationModel> defaultNotation = new HashMap<Integer,NotationModel>();
    public static final String PROP_DEFAULT_CUBE = "DefaultCube";
    public static final String PROP_DEFAULT_NOTATION = "DefaultNotation";
    protected PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
  
    /**
     * The dispatcher is used for lengthy background operations.
     * @see #dispatch(Runnable)
     */
    private SequentialDispatcher dispatcher;
    /**
     * This set is used to determine which entities should
     * be written by method writeXML.
     *
     * @see #writeXML(OutputStream, Object[])
     */
    private Object[] writeMembersOnly;

    /**
     * Creates a new DocumentModel.
     */
    public DocumentModel() {
        super(new EntityModel(null, true));

        EntityModel node = getRoot();
        node.setUserObject(this);
        node.setRemovable(false);
        setAsksAllowsChildren(true);

        insertNodeInto(node = new EntityModel("Cubes", true), getRoot(), CUBE_INDEX);
        node.setRemovable(false);
        insertNodeInto(node = new EntityModel("Notations", true), getRoot(), NOTATION_INDEX);
        node.setRemovable(false);
        insertNodeInto(node = new EntityModel("Scripts", true), getRoot(), SCRIPT_INDEX);
        node.setRemovable(false);
        insertNodeInto(node = new EntityModel("Notes", true), getRoot(), TEXT_INDEX);
        node.setRemovable(false);
    }

    public void setDispatcher(SequentialDispatcher d) {
        dispatcher = d;
    }

    @Override
    public EntityModel getRoot() {
        return (EntityModel)super.getRoot();
    }

    /**
     * Dispatches a runnable on the worker thread of the document model.
     * <p>
     * Note that the worker thread executes the runnables sequentially.
     * <p>
     * Use this method to dispatch tasks which block the user interface
     * until the task has finished. i.e. Loading and Saving a document from
     * a file.
     */
    public void dispatch(Runnable runner) {
        if (dispatcher == null) {
            dispatcher = new SequentialDispatcher();
        }
        dispatcher.dispatch(runner);
    }

    /**
     * Dispatches a runnable on the global thread pool of the virtual machine.
     * The size of the thread pool is determined by the number of available
     * processors for the virtual machine.
     * <p>
     * Note that the worker thread executes the runnables concurrently.
     * <p>
     * Use this method to dispatch tasks which do not block the user interface
     * until the task has finished. i.e. Solving a scrambled cube.
     */
    public void dispatchSolver(Runnable runner) {
        threadPool.dispatch(runner);
    }

    public EntityModel getScripts() {
        return (EntityModel) root.getChildAt(SCRIPT_INDEX);
    }

    public EntityModel getTexts() {
        return (EntityModel) root.getChildAt(TEXT_INDEX);
    }

    public EntityModel getNotations() {
        return (EntityModel) root.getChildAt(NOTATION_INDEX);
    }

    public EntityModel getCubes() {
        return (EntityModel) root.getChildAt(CUBE_INDEX);
    }

    /**
     * Gets the default cube. The default cube is used
     * by the scripts unless they override it with their
     * own definition.
     */
    public CubeModel getDefaultCube() {
        return defaultCube;
    }

    /**
     * Sets the default cube. The default cube is used
     * by the scripts unless they override it with their
     * own definition.
     */
    public void setDefaultCube(CubeModel newValue) {
        CubeModel oldValue = defaultCube;
        defaultCube = newValue;
        propertySupport.firePropertyChange(PROP_DEFAULT_CUBE, oldValue, newValue);

        nodeChanged(oldValue);
        nodeChanged(newValue);

        UndoableEdit edit = new UndoableObjectEdit(this, "Default", oldValue, newValue) {
    private final static long serialVersionUID = 1L;

            @Override
            public void revert(Object a, Object b) {
                defaultCube = (CubeModel) b;
                propertySupport.firePropertyChange(PROP_DEFAULT_CUBE, a, b);
            }
        };
        fireUndoableEdit(edit);
    }

    /**
     * Gets the default notation. The default notation is used
     * by the scripts unless they override it with their
     * own definition.
     */
    public NotationModel getDefaultNotation(int layerCount) {
        return defaultNotation.get(layerCount);
    }

    /**
     * Sets the default notation. The default notation is used
     * by the scripts unless they override it with their
     * own definition.
     */
    public void setDefaultNotation(NotationModel value) {
        NotationModel oldValue = defaultNotation.get(value.getLayerCount());

        // make sure that the notation is only represented once as the default
        defaultNotation.values().remove(value);
        defaultNotation.put(new Integer(value.getLayerCount()), value);
        propertySupport.firePropertyChange(PROP_DEFAULT_NOTATION, oldValue, value);

        nodeChanged(oldValue);
        nodeChanged(value);

        UndoableEdit edit = new UndoableObjectEdit(this, "Default", oldValue, value) {
    private final static long serialVersionUID = 1L;

            @Override
            public void revert(Object a, Object b) {
                defaultNotation.put(new Integer(((NotationModel) b).getLayerCount()), (NotationModel) b);
                propertySupport.firePropertyChange(PROP_DEFAULT_NOTATION, a, b);
            }
        };
        fireUndoableEdit(edit);
    }
    /*
    public void itemDeleted(Object item) {
    Iterator i = cubes.iterator();
    while (i.hasNext()) {
    ((DescriptionModel) i.next()).itemDeleted(item);
    }
    i = notations.iterator();
    while (i.hasNext()) {
    ((DescriptionModel) i.next()).itemDeleted(item);
    }
    i = scripts.iterator();
    while (i.hasNext()) {
    ((DescriptionModel) i.next()).itemDeleted(item);
    }
    i = texts.iterator();
    while (i.hasNext()) {
    ((DescriptionModel) i.next()).itemDeleted(item);
    }
    }
     */

    /**
     * Writes the indicated entities of the DocumentModel into the
     * output stream.
     * For peak performance, the output stream should be buffered.
     */
    public void writeXML(OutputStream out, Object[] entities)
            throws IOException {
        writeMembersOnly = entities;
        PrintWriter w = new PrintWriter(new OutputStreamWriter(out, "UTF8"));
        writeXML(w);
        w.flush();
        writeMembersOnly = null;
    }

    public boolean isWriteMember(EntityModel m) {
        // FIXME Consider seriously to use an IdentityHashMap in JDK 1.4
        if (writeMembersOnly == null) {
            return true;
        }
        for (int i = 0; i < writeMembersOnly.length; i++) {
            if (writeMembersOnly[i] == m) {
                return true;
            }
        }
        return false;
    }

    @Override
    public EntityModel getChild(Object parent, int index) {
        return (EntityModel) super.getChild(parent, index); 
    }

    /**
     * Writes the contents of the DocumentModel into the output stream.
     * For peak performance, the output stream should be buffered.
     */
    public void writeXML(PrintWriter out)
            throws IOException {
        //try {
        // We keep track of all the objects we write and
        // we assign each an individual ID. The ID's are
        // used to represent object references in the file.
        // The hash map is required to map objects to
        // their ID's.
        // The hash map key is the object, the hash map
        // value is an Integer wrapper of it's id.
        //
        // NOTE: We write objects in a specific sequence
        // to avoid backward references: First we write
        // all cubes, which have no references. Then
        // we write the notations, which have no references.
        // Then we write the scripts, which may reference
        // cubes and notations. Then we write texts, which
        // may reference cubes, notations and scripts.
        int id = 0;
        HashMap<Object,Integer> objects = new HashMap<Object,Integer>();

        // Create the DOM XMLElement. Our Markup language
        // is CubeMarkup.
        XMLElement doc = new XMLElement(null, false, false);
        doc.setName("CubeMarkup");

        XMLElement docRoot = doc;
        docRoot.setAttribute("version", Integer.toString(DOCUMENT_VERSION));
        XMLElement elem, elem2, elem3, elem4;

        // ------------------------------------
        // Write Cubes
        for (EntityModel child : getChild(getRoot(), CUBE_INDEX).getChildren()) {
            CubeModel item = (CubeModel) child;
            if (!isWriteMember(item)) {
                continue;
            }


            // Assign an id to the object and put it into our HashMap.
            objects.put(item, new Integer(++id));

            // Create the document element.
            elem = doc.createElement("Cube");
            elem.setAttribute("id", "o" + (id));

            elem.setAttribute("kind", item.getKind().getAlternativeName(0));

            if (item == getDefaultCube()) {
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
            float[] colorComponents = new float[4];
            for (EntityModel child2 : item.getColors().getChildren()) {
                CubeColorModel color = (CubeColorModel) child2;

                // Assign an id to the object and put it into our HashMap.
                objects.put(color, new Integer(++id));

                // Create the document element
                elem2 = doc.createElement("Color");
                elem2.setAttribute("id", "o" + (id));
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
                elem2.setAttribute("fillColorRef", "o" + objects.get(part.getFillColorModel()));
                elem2.setAttribute("outlineColorRef", "o" + objects.get(part.getOutlineColorModel()));
                elem.addChild(elem2);
            }
            j = 0;
            for (EntityModel child2: item.getStickers().getChildren()) {
                CubeStickerModel sticker = (CubeStickerModel) child2;

                elem2 = doc.createElement("Sticker");
                elem2.setAttribute("index", Integer.toString(j++));
                elem2.setAttribute("visible", Boolean.toString(sticker.isVisible()));
                elem2.setAttribute("fillColorRef", "o" + objects.get(sticker.getFillColorModel()));
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

        // ------------------------------------
        // Write Notations
        String[] axisTable = {"x", "y", "z"};
        for (EntityModel child: getChild(getRoot(), NOTATION_INDEX).getChildren()) {
            NotationModel item = (NotationModel) child;
            if (!isWriteMember(item)) {
                continue;
            }

            // Assign an id to the object and put it into our HashMap.
            objects.put(item, new Integer(++id));

            // Create the document element.
            elem = doc.createElement("Notation");
            elem.setAttribute("id", "o" + (id));
            elem.setAttribute("layerCount", item.getLayerCount());


            if (item.isDefaultNotation()) {
                elem.setAttribute("default", "true");
            }

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

            LinkedList<Symbol> statements = new LinkedList<Symbol>();
            statements.add(Symbol.COMMENT);
            statements.addAll(Arrays.asList(Symbol.STATEMENT.getSubSymbols()));

            for (Symbol s : statements) {
                elem2 = doc.createElement("Statement");
                elem2.setAttribute("symbol", s.toString());
                if (s == Symbol.MOVE) {
                    elem3 = doc.createElement("Statement");
                    elem3.setAttribute("symbol", s.toString());
                    elem3.setAttribute("enabled", false);

                    // We sort move symbols to generate XML files with a
                    // consistent output which can be easily diffed.
                    ArrayList<Move> moveSymbols = new ArrayList<Move>(item.getAllMoveSymbols());
                    Collections.sort(moveSymbols);
                    for (Move ts : moveSymbols) {
                        String allTokens = (ts == null) ? null : item.getAllTwistTokens(ts);
                        if (allTokens != null && allTokens.length() > 0) {
                            elem4 = doc.createElement("Token");
                            elem4.setAttribute("axis", axisTable[ts.getAxis()]);
                            elem4.setAttribute("angle", ts.getAngle() * 90);
                            elem4.setAttribute("layerList", ts.getLayerList());
                            elem4.setContent(Normalizer.normalize(allTokens, Normalizer.Form.NFC));
                            if (item.isTwistSupported(ts)) {
                                elem2.addChild(elem4);
                            } else {
                                elem3.addChild(elem4);
                            }
                        }
                    }
                    // disabled twists
                    if (elem3.getChildren().size() > 0) {
                        elem.addChild(elem3);
                    }
                } else if (s.isTerminalSymbol()) {
                    if (!item.isSupported(s)) {
                        elem2.setAttribute("enabled", false);
                    }
                    if (item.getAllTokens(s) != null) {
                        if (item.getAllTokens(s) != null && item.getAllTokens(s).length() > 0) {
                            elem3 = doc.createElement("Token");
                            elem3.setAttribute("symbol", s.toString());
                            elem3.setContent(Normalizer.normalize(item.getAllTokens(s), Normalizer.Form.NFC));
                            elem2.addChild(elem3);
                        }
                    }
                } else {
                    if (!item.isSupported(s)) {
                        elem2.setAttribute("enabled", false);
                    }
                    if (item.getSyntax(s) != null) {
                        elem2.setAttribute("syntax", item.getSyntax(s).toString());
                    }
                    for (Symbol t : s.getSubSymbols()) {
                        if (item.getAllTokens(t) != null && item.getAllTokens(t).length() > 0) {
                            elem3 = doc.createElement("Token");
                            elem3.setAttribute("symbol", t.toString());
                            elem3.setContent(Normalizer.normalize(item.getAllTokens(t), Normalizer.Form.NFC));
                            elem2.addChild(elem3);
                        }
                    }
                }
                elem.addChild(elem2);
            }




            // Write macros
            for (EntityModel child2:item.getMacroModels().getChildren()) {
                MacroModel macro = (MacroModel) child2;
                elem2 = doc.createElement("Macro");
                elem2.setAttribute("identifier", Normalizer.normalize(macro.getIdentifier(), Normalizer.Form.NFC));

                if (macro.getScript() != null && macro.getScript().length() > 0) {
                    elem3 = doc.createElement("Source");
                    elem3.setContent(Normalizer.normalize(macro.getScript(), Normalizer.Form.NFC));
                    elem2.addChild(elem3);
                }
                if (macro.getDescription() != null && macro.getDescription().length() > 0) {
                    elem3 = doc.createElement("Description");
                    elem3.setContent(macro.getDescription());
                    elem2.addChild(elem3);
                }
                elem.addChild(elem2);
            }
            docRoot.addChild(elem);
        }

// ------------------------------------
// Write Scripts
        for (EntityModel child: getChild(getRoot(), SCRIPT_INDEX).getChildren()) {
            ScriptModel item = (ScriptModel) child;
            if (!isWriteMember(item)) {
                continue;
            }

            // Assign an id to the object and put it into our HashMap.
            objects.put(item, new Integer(++id));

            // Create the document element.
            elem = doc.createElement("Script");
            elem.setAttribute("id", "o" + (id));
            if (item.getNotationModel() != null) {
                elem.setAttribute("notationRef", "o" + objects.get(item.getNotationModel()));
            }
            if (item.getCubeModel() != null) {
                elem.setAttribute("cubeRef", "o" + objects.get(item.getCubeModel()));
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

                if (macro.getScript() != null && macro.getScript().length() > 0) {
                    elem3 = doc.createElement("Source");
                    elem3.setContent(Normalizer.normalize(macro.getScript(), Normalizer.Form.NFC));
                    elem2.addChild(elem3);
                }
                if (macro.getDescription() != null && macro.getDescription().length() > 0) {
                    elem3 = doc.createElement("Description");
                    elem3.setContent(macro.getDescription());
                    elem2.addChild(elem3);
                }
                elem.addChild(elem2);
            }
            docRoot.addChild(elem);
        }

// ------------------------------------
// Write Texts
        for (EntityModel child: getChild(getRoot(), TEXT_INDEX).getChildren()) {
            TextModel item = (TextModel) child;
            if (!isWriteMember(item)) {
                continue;
            }

            // Assign an id to the object and put it into our HashMap.
            objects.put(item, new Integer(++id));

            // Create the document element.
            elem = doc.createElement("Text");
            elem.setAttribute("id", "o" + (id));

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

// Write the document to the stream
        doc.print(out);
        out.flush();
        /*
        } catch (Exception e) {
        throw new IOException(e.toString());
        }*/
        javax.swing.text.GlyphView r;
    }
    /*
    private String extractText(org.w3c.dom.Node n) {
    if (n == null) return null;
    StringBuilder buf = new StringBuilder();
    extractText(n, buf);
    return buf.toString();
    }
    private void extractText(org.w3c.dom.Node n, StringBuilder buf) {
    if (n.getNodeValue() != null) buf.append(n.getNodeValue());
    NodeList children = n.getChildNodes();
    for (int i=0; i < children.getLength(); i++) {
    extractText(children.item(i), buf);
    }
    }*/
    /*
    private boolean getBooleanAttribute(XMLElement elem, String attribName) {
    org.w3c.dom.Node attrib = elem.getAttributes().getNamedItem(attribName);
    return attrib != null && attrib.getNodeValue().equals("true");
    }
    private int getIntAttribute(XMLElement elem, String attribName, int defaultValue, int min, int max) {
    org.w3c.dom.Node attrib = elem.getAttributes().getNamedItem(attribName);
    if (attrib == null) {
    return defaultValue;
    } else {
    try {
    int value = Integer.parseInt(attrib.getNodeValue());
    return Math.max(Math.min(value, max), min);
    } catch (NumberFormatException e) {
    return defaultValue;
    }
    }
    }
     */

    /**
     * Adds the contents of the input stream to the DocumentModel.
     * For peak performance, the input stream should be buffered.
     */
    public void addSerializedNode(InputStream in)
            throws IOException {
        insertSerializedNodeInto(in, (TreeNodeImpl) root, root.getChildCount());
    }

    /**
     * Adds the contents of the input stream to the DocumentModel.
     * For peak performance, the input stream should be buffered.
     */
    public void insertSerializedNodeInto(InputStream in, MutableTreeNode parent, int index)
            throws IOException {
        Reader r = new InputStreamReader(in, "UTF8");
        insertSerializedNodeInto(r, parent, index);
    }

    /**
     * Adds the contents of the input stream to the DocumentModel.
     * For peak performance, the input stream should be buffered.
     */
    public void insertSerializedNodeInto(Reader r, MutableTreeNode parent, int index)
            throws IOException {
        //in = new ByteFilterInputStream(in);
        try {
            // The DOM XMLElement.
            long start = System.currentTimeMillis();
            XMLElement doc = new XMLElement(null, false, false);
            //.newInstance().newDocumentBuilder().parse(in);
            doc.parseFromReader(r);
            long end1 = System.currentTimeMillis();

            insertXMLNodeInto(doc, parent, index);
            long end2 = System.currentTimeMillis();
            //System.out.println("DocumentModel.addSerializedNode buildDOM=" + (end1 - start) + " parseDOM=" + (end2 - end1));

        } catch (XMLParseException e) {
            throw new IOException(e);
        }

    }

    public void addXMLNode(XMLElement doc)
            throws IOException {
        insertXMLNodeInto(doc, (TreeNodeImpl) root, root.getChildCount());
    }

    public void insertXMLNodeInto(XMLElement doc, MutableTreeNode parent, int index)
            throws IOException {
        // We keep track of all the objects we read.
        // The hash map key is a String representation of
        // the object's id, the hash map value is the object.
        HashMap<String,Object> objects = new HashMap<String,Object>();

        String attrValue;

        // Read XMLElement root
        if (!doc.getName().equals("CubeMarkup")) {
            throw new IOException("Unsupported document type: " + doc);
        }
        attrValue = doc.getStringAttribute("version");
        int documentVersion = 0;
        if (attrValue != null && attrValue.length() > 0) {
            try {
                documentVersion = Integer.parseInt(attrValue);
            } catch (NumberFormatException e) {
                documentVersion = Integer.MAX_VALUE;
            }
        }
        if (documentVersion > DOCUMENT_VERSION) {
            throw new IOException("Unsupported document version: " + attrValue);
        }

        int realCubeIndex = root.getChildAt(CUBE_INDEX).getIndex(parent) + 1;
        int realNotationIndex = root.getChildAt(NOTATION_INDEX).getIndex(parent) + 1;
        int realScriptIndex = root.getChildAt(SCRIPT_INDEX).getIndex(parent) + 1;
        int realTextIndex = root.getChildAt(TEXT_INDEX).getIndex(parent) + 1;

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
                    if (parent == root.getChildAt(CUBE_INDEX)) {
                        insertNodeInto(item, parent, index++);
                    } else if (parent.getParent() == root.getChildAt(CUBE_INDEX)) {
                        insertNodeInto(item, (TreeNodeImpl) parent.getParent(), realCubeIndex++);
                    } else {
                        addTo(item, root.getChildAt(CUBE_INDEX));
                    }
                }
                if (elem.getBooleanAttribute("default", false)) {
                    setDefaultCube(item);
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
                    if (parent == root.getChildAt(NOTATION_INDEX)) {
                        insertNodeInto(item, parent, index++);
                    } else if (parent.getParent() == root.getChildAt(NOTATION_INDEX)) {
                        insertNodeInto(item, (TreeNodeImpl) parent.getParent(), realNotationIndex++);
                    } else {
                        addTo(item, root.getChildAt(NOTATION_INDEX));
                    }
                }

                // Read layer count
                item.setLayerCount(elem.getIntAttribute("layerCount", 2, 7, 3));
                HashMap<String,Move> tvs = MoveSymbols.getMoveValueSet(item.getLayerCount());

                // Must be done only after we have read the layer count!
                if (elem.getBooleanAttribute("default", false)) {
                    setDefaultNotation(item);
                }


                // Read legacy switches. These switches are only supported for
                // the 3x3 cube.
                boolean b;
                b = elem.getBooleanAttribute("quarterTurnTwists", true);
                for (int axis = 0; axis < 3; axis++) {
                    item.basicSetMoveSupported(new Move(axis, 1, 1), b);
                    item.basicSetMoveSupported(new Move(axis, 1, -1), b);
                    item.basicSetMoveSupported(new Move(axis, 4, 1), b);
                    item.basicSetMoveSupported(new Move(axis, 4, -1), b);
                }
                b = elem.getBooleanAttribute("halfTurnTwists", true);
                for (int axis = 0; axis < 3; axis++) {
                    item.basicSetMoveSupported(new Move(axis, 1, 2), b);
                    item.basicSetMoveSupported(new Move(axis, 1, -2), b);
                    item.basicSetMoveSupported(new Move(axis, 4, 2), b);
                    item.basicSetMoveSupported(new Move(axis, 4, -2), b);
                }
                b = elem.getBooleanAttribute("midLayerTwists", true);
                for (int axis = 0; axis < 3; axis++) {
                    item.basicSetMoveSupported(new Move(axis, 2, 1), b);
                    item.basicSetMoveSupported(new Move(axis, 2, -1), b);
                    item.basicSetMoveSupported(new Move(axis, 2, 2), b);
                    item.basicSetMoveSupported(new Move(axis, 2, -2), b);
                }
                b = elem.getBooleanAttribute("twoLayerTwists", true);
                for (int axis = 0; axis < 3; axis++) {
                    item.basicSetMoveSupported(new Move(axis, 3, 1), b);
                    item.basicSetMoveSupported(new Move(axis, 3, -1), b);
                    item.basicSetMoveSupported(new Move(axis, 3, 2), b);
                    item.basicSetMoveSupported(new Move(axis, 3, -2), b);
                    item.basicSetMoveSupported(new Move(axis, 6, 1), b);
                    item.basicSetMoveSupported(new Move(axis, 6, -1), b);
                    item.basicSetMoveSupported(new Move(axis, 6, 2), b);
                    item.basicSetMoveSupported(new Move(axis, 6, -2), b);
                }
                b = elem.getBooleanAttribute("sliceTwists", true);
                for (int axis = 0; axis < 3; axis++) {
                    item.basicSetMoveSupported(new Move(axis, 5, 1), b);
                    item.basicSetMoveSupported(new Move(axis, 5, -1), b);
                    item.basicSetMoveSupported(new Move(axis, 5, 2), b);
                    item.basicSetMoveSupported(new Move(axis, 5, -2), b);
                }
                b = elem.getBooleanAttribute("rotations", true);
                for (int axis = 0; axis < 3; axis++) {
                    item.basicSetMoveSupported(new Move(axis, 7, 1), b);
                    item.basicSetMoveSupported(new Move(axis, 7, -1), b);
                    item.basicSetMoveSupported(new Move(axis, 7, 2), b);
                    item.basicSetMoveSupported(new Move(axis, 7, -2), b);
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
                item.basicSetSyntax(Symbol.COMMUTATION, elem.getAttribute("commutatorPosition", syntaxValueSet, "HEADER", false));
                item.basicSetSyntax(Symbol.CONJUGATION, elem.getAttribute("conjugatorPosition", syntaxValueSet, "HEADER", false));
                item.basicSetSyntax(Symbol.ROTATION, elem.getAttribute("rotatorPosition", syntaxValueSet, "HEADER", false));
                item.basicSetSyntax(Symbol.PERMUTATION, elem.getAttribute("permutationPosition", syntaxValueSet, "HEADER", false));
                item.basicSetSyntax(Symbol.INVERSION, elem.getAttribute("invertorPosition", syntaxValueSet, "PREFIX", false));
                item.basicSetSyntax(Symbol.REFLECTION, elem.getAttribute("reflectorPosition", syntaxValueSet, "PREFIX", false));
                item.basicSetSyntax(Symbol.REPETITION, elem.getAttribute("repetitorPosition", syntaxValueSet, "PREFIX", false));

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
                                    int axis = elem3.getAttribute("axis", axisValueSet, "x", false);
                                    int angle = elem3.getIntAttribute("angle", -180, 180, 90) / 90;
                                    if (angle == 0) {
                                        angle = 1;
                                    }
                                    int layerMask = Move.toLayerMask(elem3.getStringAttribute("layerList"));
                                    if (layerMask == 0) {
                                        layerMask = 1;
                                    }
                                    int reversedLayerMask = NotationModel.reverseLayerMask(layerMask, item.getLayerCount());

                                    // Addd move token, if layerMask is 'useful'
                                    for (int usefulLayerMask : NotationModel.getUsefulLayers(item.getLayerCount())) {
                                        if (layerMask == usefulLayerMask
                                                || reversedLayerMask == usefulLayerMask) {
                                            Move ts = new Move(axis, layerMask, angle);
                                            item.setMoveToken(ts, Normalizer.normalize(elem3.getContent(), Normalizer.Form.NFC));
                                            item.setMoveSupported(ts, isEnabled);
                                            break;
                                        }
                                    }
                                } else {
                                    item.setToken(s2, Normalizer.normalize(elem3.getContent(), Normalizer.Form.NFC));
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
                    if (parent == root.getChildAt(SCRIPT_INDEX)) {
                        insertNodeInto(item, parent, index++);
                    } else if (parent.getParent() == root.getChildAt(SCRIPT_INDEX)) {
                        insertNodeInto(item, (TreeNodeImpl) parent.getParent(), realScriptIndex++);
                    } else {
                        addTo(item, root.getChildAt(SCRIPT_INDEX));
                    }
                }
                // File format up to version 2 used "generator=true/false"
                // File format up from version 3 uses "scriptType=generator/solver"
                boolean isGenerator;
                if (elem.getAttribute("generator") != null) {
                    isGenerator = elem.getBooleanAttribute("generator", true);
                } else {
                    isGenerator = elem.getAttribute("scriptType", scriptTypeValueSet, "generator", false);
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
                    if (parent == root.getChildAt(TEXT_INDEX)) {
                        insertNodeInto(item, parent, index++);
                    } else if (parent.getParent() == root.getChildAt(TEXT_INDEX)) {
                        insertNodeInto(item, (TreeNodeImpl) parent.getParent(), realTextIndex++);
                    } else {
                        addTo(item, root.getChildAt(TEXT_INDEX));
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

    /**
     * Invoked this to add newChild to parents children.
     * This will then message nodesWereInserted to create the appropriate event.
     * This is the preferred way to add children as it will create the appropriate event.
     */
    public void addTo(EntityModel newChild, TreeNode parent) {
        insertNodeInto(newChild, (MutableTreeNode)parent, parent.getChildCount());
    }

    /**
     * Invoked this to add newChild to parents children.
     * This will then message nodesWereInserted to create the appropriate event.
     * This is the preferred way to add children as it will create the appropriate event.
     */
    @Override
    public void insertNodeInto(final MutableTreeNode newChild, final MutableTreeNode parent, final int index) {
        CompositeEdit ce = new CompositeEdit();
        fireUndoableEdit(ce);

        fireUndoableEdit(
                new AbstractUndoableEdit() {
    private final static long serialVersionUID = 1L;

                    @Override
                    public void redo() {
                        super.redo();
                        insertNodeIntoQuiet((TreeNodeImpl)newChild, (TreeNodeImpl)parent, index);
                    }

                    @Override
                    public String getPresentationName() {
                        return "Insert";
                    }
                });

        insertNodeIntoQuiet((TreeNodeImpl)newChild, (TreeNodeImpl)parent, index);

        fireUndoableEdit(
                new AbstractUndoableEdit() {
    private final static long serialVersionUID = 1L;

                    @Override
                    public void undo() {
                        super.undo();
                        removeNodeFromParentQuiet((TreeNodeImpl)newChild);
                    }

                    @Override
                    public String getPresentationName() {
                        return "Insert";
                    }
                });
        fireUndoableEdit(ce);
    }

    protected void insertNodeIntoQuiet(MutableTreeNode newChild, TreeNode parent, int index) {
        super.insertNodeInto(newChild, (MutableTreeNode)parent, index);
    }

    protected void removeNodeFromParentQuiet(MutableTreeNode node) {
        super.removeNodeFromParent(node);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Returns wether the specified node may be removed.
     *
     * @param   node   a node from the tree, obtained from this data source.
     */
    @Override
    public boolean isNodeRemovable(MutableTreeNode node) {
        // Direct children of the root node are
        // the Cubes, Notations, Scripts and the Text
        // folder. It does not make sense to rename
        // these nodes.
        //return node != root && ((TreeNode) node).getParent() != root;
        return ((EntityModel) node).isRemovable();
    }

    /**
     * Message this to remove node from its parent.
     * @exception IllegalStateException if the node is not removable.
     */
    @Override
    public void removeNodeFromParent(final MutableTreeNode node) {
        if (isNodeRemovable(node)) {
            CompositeEdit ce = new CompositeEdit();
            fireUndoableEdit(ce);

            fireUndoableEdit(
                    new AbstractUndoableEdit() {
    private final static long serialVersionUID = 1L;
                        @Override
                        public void redo() {
                            super.redo();
                            removeNodeFromParentQuiet(node);
                        }

                @Override
                        public String getPresentationName() {
                            return "Remove";
                        }
                    });

            final TreeNode parent = node.getParent();
            final int index = parent.getIndex(node);

            removeNodeFromParentQuiet(node);

            fireUndoableEdit(
                    new AbstractUndoableEdit() {
    private final static long serialVersionUID = 1L;
                @Override
                        public void undo() {
                            super.undo();
                            insertNodeIntoQuiet(node, parent, index);
                        }

                @Override
                        public String getPresentationName() {
                            return "Remove";
                        }
                    });
            fireUndoableEdit(ce);
        } else {
            throw new IllegalStateException("org.w3c.dom.Node can not be removed.");
        }
    }

    /**
     * Invoke this to insert a new child at location index in parents children.
     * This will then message nodesWereInserted to create the appropriate
     * event. This is the preferred way to add children as it will create the
     * appropriate event.
     *
     * @param   newNodeType  the type of the new child to be created, obtained
     *                    from getCreatableChildren
     * @param   parent     a node from the tree, obtained from this data source.
     * @param   index      index of the child.
     * @exception   IllegalStateException if the parent node does not allow children.
     */
    public TreePath createNodeAt(Object newNodeType, MutableTreeNode parent, int index) throws IllegalStateException {
        // We create the nodes where we want them to be created!
        EntityModel realParent = null;
        int realIndex;
        EntityModel newChild = null;
        int type;

        for (type = 0; type < NODE_TYPES.length; type++) {
            if (newNodeType == NODE_TYPES[type]) {
                realParent = (EntityModel) root.getChildAt(type);
                break;
            }
        }
        realIndex = (parent == realParent) ? index : //
                (realParent == parent.getParent()) ? realParent.getIndex(parent) + 1 ://
                realParent.getChildCount();
        switch (type) {
            case CUBE_INDEX:
                newChild = new CubeModel();
                ((CubeModel) newChild).setName("unnamed Cube");
                break;
            case NOTATION_INDEX:
                newChild = new NotationModel();
                ((NotationModel) newChild).setName("unnamed Notation");
                break;
            case SCRIPT_INDEX:
                newChild = new ScriptModel();
                ((ScriptModel) newChild).setName("unnamed Script");
                break;
            case TEXT_INDEX:
                newChild = new TextModel();
                ((TextModel) newChild).setName("unnamed Note");
                break;
        }

        insertNodeInto(newChild, realParent, realIndex);

        LinkedList<Object> path = new LinkedList<Object>();
        for (EntityModel n = newChild; n != null; n = n.getParent()) {
            path.addFirst(n);
        }
        return new TreePath(path.toArray());
    }

    /**
     * Returns wether the specified node may be renamed.
     *
     * @param   node   a node from the tree, obtained from this data source.
     */
    public boolean isNodeEditable(MutableTreeNode node) {
        // Direct children of the root node are
        // the Cubes, Notations, Scripts and the Text
        // folder. It does not make sense to rename
        // these nodes.
        return node != root && node.getParent() != root;
    }

    /**
     * Returns the types of children that may be created at this node.
     *
     * @param   parent   a node from the tree, obtained from this data source.
     * @return  an array of objects that specify a child type that may be
     *         added to the node. Returns an empty array for nodes
     *         that cannot have additional children.
     */
    @Override
    public Object[] getCreatableNodeTypes(Object parent) {
        return NODE_TYPES;
    }

    @Override
    public Object getCreatableNodeType(Object parent) {
        if (isLeaf(parent)) {
            return null;
        }

        EntityModel node = (EntityModel) parent;
        TreeNode[] path = node.getPath();
        if (path.length <= 1) {
            return "Script";
        }
        if (path[1] == getCubes()) {
            return "Cube";
        } else if (path[1] == getNotations()) {
            return "Notation";
        } else if (path[1] == getScripts()) {
            return "Script";
        } else if (path[1] == getTexts()) {
            return "Note";
        } else {
            return null;
        }
    }

    /**
     * Returns wether the specified node may be inserted.
     *
     * @param   parent   a node from the tree, obtained from this data source.
     */
    public boolean isNodeAddable(MutableTreeNode parent, int index) {
        return true;
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof InfoModel;
    }

    /**
     * Removes an UndoableEditListener.
     */
    @Override
    public void removeUndoableEditListener(UndoableEditListener l) {
        listenerList.remove(UndoableEditListener.class, l);
    }

    /**
     * Adds an UndoableEditListener.
     */
    @Override
    public void addUndoableEditListener(UndoableEditListener l) {
        listenerList.add(UndoableEditListener.class, l);
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
    public void fireUndoableEdit(UndoableEdit edit) {
        UndoableEditEvent evt = null;

        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == UndoableEditListener.class) {
                // Lazily create the event
                if (evt == null) {
                    evt = new UndoableEditEvent(this, edit);
                }
                ((UndoableEditListener) listeners[i + 1]).undoableEditHappened(evt);
            }
        }
    }

    /**
     * Copies the indicated nodes to the clipboard.
     * @param   nodes The nodes to be copied.
     * @exception   IllegalStateException if the nodes can not be removed.
     */
    public Transferable exportTransferable(MutableTreeNode[] nodes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            writeXML(out, nodes);
            out.close();
        } catch (IOException e) {
            InternalError error = new InternalError(e.getMessage());
            error.initCause(e);
            throw error;
        }
        XMLTransferable transfer = new XMLTransferable(out.toByteArray(), "text/xml", "Cube Twister Markup");
        return transfer;
    }

    @Override
    public List<TreePath> importTransferable(Transferable transfer, int action, MutableTreeNode parent, int index)
            throws UnsupportedFlavorException, IOException {
LinkedList<TreePath> importedPaths=new LinkedList<TreePath>();
        DataFlavor flavor = new DataFlavor("text/xml", "Cube Twister Markup");
        //Transferable transfer = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
        if (transfer.isDataFlavorSupported(flavor)) {
            InputStream in = null;
            try {
                in = (InputStream) transfer.getTransferData(flavor);
                insertSerializedNodeInto(in, parent, index);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
importedPaths.add(new TreePath(((TreeNodeImpl)parent).getPath()).pathByAddingChild(getChild(parent,index)));
            return importedPaths;
        } else if (transfer.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            Reader in = null;
            try {
                in = new StringReader((String) transfer.getTransferData(DataFlavor.stringFlavor));
                insertSerializedNodeInto(in, parent, index);
            } finally {
                if (in != null) {
                    in.close();
                }
            }

importedPaths.add(new TreePath(((TreeNodeImpl)parent).getPath()).pathByAddingChild(getChild(parent,index)));
            return importedPaths;
        } else {
            Toolkit.getDefaultToolkit().beep();
            return importedPaths;
        }
    }

    public boolean isImportable(DataFlavor[] flavors, int action, MutableTreeNode parent, int index) {
        return parent == getCubes() || parent == getNotations() || parent == getScripts() || parent == getTexts();
    }

    /**
     * Returns true, if no background processes are working on the
     * model.
     */
    public boolean isCloseable() {
        for (EntityModel node: getScripts().getChildren()) {
            ScriptModel item = (ScriptModel) node;
            if (item.getProgressView() != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Shuts down all background processes.
     */
    public void close() {
        for (EntityModel node: getScripts().getChildren()) {
            ScriptModel item = (ScriptModel) node;
            ProgressObserver p = item.getProgressView();
            if (p != null) {
                p.cancel();
            }
        }
    }

    public Action[] getNodeActions(final MutableTreeNode[] nodes) {
        LinkedList<Action> actions = new LinkedList<Action>();
        boolean allNodesAreScripts = true;
        for (int i = 0; i < nodes.length; i++) {
            if (!(nodes[i] instanceof ScriptModel)) {
                allNodesAreScripts = false;
            }
        }
        return actions.toArray(new Action[actions.size()]);
    }

    public void dispose() {
        TreeNode[] children = new TreeNode[root.getChildCount()];
        int[] childIndices = new int[root.getChildCount()];
        for (int i = 0; i < children.length; i++) {
            EntityModel entity = (EntityModel) root.getChildAt(i);
            entity.dispose();
            children[i] = root.getChildAt(i);
            childIndices[i] = i;
        }
        fireTreeStructureChanged(this, new Object[]{root}, childIndices, children);
        defaultCube = null;
        defaultNotation = null;
        propertySupport = null;
        listenerList = null;
        dispatcher = null;

        ((EntityModel) root).setUserObject(null);
        root = null;
    }
    /*
    public void finalize() {
    System.out.println("DocumentModel.finalize " + this);
    }*/
}
