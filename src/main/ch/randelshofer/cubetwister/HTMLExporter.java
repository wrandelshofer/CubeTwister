package ch.randelshofer.cubetwister;
/*
 * @(#)HTMLExporter.java  2.3.1  2010-11-06
 * Copyright (c) 2007 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

import ch.randelshofer.cubetwister.doc.*;
import ch.randelshofer.gui.ProgressObserver;
import ch.randelshofer.gui.ProgressView;
import ch.randelshofer.gui.tree.TreeNodeImpl;
import ch.randelshofer.rubik.*;
import ch.randelshofer.rubik.parser.*;
import java.io.*;
import java.util.*;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Unpacker;
import java.util.zip.*;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

/**
 * HTMLExporter exports a CubeTwister document to a directory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>2.3 2010-02-08 Show more information about progress. Only export
 * applets which are needed by the document. Compress the resources.xml file.
 * <br>2.2.1 2009-01-25 Fixed NPE in method htmlencode.
 * <br>2.2 2009-01-08 If a file already exists, but does not have the
 * right case, delete it instead of just overwriting its contents.
 * <br>2.1 2009-01-07 Added support for script.scriptType attribute.
 * <br>2.0 2008-12-25 The "htmltemplates" resource is now compressed
 * as tar.bz instead of as .zip. The "htmltemplates" does now contain only
 * pack200-compressed java applets - the jar files of the applets need to
 * be generated from them.
 * <br>1.0 5. August 2007 Created.
 */
public class HTMLExporter implements Exporter {

    /**
     * Output directory of the exporter.
     * Note: zipfile and dir are mutually exclusive. If dir has a non-null
     * value, then zipFile must be null and vice versa.
     */
    private File dir;
    /**
     * Output zip file of the exporter.
     * Note: zipfile and dir are mutually exclusive. If zipFile has a non-null
     * value, then dir must be null and vice versa.
     */
    private File zipFile;
    /**
     * ZipOutputStream for the zipFile.
     */
    private ZipOutputStream zipOut;
    /**
     * The current document model.
     */
    private DocumentModel model;
    private String documentName;
    private ProgressObserver p;
    /**
     * File names and links need to be unique.
     * We create a unique id for every object and store it here.
     */
    private HashMap<EntityModel, String> ids;
    /** Determine all virtual cube kinds. */
    private HashSet<CubeKind> virtualCubeKinds;
    /** Determine all player cube kinds. */
    private HashSet<CubeKind> playerCubeKinds;

    private static class DataMap extends HashMap<String, String> {
    public final static long serialVersionUID=1L;

        @Override
        public String put(String key, String value) {
            return super.put(key, htmlencode(value));
        }

        public String putUnencoded(String key, String value) {
            return super.put(key, value);
        }
        public String putParameter(String key, String value) {
            return super.put(key, paramencode(value));
        }

        public String put(String key, int value) {
            return super.put(key, Integer.toString(value));
        }

        private String htmlencode(String str) {
            if (str != null) {
                str = str.replace("&", "&amp;");
                str = str.replace("<", "&lt;");
                str = str.replace(">", "&gt;");
                str = str.replace("\n", "<br>");
            }
            return str;
        }
        private String paramencode(String str) {
            if (str != null) {
                str = str.replace("\"", "&#034;");
            }
            return str;
        }
    }

    private static class StackEntry {

        public DataMap data = new DataMap();
        public CubeModel cube;
        public NotationModel notation;
        public ScriptModel script;
        public TextModel note;
    }
    /** Data stack.
     */
    private Stack<StackEntry> stack;
    /**
     * Output Stream for the current Entry.
     */
    private OutputStream entryOut;

    /** Creates a new instance. */
    public HTMLExporter() {
    }

    public void exportToDirectory(String documentName, DocumentModel model, File dir, ProgressObserver p) throws IOException {
        this.documentName = documentName;
        this.model = model;
        this.dir = dir;
        this.zipFile = null;
        this.p = p;
        init();
        processHTMLTemplates(p);
        new File(dir, "applets").mkdir();
        ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(new File(dir, "applets/resources.xml.zip"))));
        zout.setLevel(Deflater.BEST_COMPRESSION);
        try {
            //model.writeXML(new PrintWriter(new File(dir, "applets/resources.xml")));
            zout.putNextEntry(new ZipEntry("resources.xml"));
            PrintWriter pw = new PrintWriter(zout);
            model.writeXML(pw);
            pw.flush();
            zout.closeEntry();
        } finally {
            zout.close();
        }
        p.setProgress(p.getProgress() + 1);
    }

    public void exportToZipFile(String documentName, DocumentModel model, File zipFile, ProgressObserver p) throws IOException {
        this.documentName = documentName;
        this.model = model;
        this.dir = null;
        this.zipFile = zipFile;
        this.p = p;
        zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
        } finally {
            zipOut.close();
        }
        init();
        processHTMLTemplates(p);
    }

    private void putNextEntry(String filename) throws IOException {
        if (zipOut != null) {
            ZipEntry entry = new ZipEntry(filename);
            zipOut.putNextEntry(entry);
            entryOut = zipOut;
        } else {
            File f = new File(dir, filename);
            if (f.exists()
                    && f.getCanonicalFile().getName().equals(f.getName())) {
                // If the filename does not match, we have to delete the
                // file.
                f.delete();
            } else {
                f.getParentFile().mkdirs();
            }
            entryOut = new BufferedOutputStream(new FileOutputStream(f));
        }
    }

    private void closeEntry() throws IOException {
        if (zipOut != null) {
            zipOut.closeEntry();
        } else {
            entryOut.close();
        }
        entryOut = null;
    }

    private void init() throws IOException {
        // Initialize the progress observer
        // --------------------------------
        p.setMaximum(countHTMLTemplates(p) + 1);
        p.setIndeterminate(false);

        // Determine which virtual cube kinds need to be exported
        virtualCubeKinds = new HashSet<CubeKind>();
        for (EntityModel node : model.getCubes().getChildren()) {
            CubeModel cm = (CubeModel) node;
            virtualCubeKinds.add(cm.getKind());
        }
        playerCubeKinds = new HashSet<CubeKind>();
        for (EntityModel node : model.getCubes().getChildren()) {
            ScriptModel sm = (ScriptModel) node;
            playerCubeKinds.add(sm.getCubeModel().getKind());
        }


        // Create unique ID's for all objects
        // ----------------------------------
        ids = new HashMap<EntityModel, String>();

        // Holds all used ID's. To prevent name collisions on
        // file systems, which are not case sensitive, we store
        // only lower-case ID's in this set.
        HashSet<String> usedIDs = new HashSet<String>();

        for (EntityModel node : model.getRoot().preorderIterable()) {
            if (node instanceof InfoModel) {
                InfoModel im = (InfoModel) node;
                String baseId = toID(im.getName());
                String id = baseId;
                for (int i = 1; usedIDs.contains(id.toLowerCase()); i++) {
                    id = baseId + "_" + i;
                }
                usedIDs.add(id.toLowerCase());
                ids.put(im, id);
            }
        }

        // Create top level of data on placeholder stack
        stack = new Stack<StackEntry>();
        StackEntry entry = new StackEntry();
        stack.push(entry);

        // Put labels
        DataMap data = entry.data;
        data.put("software.title", "CubeTwister");
        data.put("software.copyright", "Copyright © by Werner Randelshofer. All Rights Reserved.");
        data.put("software.version", Main.getVersion());
        data.put("cubes.title", "Cubes");
        data.put("notations.title", "Notations");
        data.put("scripts.title", "Scripts");
        data.put("notes.title", "Notes");

        // Put information about the document
        data.put("document.name", documentName);
        data.put("document.copyright", "Copyright © by Werner Randelshofer. All Rights Reserved.");
        data.put("cube.count", model.getCubes().getChildCount());
        data.put("notation.count", model.getNotations().getChildCount());
        data.put("script.count", model.getScripts().getChildCount());
        data.put("note.count", model.getTexts().getChildCount());

        // Push default cube and default notation on the placeholder Stack
        putCubeData(model.getDefaultCube(), "");
        putNotationData(model.getDefaultNotation(model.getDefaultCube().getLayerCount()), "");
    }

    private void putCubeData(CubeModel m, String prefix) {
        StackEntry entry = stack.peek();
        DataMap data = entry.data;
        entry.cube = m;

        data.put(prefix + "cube", ids.get(m));
        data.put(prefix + "cube.name", m.getName());
        data.putParameter(prefix + "cube.nameParameter", m.getName());
        data.put(prefix + "cube.description", m.getDescription());
        data.put(prefix + "cube.author", m.getAuthor());
        data.put(prefix + "cube.date", m.getDate());

        data.put(prefix + "cube.alpha", m.getIntAlpha());
        data.put(prefix + "cube.beta", m.getIntBeta());
        //data.put("cube.", m.getColors());
        data.put(prefix + "cube.explode", m.getIntExplode());
        data.put(prefix + "cube.faceCount", m.getFaceCount());
        if (m.getFrontBgColor() != null) {
            data.put(prefix + "cube.frontBgColor", "#" + Integer.toHexString(m.getFrontBgColor().getRGB()));
        }
        //data.put("cube.", m.getFrontBgImage());
        data.put(prefix + "cube.scale", m.getIntScale());
        //data.put("cube.", m.getKind());
        data.put(prefix + "cube.partCount", m.getPartCount());
        //data.put("cube.", m.getPartFillColor());
        //data.put("cube.", m.getPartOutlineColor());
        //data.put("cube.", m.getParts());
        if (m.getRearBgColor() != null) {
            data.put(prefix + "cube.rearBgColor", "#" + Integer.toHexString(m.getRearBgColor().getRGB()));
        }
        //data.put("cube.", m.getRearBgImage());
        data.put(prefix + "cube.stickerCount", m.getStickerCount());
        //data.put("cube.", "#"+Integer.toHexString(m.getStickerFillColor().getRGB()));
        //data.put("cube.", "#"+Integer.toHexString(m.getStickerOutlineColor().getRGB()));
        //data.put("cube.", m.getStickersImage());

        data.put(prefix + "cube.kind", m.getKind().getId());
        data.put(prefix + "cube.shortKind", m.getKind().getAlternativeName(1));
        data.put(prefix + "cube.shorterKind", m.getKind().getAlternativeName(2));
    }

    private void putNotationData(NotationModel m, String prefix) {
        StackEntry entry = stack.peek();
        DataMap data = entry.data;
        entry.notation = m;

        data.put(prefix + "notation", ids.get(m));
        data.put(prefix + "notation.layerCount", m.getLayerCount());
        data.put(prefix + "notation.name", m.getName());
        data.putParameter(prefix + "notation.nameParameter", m.getName());
        data.put(prefix + "notation.description", m.getDescription());
        data.put(prefix + "notation.author", m.getAuthor());
        data.put(prefix + "notation.date", m.getDate());

        for (Symbol s : Symbol.values()) {
            if (s.isTerminalSymbol()) {
                if (m.getAllTokens(s) == null) {
                    data.put(prefix + "notation." + s.toString() + "", "");
                } else {
                    data.put(prefix + "notation." + s.toString() + "", m.getAllTokens(s));
                }
            }
        }

        data.put(prefix + "notation.permutation", Boolean.toString(m.isSupported(Symbol.PERMUTATION)));
        data.put(prefix + "notation.grouping", Boolean.toString(m.isSupported(Symbol.GROUPING)));
        data.put(prefix + "notation.repetition", Boolean.toString(m.isSupported(Symbol.REPETITION)));
        data.put(prefix + "notation.inversion", Boolean.toString(m.isSupported(Symbol.INVERSION)));
        data.put(prefix + "notation.reflection", Boolean.toString(m.isSupported(Symbol.REFLECTION)));
        data.put(prefix + "notation.conjugation", Boolean.toString(m.isSupported(Symbol.CONJUGATION)));
        data.put(prefix + "notation.commutation", Boolean.toString(m.isSupported(Symbol.COMMUTATION)));
        data.put(prefix + "notation.rotation", Boolean.toString(m.isSupported(Symbol.ROTATION)));
        data.put(prefix + "notation.delimiters", Boolean.toString(m.isSupported(Symbol.DELIMITER)));
        data.put(prefix + "notation.comment", Boolean.toString(m.isSupported(Symbol.COMMENT)));

        data.put(prefix + "notation.permutationSyntax", m.getSyntax(Symbol.PERMUTATION).toString());
        data.put(prefix + "notation.repetitionSyntax", m.getSyntax(Symbol.REPETITION).toString());
        data.put(prefix + "notation.inversionSyntax", m.getSyntax(Symbol.INVERSION).toString());
        data.put(prefix + "notation.reflectionSyntax", m.getSyntax(Symbol.REFLECTION).toString());
        data.put(prefix + "notation.conjugationSyntax", m.getSyntax(Symbol.CONJUGATION).toString());
        data.put(prefix + "notation.commutationSyntax", m.getSyntax(Symbol.COMMUTATION).toString());
        data.put(prefix + "notation.rotationSyntax", m.getSyntax(Symbol.ROTATION).toString());

        data.put(prefix + "notation.macro.count", m.getMacroModels().getChildCount());
    }

    private void putScriptData(ScriptModel m) {
        StackEntry entry = stack.peek();
        DataMap data = entry.data;
        entry.script = m;

        data.put("script", ids.get(m));
        data.put("script.script", m.getScript());
        data.putParameter("script.scriptParameter", m.getScript());

        data.put("script.name", m.getName());
        data.putParameter("script.nameParameter", m.getName());
        data.put("script.description", m.getDescription());
        data.put("script.author", m.getAuthor());
        data.put("script.date", m.getDate());

        Cube cube = m.getCubeModel().createCube();
        cube.reset();
        Node parsedScript;
        try {
            parsedScript = m.getParser().parse(m.getScript());
            parsedScript.applyTo(cube, false);
        } catch (IOException ex) {
            parsedScript = new SequenceNode(m.getNotationModel().getLayerCount());
        }
        data.put("script.permutation", Cubes.toPermutationString(cube, m.getNotationModel()));
        data.put("script.order", Cubes.getOrder(cube));
        data.put("script.visibleOrder", Cubes.getVisibleOrder(cube));
        data.put("script.faceTurnCount", parsedScript.getFaceTurnCount());
        data.put("script.layerTurnCount", parsedScript.getLayerTurnCount());
        data.put("script.blockTurnCount", parsedScript.getBlockTurnCount());
        data.put("script.quarterTurnCount", parsedScript.getQuarterTurnCount());
        data.put("script.scriptType", m.isGenerator() ? "generator" : "solver");

        putNotationData(m.getNotationModel(), "script.");
        putCubeData(m.getCubeModel(), "script.");
    }

    private void putNoteData(TextModel m) {
        StackEntry entry = stack.peek();
        DataMap data = entry.data;
        entry.note = m;

        data.put("note", ids.get(m));
        data.put("note.name", m.getName());
        data.put("note.description", m.getDescription());
        data.put("note.author", m.getAuthor());
        data.put("note.date", m.getDate());
    }

    private void processHTMLTemplates(ProgressObserver p) throws IOException {
        TarInputStream tin = null;
        try {
            InputStream in = getClass().getResourceAsStream("/htmltemplates.tar.bz");
            in.read(); // skip header chars;
            in.read();
            tin = new TarInputStream(new BZip2CompressorInputStream(in));

            for (TarArchiveEntry entry = tin.getNextEntry(); entry != null && !p.isCanceled();) {
                if (entry.isDirectory()) {
                    //System.out.println("dir:" + entry.getName());
                } else {
                    //System.out.println("file:"+entry.getName());

                    String name = entry.getName();
                    if (name.indexOf("${cube}") != -1) {
                        processCubeTemplate(name, toTokens(name, tin));
                    } else if (name.indexOf("${notation}") != -1) {
                        processNotationTemplate(name, toTokens(name, tin));
                    } else if (name.indexOf("${script}") != -1) {
                        processScriptTemplate(name, toTokens(name, tin));
                    } else if (name.indexOf("${note}") != -1) {
                        processNoteTemplate(name, toTokens(name, tin));
                    } else if (name.endsWith(".html")) {
                        processHTMLTemplate(name, toTokens(name, tin));
                    } else if (name.endsWith(".jar.pack.gz")) {
                        processPack200Template(name, tin);
                    } else {
                        processBinaryTemplate(name, tin);
                    }

                }

                entry = tin.getNextEntry();
            }


        } finally {
            if (tin != null) {
                tin.close();
            }
        }
    }

    private int countHTMLTemplates(ProgressObserver p) throws IOException {
        int count = 0;

        HashSet<CubeKind> cubeKinds = new HashSet<CubeKind>();
        EntityModel dmtn = model.getChild(model.getRoot(), DocumentModel.CUBE_INDEX);
        for (EntityModel node: dmtn.getChildren()) {
            CubeModel cm = (CubeModel) node;
            cubeKinds.add(cm.getKind());
        }
        int cubeCount = dmtn.getChildCount();
        dmtn = model.getChild(model.getRoot(), DocumentModel.NOTATION_INDEX);
        int notationCount = dmtn.getChildCount();
        dmtn = model.getChild(model.getRoot(), DocumentModel.SCRIPT_INDEX);
        int scriptCount = dmtn.getChildCount();
        dmtn = model.getChild(model.getRoot(), DocumentModel.TEXT_INDEX);
        int textCount = dmtn.getChildCount();

        TarInputStream tin = null;
        try {
            InputStream in = getClass().getResourceAsStream("/htmltemplates.tar.bz");
            in.read(); // skip header chars;
            in.read();
            tin = new TarInputStream(new BZip2CompressorInputStream(in));

            for (TarArchiveEntry entry = tin.getNextEntry(); entry != null && !p.isCanceled();) {
                if (entry.isDirectory()) {
                    //System.out.println("dir:" + entry.getName());
                } else {
                    //System.out.println("file:"+entry.getName());

                    String name = entry.getName();
                    if (name.indexOf("${cube}") != -1) {
                        count += cubeCount;
                    } else if (name.indexOf("${notation}") != -1) {
                        count += notationCount;
                    } else if (name.indexOf("${script}") != -1) {
                        count += scriptCount;
                    } else if (name.indexOf("${note}") != -1) {
                        count += textCount;
                    } else if (name.endsWith(".html")) {
                        count++;
                    } else if (name.endsWith(".jar.pack.gz")) {
                        count++;
                    } else {
                        count++;
                    }

                }

                entry = tin.getNextEntry();
            }


        } finally {
            if (tin != null) {
                tin.close();
            }
        }

        return count;
    }

    /**
     * Reads the content of the input stream into a list of token.
     * If a token start with "${", its a placeholder.
     * If a token doesn't start with "${" its a literal text.
     */
    private String[] toTokens(String filename, InputStream in) throws IOException {
        InputStreamReader reader = new InputStreamReader(in, "UTF8");
        StringBuilder buf = new StringBuilder();
        char[] cbuf = new char[256];
        int len;
        while (-1 != (len = reader.read(cbuf, 0, cbuf.length))) {
            buf.append(cbuf, 0, len);
        }

        int p1 = 0;
        int p2 = 0;
        LinkedList<String> tokens = new LinkedList<String>();
        while (true) {
            p1 = buf.indexOf("${", p2);
            if (p1 == -1) {
                break;
            }
            tokens.add(buf.substring(p2, p1));
            p2 = buf.indexOf("}", p1 + 2);
            if (p2 == -1) {
                throw new IOException("Closing curly bracket missing in " + filename + " after position " + p1);
            }
            p2++;
            tokens.add(buf.substring(p1, p2));
        }
        if (p2 != buf.length()) {
            tokens.add(buf.substring(p2));
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    /**
     * Processes a cube template.
     *
     * @param filenameTemplate The template for the filenameTemplate. It must contain
     * the substring ${cube}
     * @param in An input stream for reading the contents of the template.
     */
    private void processCubeTemplate(String filenameTemplate, String[] tokens) throws IOException {
        String placeholder = "${cube}";
        int plh = filenameTemplate.indexOf(placeholder);
        String filenamePrefix = (plh == 0) ? "" : filenameTemplate.substring(0, plh);
        String filenameSuffix = (plh >= filenameTemplate.length() - placeholder.length()) ? "" : filenameTemplate.substring(plh + placeholder.length());

        for (EntityModel node : model.getCubes().getChildren()) {
            p.setProgress(p.getProgress() + 1);
            CubeModel m = (CubeModel) node;
            stack.push(new StackEntry());
            putCubeData(m, "");
            p.setNote("Exporting "+ids.get(m) + filenameSuffix+" ...");
            putNextEntry(filenamePrefix + ids.get(m) + filenameSuffix);
            Writer w = new OutputStreamWriter(entryOut, "UTF8");
            writeData(w, tokens, 0, tokens.length);
            w.flush();
            stack.pop();
            closeEntry();
        }
    }

    /**
     * Converts a String, so that it only contains lower case ASCII characters.
     */
    private String toID(String str) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = Character.toLowerCase(str.charAt(i));
            if (Character.isJavaIdentifierPart(ch)) {
                buf.append(ch);
            } else if (Character.isWhitespace(ch)) {
                buf.append('_');
            }
        }
        return buf.toString();
    }

    /**
     * Processes a notation template.
     *
     * @param filenameTemplate The filenameTemplate of the template. The filenameTemplate must contain the substring
     * ${notation}
     * @param in An input stream for reading the contents of the template.
     */
    private void processNotationTemplate(String filenameTemplate, String[] tokens) throws IOException {
        String placeholder = "${notation}";
        int plh = filenameTemplate.indexOf(placeholder);
        String filenamePrefix = (plh == 0) ? "" : filenameTemplate.substring(0, plh);
        String filenameSuffix = (plh >= filenameTemplate.length() - placeholder.length()) ? "" : filenameTemplate.substring(plh + placeholder.length());

        for (EntityModel node : model.getNotations().getChildren()) {
            p.setProgress(p.getProgress() + 1);
            NotationModel m = (NotationModel) node;
            stack.push(new StackEntry());
            putNotationData(m, "");
            p.setNote("Exporting "+ids.get(m) + filenameSuffix+" ...");
            putNextEntry(filenamePrefix + ids.get(m) + filenameSuffix);
            Writer w = new OutputStreamWriter(entryOut, "UTF8");
            writeData(w, tokens, 0, tokens.length);
            w.flush();
            stack.pop();
            closeEntry();
        }
    }

    /**
     * Processes a script template.
     *
     * @param filename The filename of the template. The filename must contain the substring
     * ${script}
     * @param in An input stream for reading the contents of the template.
     */
    private void processScriptTemplate(String filename, String[] tokens) throws IOException {
        String placeholder = "${script}";
        int plh = filename.indexOf(placeholder);
        String filenamePrefix = (plh == 0) ? "" : filename.substring(0, plh);
        String filenameSuffix = (plh >= filename.length() - placeholder.length()) ? "" : filename.substring(plh + placeholder.length());

        for (EntityModel node : model.getScripts().getChildren()) {
            p.setProgress(p.getProgress() + 1);
            ScriptModel m = (ScriptModel) node;
            stack.push(new StackEntry());
            putScriptData(m);
            p.setNote("Exporting "+ids.get(m) + filenameSuffix+" ...");
            putNextEntry(filenamePrefix + ids.get(m) + filenameSuffix);
            Writer w = new OutputStreamWriter(entryOut, "UTF8");
            writeData(w, tokens, 0, tokens.length);
            w.flush();
            closeEntry();
            stack.pop();
        }
    }

    /**
     * Processes a note template.
     *
     * @param filename The filename of the template. The filename must contain the substring
     * ${note}
     * @param in An input stream for reading the contents of the template.
     */
    private void processNoteTemplate(String filename, String[] tokens) throws IOException {
        String placeholder = "${note}";
        int plh = filename.indexOf(placeholder);
        String filenamePrefix = (plh == 0) ? "" : filename.substring(0, plh);
        String filenameSuffix = (plh >= filename.length() - placeholder.length()) ? "" : filename.substring(plh + placeholder.length());

        for (EntityModel node : model.getTexts().getChildren()) {
            p.setProgress(p.getProgress() + 1);
            TextModel m = (TextModel) node;
            stack.push(new StackEntry());
            putNoteData(m);
            p.setNote("Exporting "+ids.get(m) + filenameSuffix+" ...");
            putNextEntry(filenamePrefix + ids.get(m) + filenameSuffix);
            Writer w = new OutputStreamWriter(entryOut, "UTF8");
            writeData(w, tokens, 0, tokens.length);
            w.flush();
            closeEntry();
            stack.pop();
        }
    }

    /**
     * Processes a HTML template.
     *
     * @param filename The filename of the template.
     * @param in An input stream for reading the contents of the template.
     */
    private void processHTMLTemplate(String filename, String[] tokens) throws IOException {
        p.setProgress(p.getProgress() + 1);
        putNextEntry(filename);
        Writer w = new OutputStreamWriter(entryOut, "UTF-8");
        writeData(w, tokens, 0, tokens.length);
        w.flush();
        closeEntry();
    }

    private String getValue(String placeholder) {
        for (int i = stack.size() - 1; i >= 0; i--) {
            HashMap<String, String> m = stack.get(i).data;
            if (m.containsKey(placeholder)) {
                return m.get(placeholder);
            }
        }
        return "-" + placeholder + "-";
    }

    private CubeModel getCurrentCube() {
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i).cube != null) {
                return stack.get(i).cube;
            }
        }
        return null;
    }

    private NotationModel getCurrentNotation() {
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i).notation != null) {
                return stack.get(i).notation;
            }
        }
        return null;
    }

    private ScriptModel getCurrentScript() {
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i).script != null) {
                return stack.get(i).script;
            }
        }
        return null;
    }

    private TextModel getCurrentNote() {
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i).note != null) {
                return stack.get(i).note;
            }
        }
        return null;
    }

    private void writeData(Writer w, String[] tokens, int start, int end) throws IOException {
        for (int i = start; i < end; i++) {
            String t = tokens[i];

            if (t.startsWith("${")) {
                if (t.startsWith("${FOR ")) {
                    i = writeForeachBlock(w, tokens, i);
                } else if (t.startsWith("${IF ")) {
                    i = writeConditionalBlock(w, tokens, i);
                } else {
                    w.write(getValue(t.substring(2, t.length() - 1)));
                }
            } else {
                w.write(t);
            }
        }
    }

    /**
     * Writes a block of data for each occurence of the specified object placeholder.
     * The block starts with a ${FOR placeholder} token and ends with a
     * ${ENDFOR placeholder} token, or a ${ENDFOR} token..
     *
     * @param w The Writer into which we write the output of the block.
     * @param tokens An array with tokens
     * @param start The index of the token which contains the
     * ${BEGIN placeholder} token.
     * @return Returns the index of the token which contains the
     * ${END placeholder} token.
     */
    private int writeForeachBlock(Writer w, String[] tokens, int start) throws IOException {
        // Search end token
        String endToken = "${FOR" + tokens[start].substring("${FOR ".length());
        int depth = 0;
        int end;
        for (end = start; end < tokens.length; end++) {
            String t = tokens[end];
            if (t.startsWith("${FOR ")) {
                depth++;
            }
            if (t.startsWith("${ENDFOR ") || t.equals("${ENDFOR}")) {
                depth--;

                if (depth == 0 /*&& t.equals(endToken)*/) {
                    break;
                }
            }
        }

        // Write block
        String placeholder = tokens[start].substring("${FOR ".length(), tokens[start].length() - 1).trim();
        if ("cube".equals(placeholder)) {
        for (EntityModel node : model.getCubes().getChildren()) {
                CubeModel m = (CubeModel) node;
                stack.push(new StackEntry());
                putCubeData(m, "");
                writeData(w, tokens, start + 1, end);
                stack.pop();
            }
        } else if ("notation".equals(placeholder)) {
        for (EntityModel node : model.getNotations().getChildren()) {
                NotationModel m = (NotationModel) node;
                stack.push(new StackEntry());
                putNotationData(m, "");
                writeData(w, tokens, start + 1, end);
                stack.pop();
            }
        } else if ("notation.movelayer".equals(placeholder)) {
            NotationModel m = getCurrentNotation();
            if (m != null) {
                NotationMovesTableModel table = new NotationMovesTableModel();
                table.setModel(m);
                for (int i = 0; i < table.getRowCount(); i++) {
                    if (table.getValueAt(i, 0) == Boolean.TRUE) {
                        stack.push(new StackEntry());
                        HashMap<String, String> data = stack.peek().data;
                        data.put("notation.move.layersymbol", table.getValueAt(i, 1).toString());
                        StringBuilder buf = new StringBuilder();
                        int layerIndex = 1;
                        for (char ch : table.getValueAt(i, 1).toString().toCharArray()) {
                            if (ch == '\u25cf') {
                                if (buf.length() != 0) {
                                    buf.append(',');
                                }
                                buf.append(Integer.toString(layerIndex));
                            }
                            layerIndex++;
                        }
                        data.put("notation.move.layers", buf.toString());
                        data.put("notation.move.angle", table.getValueAt(i, 2).toString());
                        data.put("notation.move.right", table.getValueAt(i, 3).toString());
                        data.put("notation.move.up", table.getValueAt(i, 4).toString());
                        data.put("notation.move.front", table.getValueAt(i, 5).toString());
                        data.put("notation.move.left", table.getValueAt(i, 6).toString());
                        data.put("notation.move.down", table.getValueAt(i, 7).toString());
                        data.put("notation.move.back", table.getValueAt(i, 8).toString());
                        writeData(w, tokens, start + 1, end);
                        stack.pop();
                    }
                }
            }

        } else if ("notation.macro".equals(placeholder)) {
            NotationModel m = getCurrentNotation();
            if (m != null) {
                for (EntityModel child: m.getMacroModels().getChildren()) {
                    MacroModel mm = (MacroModel) child;
                    stack.push(new StackEntry());
                    HashMap<String, String> data = stack.peek().data;
                    data.put("notation.macro.name", mm.getName());
                    data.put("notation.macro.identifier", mm.getIdentifier());
                    data.put("notation.macro.script", mm.getScript());
                    data.put("notation.macro.description", mm.getDescription());
                    data.put("notation.macro.date", mm.getDate());
                    data.put("notation.macro.author", mm.getAuthor());
                    writeData(w, tokens, start + 1, end);
                    stack.pop();
                }
            }

        } else if ("script".equals(placeholder)) {
            for (EntityModel child: model.getScripts().getChildren()) {
                ScriptModel m = (ScriptModel) child;
                stack.push(new StackEntry());
                putScriptData(m);
                writeData(w, tokens, start + 1, end);
                stack.pop();
            }
        } else if ("script.macro".equals(placeholder)) {
            ScriptModel m = getCurrentScript();
            if (m != null) {
                for (EntityModel child: m.getMacroModels().getChildren()) {
                    MacroModel mm = (MacroModel) child;
                    stack.push(new StackEntry());
                    HashMap<String, String> data = stack.peek().data;
                    data.put("script.macro.name", mm.getName());
                    data.put("script.macro.identifier", mm.getIdentifier());
                    data.put("script.macro.script", mm.getScript());
                    data.put("script.macro.description", mm.getDescription());
                    data.put("script.macro.date", mm.getDate());
                    data.put("script.macro.author", mm.getAuthor());
                    writeData(w, tokens, start + 1, end);
                    stack.pop();
                }
            }

        } else if ("note".equals(placeholder)) {
            for (EntityModel child: model.getTexts().getChildren()) {
                TextModel m = (TextModel) child;
                stack.push(new StackEntry());
                putNoteData(m);
                writeData(w, tokens, start + 1, end);
                stack.pop();
            }
        }

        return end;
    }

    /**
     * Writes a conditional block of data, if the specified value1 is met.
     *
     * The block starts with a ${IF placeholder}, ${IF value1=value2}, or
     * ${IF value1!=value2}. If value1 or value2 is enclosed in double quotes,
     * the value is treated as a literal value. If the value is not enclosed
     * in double quotes, the value is treated as a placeholder.
     * The block ends with ${ENDIF} or ${ENDIF placeholder}.
     * The block can contain ${ELSE} or ${ELSE placeholder}.
     *
     * @param w The Writer into which we write the output of the block.
     * @param tokens An array with tokens
     * @param start The index of the token which contains the ${IF ...} token.
     * @return Returns the index of the token which contains the ${ENDIF} token.
     */
    private int writeConditionalBlock(Writer w, String[] tokens, int start) throws IOException {
        // Search end token
        int depth = 0;
        int end;
        int elseblock = -1;
        for (end = start; end < tokens.length; end++) {
            String t = tokens[end];
            if (t.startsWith("${IF ")) {
                depth++;
            } else if (t.startsWith("${ENDIF ") || t.equals("${ENDIF}")) {
                depth--;
                if (depth == 0) {
                    break;
                }
            } else if (t.startsWith("${ELSE ") || t.equals("${ELSE}")) {
                if (depth == 0) {
                    elseblock = end;
                }
            }
        }

        // Write block
        String expression = tokens[start].substring("${IF ".length(), tokens[start].length() - 1);
        char operator;
        String value1;
        String value2;
        int pos;
        if (-1 != (pos = expression.indexOf("!="))) {
            operator = '!';
            value1 = expression.substring(0, pos);
            value2 = expression.substring(pos + 2);
        } else if (-1 != (pos = expression.indexOf('='))) {
            operator = '=';
            value1 = expression.substring(0, pos);
            value2 = expression.substring(pos + 1);
        } else {
            operator = '=';
            value2 = "\"true\"";
            value1 = expression;
        }
        value1 = value1.trim();
        value2 = value2.trim();
        if (value1.startsWith("\"")) {
            value1 = value1.substring(1, value1.length() - 1);
        } else if (!value1.matches("^[0-9]+$")) {
            value1 = getValue(value1);
        }
        if (value2.startsWith("\"")) {
            value2 = value2.substring(1, value2.length() - 1);
        } else if (!value2.matches("^[0-9]+$")) {
            value2 = getValue(value2);
        }

        if (value1 == null || value2 == null) {
            w.write("-" + tokens[start] + "-");
        } else if (value1.equals(value2) ^ operator == '!') {
            writeData(w, tokens, start + 1, end);
        } else if (elseblock != -1) {
            writeData(w, tokens, elseblock + 1, end);
        }
        return end;
    }

    /**
     * Processes a binary template.
     *
     * @param name The name of the template.
     * @param in An input stream for reading the contents of the template.
     */
    private void processBinaryTemplate(String filename, InputStream in) throws IOException {
        p.setProgress(p.getProgress() + 1);

        putNextEntry(filename);

        byte[] buf = new byte[256];
        int len;
        while (-1 != (len = in.read(buf, 0, buf.length))) {
            entryOut.write(buf, 0, len);
        }

        closeEntry();
    }

    /**
     * Processes a pack200 template, by writing it both as a pack200 file and
     * a Jar file to the output stream.
     *
     * @param name The name of the template. Must end with ".jar.pack.gz".
     * @param in An input stream for reading the contents of the template.
     */
    private void processPack200Template(String filename, InputStream in) throws IOException {
            p.setNote("Exporting "+filename+" ...");
        p.setProgress(p.getProgress() + 1);

        // Determine whether we can skip this file
        boolean skip = true;
        int pos1 = filename.lastIndexOf('/');
        int pos2 = filename.lastIndexOf("Player.jar.pack.gz");
        if (pos2 != -1) {
            for (CubeKind ck : playerCubeKinds) {
                if (ck.isNameOfKind(filename.substring(pos1 + 1, pos2))) {
                    skip = false;
                    break;
                }
            }
            if (skip)
                for (CubeKind ck : virtualCubeKinds) {
                    if (ck.isNameOfKind(filename.substring(pos1 + 1, pos2))) {
                        skip = false;
                        break;
                    }
                }
        }
        if (skip) {
            pos1 = filename.lastIndexOf("Virtual");
            pos2 = filename.lastIndexOf(".jar.pack.gz");
            if (pos2 != -1) {
                for (CubeKind ck : virtualCubeKinds) {
                    if (ck.isNameOfKind(filename.substring(pos1 + 7, pos2))) {
                        skip = false;
                        break;
                    }
                }
            }
        }
        if (skip) {
            return;
        }

        byte[] buf = new byte[1024];
        int len;

        // Write the pack200 file into the output and into a temporary buffer
        putNextEntry(filename);
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        while (-1 != (len = in.read(buf, 0, buf.length))) {
            tmp.write(buf, 0, len);
            entryOut.write(buf, 0, len);
        }
        closeEntry();
        tmp.close();

        // Uncompress the pack200 file from the temporary buffer into the output
        putNextEntry(filename.substring(0, filename.length() - 8));
        InputStream itmp = new GZIPInputStream(new ByteArrayInputStream(tmp.toByteArray()));
        JarOutputStream jout = new JarOutputStream(entryOut);
        jout.setLevel(Deflater.BEST_COMPRESSION);
        Unpacker unpacker = Pack200.newUnpacker();
        unpacker.unpack(itmp, jout);

        jout.finish();
        closeEntry();
    }
}
