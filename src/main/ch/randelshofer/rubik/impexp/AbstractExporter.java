/* @(#)AbstractExporter.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.rubik.impexp;

import ch.randelshofer.cubetwister.doc.*;
import ch.randelshofer.rubik.impexp.*;
import ch.randelshofer.gui.*;
import ch.randelshofer.gui.tree.*;
import ch.randelshofer.util.*;
import ch.randelshofer.io.*;
import ch.randelshofer.rubik.*;
import ch.randelshofer.rubik.parser.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
/**
 * AbstractExporter.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class AbstractExporter extends JPanel implements Exporter {
    private final static long serialVersionUID = 1L;
    protected DocumentModel documentModel;

    @Override
    public JComponent getComponent() {
        return this;
    }
    
    protected static class ScriptRecord {
        public String name;
        public String notation;
        public String script;
        public String[][] macros;
        public String author;
        public String date;
        public boolean isParsed;
        public int ltm;
        public int ftm;
        public int qtm;
        public int visualOrder;
        public int realOrder;
        public String visualPermutation;
        public String realPermutation;
        public String description;
    }
    
    /** Creates new form. */
    public AbstractExporter() {
        initComponents();
    }
    
    @Override
    public void setDocumentModel(DocumentModel documentModel) {
        this.documentModel = documentModel;
    }
    
    /**
     * Creates script records.
     */
    public ArrayList<ScriptRecord> createScriptRecords(ProgressObserver p) throws IOException {
        p.setMaximum(documentModel.getScripts().getChildCount());
        int progress = 0;
        RubiksCube cube = new RubiksCube();
        
        ArrayList<ScriptRecord> records = new ArrayList<ScriptRecord>(documentModel.getScripts().getChildCount());
        
        for (Enumeration i = documentModel.getScripts().children(); i.hasMoreElements(); ) {
            ScriptModel item = (ScriptModel) i.nextElement();
            
            p.setProgress(++progress);
            p.setNote("Exporting \""+item.getName()+"\"");
            
            ScriptRecord sr = new ScriptRecord();
            
            
            sr.name = item.getName();
            sr.description = item.getDescription();
            sr.author = item.getAuthor();
            sr.date = item.getDate();
            sr.notation = item.getNotationModel().getName();
            sr.script = item.getScript();
            
            // Add synthetic fields to record
            ScriptParser parser = item.getParser();
            SequenceNode parsedScript = item.getParsedScript();
            sr.isParsed = parsedScript != null;
            if (sr.isParsed) {
                cube.reset();
                parsedScript.applyTo(cube, false);
                sr.ltm = parsedScript.getLayerTurnCount();
                sr.ftm = parsedScript.getFaceTurnCount();
                sr.qtm = parsedScript.getQuarterTurnCount();
                sr.visualOrder = Cubes.getVisibleOrder(cube);
                sr.realOrder = Cubes.getOrder(cube);
                sr.visualPermutation = Cubes.toVisualPermutationString(cube, parser.getNotation());
                sr.realPermutation = Cubes.toPermutationString(cube, parser.getNotation());
            }
            
            // Add macros to data record
            sr.macros = new String[item.getMacroModels().getChildCount()][2];
            int k = 0;
            for (Enumeration j = item.getMacroModels().children(); j.hasMoreElements(); ) {
                MacroModel macro = (MacroModel) j.nextElement();
                sr.macros[k][0] = macro.getIdentifier();
                sr.macros[k++][1] = macro.getScript();
            }
            
            records.add(sr);
        }
        return records;
    }
    
    /**
     * Creates translated script records.
     */
    public ArrayList createScriptRecords(NotationModel translator, ProgressObserver p) throws IOException {
        p.setMaximum(documentModel.getScripts().getChildCount()+1);
        int progress = 0;
        
        RubiksCube cube = new RubiksCube();
        ArrayList<ScriptRecord> records = new ArrayList<ScriptRecord>(documentModel.getScripts().getChildCount());
        
        for (Enumeration i = documentModel.getScripts().children(); i.hasMoreElements(); ) {
            ScriptModel item = (ScriptModel) i.nextElement();
            
            p.setProgress(++progress);
            p.setNote("Exporting \""+item.getName()+"\"");
            
            ScriptRecord sr = new ScriptRecord();
            
            
            sr.name = item.getName();
            sr.description = item.getDescription();
            sr.author = item.getAuthor();
            sr.date = item.getDate();
            sr.notation = translator.getName();
            
            // Add synthetic fields to record
            ScriptParser parser = item.getParser();
            SequenceNode parsedScript = item.getParsedScript();
            sr.isParsed = parsedScript != null;
            sr.macros = new String[item.getMacroModels().getChildCount()][2];
            if (sr.isParsed) {
                
                // Translate
                // -------------------------------------------------
                @SuppressWarnings("unchecked")
                List<MacroNode> macros = (List)item.getMacroModels().getChildren();
                ScriptParser oldParser = item.getNotationModel().getParser(macros);
                
                // Parse the local macros
                ArrayList<MacroNode> localMacros = new ArrayList<MacroNode>();
                for (Iterator j = macros.iterator(); j.hasNext(); ) {
                    MacroModel macro = (MacroModel) j.next();
                    localMacros.add(new MacroNode(translator.getLayerCount(),macro.getIdentifier(),
                    macro.getScript(), 0, 0));
                }
                
                ScriptParser newParser = translator.getParser(localMacros);
                
                // Translate the Script 
                if (true) throw new InternalError("not implemented");
                /*
                if (oldParser.isTwistSupported() && ! newParser.isTwistSupported()) {
                    SequenceNode node = oldParser.parse(item.getScript());
                    cube.reset();
                    node.applySubtreeTo(cube, false);
                    sr.script = cube.toPermutationString(newParser);
                } else {
                    sr.script = newParser.toString(oldParser.parse(item.getScript()), parsedMacros);
                }
                
                // Translate the macros
                ArrayList translatedMacros = new ArrayList(macros.size());
                sr.macros = new String[macros.size()][2];
                for (int j = 0; j < macros.size(); j++) {
                    MacroModel macro = (MacroModel) macros.get(j);
                    sr.macros[j][0] = macro.getIdentifier();
                    sr.macros[j][1]  =
                    newParser.toString(
                    (SequenceNode) ((SequenceNode) parsedMacros.get(macro.getIdentifier())).getChildAt(0),
                    parsedMacros
                    );
                    MacroModel translatedMacro = new MacroModel();
                    translatedMacro.setIdentifier(sr.macros[j][0]);
                    translatedMacro.setScript(sr.macros[j][1]);
                    translatedMacros.add(translatedMacro);
                }
                newParser = translator.getParser(translatedMacros);
                parsedScript = newParser.parse(sr.script);
                 */
                cube.reset();
                parsedScript.applyTo(cube, false);
                sr.ltm = parsedScript.getLayerTurnCount();
                sr.ftm = parsedScript.getFaceTurnCount();
                sr.qtm = parsedScript.getQuarterTurnCount();
                sr.visualOrder = Cubes.getVisibleOrder(cube);
                sr.realOrder = Cubes.getOrder(cube);
                new InternalError("not implemented");
                /*
                sr.visualPermutation = Cubes.toVisualPermutationString(cube, newParser);
                sr.realPermutation = Cubes.toPermutationString(cube, newParser);
                 */
                /*
                java.util.List macros = new TreeNodeCollection(item.getMacroModels());
                ScriptParser oldParser = item.getNotationModel().getParser(macros);
                 
                // Parse the local macros
                HashMap parsedMacros = new HashMap();
                for (int j=0; j < macros.size(); j++) {
                    MacroModel macro = (MacroModel) macros.get(j);
                    MacroNode macroNode = new MacroNode(
                    new StringTokenizer(macro.getIdentifier()).nextToken(),
                    macro.getScript(), 0, 0
                    );
                    macroNode.expand(oldParser);
                    StringTokenizer t = new StringTokenizer(macro.getIdentifier());
                    while (t.hasMoreTokens()) {
                        parsedMacros.put(t.nextToken(), macroNode);
                    }
                }
                 
                ScriptParser newParser = translator.getParser(parsedMacros);
                 
                // Translate the Script and add it to the data record
                parsedScript = oldParser.parse(item.getScript());
                if (oldParser.isTwistSupported() && ! newParser.isTwistSupported()) {
                    parsedScript.applySubtreeTo(cube, false);
                    sr.script = cube.toPermutationString(newParser);
                } else {
                    sr.script = newParser.toString(parsedScript, parsedMacros);
                }
                 
                // Translate the macros and add them to the data record
                 
                sr.macros = new String[macros.size()][2];
                ArrayList translatedMacros = new ArrayList(macros.size());
                for (int k=0; k < macros.size(); k++) {
                    MacroModel macro = (MacroModel) macros.get(k);
                    String identifier = macro.getIdentifier();
                    String macroScript =
                    newParser.toString(
                    (SequenceNode) ((SequenceNode) parsedMacros.get(identifier)).getChildAt(0),
                    parsedMacros
                    );
                    MacroModel translatedMacro = new MacroModel();
                    translatedMacro.setIdentifier(identifier);
                    translatedMacro.setScript(macroScript);
                    translatedMacros.add(translatedMacro);
                 
                    sr.macros[k][0] = macro.getIdentifier();
                    sr.macros[k++][1] = translatedMacro.getScript();
                }
                 
                newParser = translator.getParser(translatedMacros);
                parsedScript = newParser.parse(sr.script);
                 
                cube.reset();
                parsedScript.applySubtreeTo(cube, false);
                sr.ltm = parsedScript.getLayerTurnCount();
                sr.ftm = parsedScript.getFaceTurnCount();
                sr.qtm = parsedScript.getQuarterTurnCount();
                sr.visualOrder = cube.getVisibleOrder();
                sr.realOrder = cube.getOrder();
                sr.visualPermutation = cube.toVisualPermutationString(newParser);
                sr.realPermutation = cube.toPermutationString(newParser);
                 */
            }
            
            records.add(sr);
        }
        
        return records;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

    }//GEN-END:initComponents
    
    public void exportFile(File file, ProgressObserver p) throws IOException {
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
