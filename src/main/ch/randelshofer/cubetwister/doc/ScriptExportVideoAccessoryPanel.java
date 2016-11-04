/**
 * @(#)ScriptExportVideoAccessoryPanel.java  1.3  2011-04-20
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.ProgressObserver;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.*;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;

/**
 * ScriptExportVideoAccessoryPanel with file filters.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.2 2009-08-29 Added support for RAW video encoding.
 * <br>1.1 2008-08-12 Added support for AVI video
 * <br>1.0 ScriptExportVideoAccessoryPanel Created.
 */
public class ScriptExportVideoAccessoryPanel extends javax.swing.JPanel {
    private final static long serialVersionUID = 1L;

    private JFileChooser fileChooser;
    private Preferences prefs;
    private ExtensionFileFilter tsccAVI = new ExtensionFileFilter("AVI Screen Capture video", "avi");
    //private ExtensionFileFilter pngAVI = new ExtensionFileFilter("AVI PNG video", "avi");
    private ExtensionFileFilter jpgAVI = new ExtensionFileFilter("AVI JPEG video", "avi");
    private ExtensionFileFilter animMovie = new ExtensionFileFilter("QuickTime Animation video", "mov");
    private ExtensionFileFilter pngMovie = new ExtensionFileFilter("QuickTime PNG video", "mov");
    private ExtensionFileFilter tsccMovie = new ExtensionFileFilter("QuickTime Screen Capture video", "mov");
    private ExtensionFileFilter jpgMovie = new ExtensionFileFilter("QuickTime JPEG video", "mov");
    private ExtensionFileFilter pngStills = new ExtensionFileFilter("PNG still images", "png");
    private ExtensionFileFilter jpgStills = new ExtensionFileFilter("JPEG still images", "jpg");
    private ScriptModel m;
    private HashMap<String,ExtensionFileFilter> effMap=new HashMap<String,ExtensionFileFilter>();
    private HashMap<ExtensionFileFilter,String> invEffMap=new HashMap<ExtensionFileFilter,String>();

    /** Creates the accessory panel and adds its file filters to
    the file chooser. */
    public ScriptExportVideoAccessoryPanel(ScriptModel m, JFileChooser fileChooser) {
        initComponents();
        fromLabel.setVisible(false);
        fromField.setVisible(false);
        toLabel.setVisible(false);
        toField.setVisible(false);

        effMap.put("rawAVI", tsccAVI);
        effMap.put("jpgAVI", jpgAVI);
        effMap.put("animMovie", animMovie);
        effMap.put("pngMovie", pngMovie);
        effMap.put("rawMovie", tsccMovie);
        effMap.put("jpgMovie", jpgMovie);
        effMap.put("pngStills", pngStills);
        effMap.put("jpgStills", jpgStills);
        for (Map.Entry<String,ExtensionFileFilter> e:effMap.entrySet()) {
        invEffMap.put(e.getValue(),e.getKey());
        }

        for (Component c : innerPanel.getComponents()) {
            if (c instanceof JTextField) {
                c.setMinimumSize(c.getPreferredSize());
            }
        }

        if (UIManager.getLookAndFeel().getID().toLowerCase().indexOf("aqua") == -1) {
            GridBagLayout layout = (GridBagLayout) innerPanel.getLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 10, 0, 0);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridy = 0;
            int count = 0;
            for (Component c : innerPanel.getComponents()) {
                if (c instanceof JSlider) {
                    count++;
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                } else {
                    gbc.fill = GridBagConstraints.NONE;
                }
                gbc.gridy = count / 2;
                gbc.insets = new Insets((gbc.gridy > 0) ? 10 : 0, 10, 0, 0);
                layout.setConstraints(c, gbc);
                count++;
            }
            setBorder(null);
        }

        this.m = m;

        prefs = Preferences.userNodeForPackage(getClass());


        this.fileChooser = fileChooser;
        fileChooser.setAcceptAllFileFilterUsed(false);
        //fileChooser.addChoosableFileFilter(pngAVI);
        fileChooser.addChoosableFileFilter(tsccAVI);
        fileChooser.addChoosableFileFilter(jpgAVI);
        fileChooser.addChoosableFileFilter(animMovie);
        fileChooser.addChoosableFileFilter(pngMovie);
        fileChooser.addChoosableFileFilter(tsccMovie);
        fileChooser.addChoosableFileFilter(jpgMovie);
        fileChooser.addChoosableFileFilter(pngStills);
        fileChooser.addChoosableFileFilter(jpgStills);
        String format = prefs.get("Script.exportVideoFormat", "pngMovie");
        ExtensionFileFilter eff=effMap.get(format);
        if (eff!=null) {fileChooser.setFileFilter(eff);
        }
        fileChooser.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("fileFilterChanged".equals(evt.getPropertyName())) {
                    String format = invEffMap.get((ExtensionFileFilter)evt.getNewValue());
                    prefs.put("Script.exportVideoFormat", format);
                    updateOptions();
                }
            }
        });


        widthField.setText(prefs.get("Script.exportVideo.width", "320"));
        heightField.setText(prefs.get("Script.exportVideo.height", "240"));
        fromField.setText(prefs.get("Script.exportVideo.firstFrame", "0"));
        toField.setText(prefs.get("Script.exportVideo.lastFrame", ""));
        qualitySlider.setValue((int) (100d * prefs.getDouble("Script.exportVideo.quality", 0.9)));
        fpsField.setText(Double.toString(prefs.getDouble("Script.exportVideo.fps", 30.0)));
        updateOptions();

    }

    public void setVideoWidth(int newValue) {
        widthField.setText(Integer.toString(newValue));
    }

    public int getVideoWidth() {
        prefs.put("Script.exportVideo.width", widthField.getText());
        try {
            return Integer.decode(widthField.getText()).intValue();
        } catch (NumberFormatException e) {
            return 320;
        }
    }

    public void setVideoHeight(int newValue) {
        if (newValue == Integer.MAX_VALUE) {
            heightField.setText("");
        } else {
            heightField.setText(Integer.toString(newValue));
        }
    }

    public int getVideoHeight() {
        prefs.put("Script.exportVideo.height", heightField.getText());
        try {
            return Integer.decode(heightField.getText()).intValue();
        } catch (NumberFormatException e) {
            return 240;
        }
    }

    public void setFirstFrame(int newValue) {
        fromField.setText(Integer.toString(newValue));
    }

    public int getFirstFrame() {
        prefs.put("Script.exportVideo.firstFrame", fromField.getText());
        try {
            return Integer.decode(fromField.getText()).intValue();
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public void setLastFrame(int newValue) {
        if (newValue == Integer.MAX_VALUE) {
            toField.setText("");
        } else {
            toField.setText(Integer.toString(newValue));
        }
    }

    public int getLastFrame() {
        prefs.put("Script.exportVideo.lastFrame", toField.getText());
        try {
            return Integer.decode(toField.getText()).intValue();
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    public void setFPS(double newValue) {
        String str = Double.toString(newValue);
        fpsField.setText(str);
    }

    public double getFPS() {
        prefs.put("Script.exportVideo.fps", fpsField.getText());
        try {
            return Double.valueOf(fpsField.getText()).doubleValue();
        } catch (NumberFormatException e) {
            return 30d;
        }
    }

    public void setQuality(double newValue) {
        qualitySlider.setValue((int) (100 * newValue));
    }

    public double getQuality() {
        prefs.putDouble("Script.exportVideo.quality", qualitySlider.getValue() / 100d);
        return qualitySlider.getValue() / 100d;
    }

    private void updateOptions() {
        boolean b = (fileChooser.getFileFilter() == jpgMovie ||
                fileChooser.getFileFilter() == jpgAVI ||
                fileChooser.getFileFilter() == jpgStills);
        qualityLabel.setVisible(b);
        qualitySlider.setVisible(b);
        revalidate();
    }

    public void setScriptModel(ScriptModel newValue) {
        this.m=newValue;
    }

    public ScriptVideoExporter createExporter(File file, ProgressObserver progressMonitor) {
        ScriptVideoExporter sve = new ScriptVideoExporter(m, file, progressMonitor);
        sve.setFirstFrame(getFirstFrame());
        sve.setLastFrame(getLastFrame());
        sve.setVideoWidth(getVideoWidth());
        sve.setVideoHeight(getVideoHeight());
        sve.setQuality(getQuality());
        sve.setFPS(getFPS());
        /*if (fileChooser.getFileFilter() == pngAVI) {
            sve.setFormat(ScriptVideoExporter.FileFormat.AVI_PNG);
        } else*/ if (fileChooser.getFileFilter() == tsccAVI) {
            sve.setFormat(ScriptVideoExporter.FileFormat.AVI_TSCC);
        } else if (fileChooser.getFileFilter() == jpgAVI) {
            sve.setFormat(ScriptVideoExporter.FileFormat.AVI_JPG);
        } else if (fileChooser.getFileFilter() == animMovie) {
            sve.setFormat(ScriptVideoExporter.FileFormat.QUICKTIME_ANIMATION);
        } else if (fileChooser.getFileFilter() == pngMovie) {
            sve.setFormat(ScriptVideoExporter.FileFormat.QUICKTIME_PNG);
        } else if (fileChooser.getFileFilter() == tsccMovie) {
            sve.setFormat(ScriptVideoExporter.FileFormat.QUICKTIME_TSCC);
        } else if (fileChooser.getFileFilter() == jpgMovie) {
            sve.setFormat(ScriptVideoExporter.FileFormat.QUICKTIME_JPG);
        } else if (fileChooser.getFileFilter() == pngStills) {
            sve.setFormat(ScriptVideoExporter.FileFormat.PNG_STILLS);
        } else {
            sve.setFormat(ScriptVideoExporter.FileFormat.JPG_STILLS);
        }
        return sve;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        innerPanel = new javax.swing.JPanel();
        widthLabel = new javax.swing.JLabel();
        widthField = new javax.swing.JTextField();
        heightLabel = new javax.swing.JLabel();
        heightField = new javax.swing.JTextField();
        fpsLabel = new javax.swing.JLabel();
        fpsField = new javax.swing.JTextField();
        qualityLabel = new javax.swing.JLabel();
        qualitySlider = new javax.swing.JSlider();
        fromLabel = new javax.swing.JLabel();
        fromField = new javax.swing.JTextField();
        toLabel = new javax.swing.JLabel();
        toField = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        setLayout(new java.awt.GridBagLayout());

        innerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        innerPanel.setLayout(new java.awt.GridBagLayout());

        widthLabel.setText("Width:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        innerPanel.add(widthLabel, gridBagConstraints);

        widthField.setColumns(4);
        widthField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        widthField.setText("320");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        innerPanel.add(widthField, gridBagConstraints);

        heightLabel.setText("Height:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        innerPanel.add(heightLabel, gridBagConstraints);

        heightField.setColumns(4);
        heightField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        heightField.setText("240");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        innerPanel.add(heightField, gridBagConstraints);

        fpsLabel.setText("Frames per Sec.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        innerPanel.add(fpsLabel, gridBagConstraints);

        fpsField.setColumns(4);
        fpsField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fpsField.setText("30.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 0);
        innerPanel.add(fpsField, gridBagConstraints);

        qualityLabel.setText("Quality:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        innerPanel.add(qualityLabel, gridBagConstraints);

        qualitySlider.setMajorTickSpacing(25);
        qualitySlider.setMinorTickSpacing(5);
        qualitySlider.setPaintLabels(true);
        qualitySlider.setPaintTicks(true);
        qualitySlider.setValue(90);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        innerPanel.add(qualitySlider, gridBagConstraints);

        fromLabel.setText("Frames from:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        innerPanel.add(fromLabel, gridBagConstraints);

        fromField.setColumns(4);
        fromField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        fromField.setText("1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        innerPanel.add(fromField, gridBagConstraints);

        toLabel.setText("to:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        innerPanel.add(toLabel, gridBagConstraints);

        toField.setColumns(4);
        toField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        innerPanel.add(toField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(innerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField fpsField;
    private javax.swing.JLabel fpsLabel;
    private javax.swing.JTextField fromField;
    private javax.swing.JLabel fromLabel;
    private javax.swing.JTextField heightField;
    private javax.swing.JLabel heightLabel;
    private javax.swing.JPanel innerPanel;
    private javax.swing.JLabel qualityLabel;
    private javax.swing.JSlider qualitySlider;
    private javax.swing.JTextField toField;
    private javax.swing.JLabel toLabel;
    private javax.swing.JTextField widthField;
    private javax.swing.JLabel widthLabel;
    // End of variables declaration//GEN-END:variables
}
