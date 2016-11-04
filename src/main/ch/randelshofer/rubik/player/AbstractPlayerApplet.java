/*
 * @(#)AbstractPlayerApplet.java  6.3.1  2012-02-12
 *
 * Copyright (c) 2005-2012 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.rubik.player;

import ch.randelshofer.gui.Fonts;
import ch.randelshofer.gui.JMultilineLabel;
import ch.randelshofer.gui.LayeredBorderLayout;
import ch.randelshofer.gui.border.BackdropBorder;
import ch.randelshofer.gui.border.ButtonStateBorder;
import ch.randelshofer.gui.border.ImageBevelBorder;
import ch.randelshofer.gui.border.RoundedLineBorder;
import ch.randelshofer.gui.plaf.CustomButtonUI;
import ch.randelshofer.io.ParseException;
import ch.randelshofer.util.ArrayUtil;
import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.Cube3D;
import ch.randelshofer.rubik.Cube3DCanvas;
import ch.randelshofer.rubik.CubeEvent;
import ch.randelshofer.rubik.CubeKind;
import ch.randelshofer.rubik.CubeListener;
import ch.randelshofer.rubik.Cubes;
import ch.randelshofer.rubik.DefaultCubeAttributes;
import ch.randelshofer.rubik.parser.CubeMarkupNotation;
import ch.randelshofer.rubik.parser.Node;
import ch.randelshofer.rubik.parser.ScriptKeyboardHandler;
import ch.randelshofer.rubik.parser.ScriptParser;
import ch.randelshofer.rubik.parser.ScriptPlayer;
import ch.randelshofer.rubik.parser.SequenceNode;
import ch.randelshofer.rubik.parser.Symbol;
import ch.randelshofer.util.AppletParameterException;
import ch.randelshofer.util.Applets;
import ch.randelshofer.util.Images;
import ch.randelshofer.util.PooledSequentialDispatcher;
import java.lang.reflect.InvocationTargetException;
import javax.swing.event.*;
import nanoxml.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ButtonUI;
import javax.swing.text.*;

/**
 * This is the base class for Applets featuring a Rubik's Cube like puzzle which
 * can play back scripts.
 *
 * @author Werner Randelshofer
 * @version 6.3.1 Fixes "images" path.
 * <br>6.3 2011-06-29 Adds setScriptType/getScriptType methods to JavaScript
 * API.
 * <br>6.2 2010-08-03 "stickersFront", "stickersLeft", ... parameters could only
 * access the first six colors in the color list. Adds applet parameter
 * "locale".
 * <br>6.1.2 2010-03-27 Test if player != null in the JavaScript API methods.
 * <br>6.1.1 2010-02-12 Get default twist duration from model.
 * <br>6.1 2010-02-09 Added support for zip-compressed resource file.
 * <br>6.0 2010-01-24 Load stickers image from the .jar file, if a file named
 * StickersImage.png, .jpg or .gif is in the .jar file.
 * <br>5.5.1 2010-01-01 Adding an additional color to the cube in the resource
 * XML file caused one complete face to be in this color.
 * <br>5.5 2009-12-02 Added "scaleFactor" parameter.
 * <br>5.4.2 2009-11-28 Flush memory when applet is stopped.
 * <br>5.4.1 2009-07-04 Catch all throwables when getting an image.
 * <br>5.4 2009-04-13 Added method getAPIInfo(), fixed method
 * getParameterInfo().
 * <br>5.3 Added methods setPermutation/getPermutation.
 * <br>5.2 2009-03-08 Added "stickerBevel" parameter. The script field is now
 * always displayed, if the "displayLines" parameter has a value different from
 * 0.
 * <br>5.0 2009-01-06 Added public methods for controlling playback using
 * JavaScript. Changed case of 'scriptType' parameter values.
 * <br>4.5 2009-01-04 Bail if notation parameter is for a different cube.
 * <br>4.4 2008-12-20 Only read cube atributes from resource file if kind
 * matches. "colorMap" attribute was wrongly used instead of "colorList".
 * "colorTable" was not applied to colorMap. Fixed class-cast exceptions when
 * reading "colorTable" and "colorList" parameters.
 * <br>4.3 2008-09-13 Read cube attributes from resource file.
 * <br>4.2 2008-06-14 Print all read parameters when debugging mode is turned
 * on.
 * <br>4.1 2008-04-03 Added parameter showController.
 * <br>4.0 2008-02-02 Rewrote parameters. Now, an empty parameter has the same
 * effect like not specifying this parameter at all.
 * <br>3.0 2007-12-25 Rewritten for Swing.
 * <br>2.0 2007-11-15 Upgraded to Java 1.4.
 * <br>1.0.2 2007-11-14 Compute version string dynamically.
 * <br>1.0.1 2007-03-04 Updated version string.
 * <br>1.0 2005-03-03 Created.
 */
public abstract class AbstractPlayerApplet extends javax.swing.JApplet
        implements Runnable {

    protected ScriptPlayer player;
    private JMultilineLabel scriptTextArea;
    private JLayeredPane viewPane;
    private JPanel controlsPanel;
    private final static Color inactiveSelectionBackground = new Color(213, 213, 213);
    private final static Color activeSelectionBackground = new Color(255, 255, 64);
    protected Cube3DCanvas frontCanvas, rearCanvas;
    protected CubeMarkupNotation notation;
    protected boolean isRearViewVisible;
    private String script;
    private ScriptParser parser;
    /**
     * We cache the information about the script here, because retrieval is
     * expensive.
     */
    private String turnCountInfo;
    /**
     * This flag is set to true, if the "scriptType" applet parameter has the
     * value "Solver".
     */
    private boolean isSolver;
    /**
     * This flag is set to true, if the "autoPlay" applet parameter has the
     * value "true".
     */
    private boolean isAutoPlay;
    /**
     * Set this to true, to print debug information.
     */
    private boolean debug;
    /**
     * The settings panel is superimposed over the cube.
     */
    private JPanel settingsPanel;
    private DefaultCubeAttributes attributes;
    private Cube3D cube3D;
    private int defaultTwistDuration;

    /**
     * Initializes the applet.
     */
    @Override
    public void init() {
        Runtime rt = Runtime.getRuntime();

        // All initialisation is done on this worker thread.
        PooledSequentialDispatcher.dispatchConcurrently(this);

        // System.out.println(getAppletInfo());
        JPanel p = (JPanel) getContentPane();
        p.setBackground(Color.WHITE);
        p.setOpaque(true);
    }

    /**
     * Stops all background threads.
     */
    @Override
    public void stop() {
        if (player != null) {
            player.stop();
            player.getCanvas().flush();
            if (rearCanvas != null) {
                rearCanvas.flush();
            }
        }
        Runtime.getRuntime().gc();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    public static void main(final AbstractPlayerApplet applet, String[] args) {
        Applets.setMainArgs(applet, args);
        // System.setProperty("apple.awt.graphics.UseQuartz","false");
        applet.init();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame f = new JFrame(applet.getAppletInfo());
                f.getContentPane().add(applet);
                f.setSize(400, 400);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setVisible(true);
                applet.start();
            }
        });
    }

    protected String getAppletVersion() {
        String version = AbstractPlayerApplet.class.getPackage().getImplementationVersion();
        return version == null ? "" : version;
    }

    protected abstract ScriptPlayer createPlayer();

    protected abstract int getLayerCount();

    protected Cube3DCanvas createRearCanvas() {
        // Netbeans form editor does not support abstract classes. :(
        //throw new InternalError("Subclass responsibility");
        return null;
    }

    protected void configurePlayer(ScriptPlayer p) {
    }

    /**
     * This method is public due to a side effect of the implementation. Never
     * call this method explicitly.
     * <p>
     * Initialises the applet. This is done on a background thread, because it
     * may take a while.
     *
     * @see #init
     */
    @Override
    public void run() {
        player = createPlayer();
        configurePlayer(player);
        cube3D = player.getCube3D();

        frontCanvas = player.getCanvas();
        ((JComponent) frontCanvas.getVisualComponent()).setOpaque(true);
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    initComponents();
                }
            });
        } catch (InterruptedException ex) {
//
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
        controlsPanel = new JPanel();
        controlsPanel.setLayout(new BorderLayout());

        controlsPanel.add("Center", player.getControlPanelComponent());
        controlsPanel.add("South", scriptTextArea = new JMultilineLabel());
        scriptTextArea.setBorderColor(new Color(0x959595));
        scriptTextArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        scriptTextArea.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {
                int caret = scriptTextArea.viewToModel(event.getX(), event.getY());
                player.moveToCaret(caret);
            }
        });
        // Speed up the layout process by giving the scriptTextArea the size
        // of the applet.
        scriptTextArea.setSize(getSize());
        final Component view;

        view = player.getVisualComponent();

        try {
            readParameters();
            if (debug) {
                System.out.flush();
            }
        } catch (final Throwable e) {
            if (debug) {
                System.out.flush();
            }
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    e.printStackTrace();

                    getContentPane().removeAll();
                    getContentPane().setLayout(new BorderLayout());

                    JTextPane msgField = new JTextPane();
                    msgField.setFont(msgField.getFont().deriveFont(11f));
                    msgField.setBorder(new EmptyBorder(12, 20, 20, 20));
                    msgField.setEditable(false);
                    MutableAttributeSet redMarker = new SimpleAttributeSet();
                    StyleConstants.setBackground(redMarker, new Color(0xfc9d9f));
                    MutableAttributeSet bold = new SimpleAttributeSet();
                    StyleConstants.setBold(bold, true);
                    MutableAttributeSet plain = new SimpleAttributeSet();
                    MutableAttributeSet code = new SimpleAttributeSet();
                    StyleConstants.setFontFamily(code, Fonts.getMonospaceFont().getFamily());
                    StyleConstants.setFontSize(code, 10);

                    DefaultStyledDocument doc = new DefaultStyledDocument();
                    try {
                        doc.insertString(doc.getLength(), getAppletInfo() + "\n\n", plain);

                        if (e instanceof AppletParameterException) {
                            AppletParameterException ape = (AppletParameterException) e;
                            doc.insertString(doc.getLength(), "Illegal Applet Parameter\n", bold);
                            doc.insertString(doc.getLength(), ape.getMessageText() + "\n", plain);
                            doc.insertString(doc.getLength(), "<parameter name=\"" + ape.getName() + "\" value=\"", plain);
                            doc.insertString(doc.getLength(), ape.getValue().substring(0, ape.getStart()), plain);
                            doc.insertString(doc.getLength(), ape.getValue().substring(ape.getStart(), ape.getEnd()), redMarker);
                            doc.insertString(doc.getLength(), ape.getValue().substring(ape.getEnd()) + "\"/>", plain);
                        } else {
                            if (e.getMessage() != null && e.getMessage().length() > 0) {
                                doc.insertString(doc.getLength(), e.getMessage(), bold);
                            } else {
                                doc.insertString(doc.getLength(), e.toString(), bold);

                            }
                        }
                    } catch (BadLocationException ex) {
                        InternalError error = new InternalError(ex.toString());
                        error.initCause(ex);
                        throw error;
                    }
                    msgField.setDocument(doc);
                    JScrollPane errorPane = new JScrollPane(msgField);
                    getContentPane().removeAll();
                    getContentPane().add("Center", errorPane);

                    invalidate();
                    validate();
                }
            });
            return;
        }

        ChangeListener selectionUpdater
                = new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent evt) {
                        selectCurrentSymbol();
                    }
                };

        player.getTimeModel().addChangeListener(selectionUpdater);
        player.addChangeListener(selectionUpdater);
        player.getCube().addCubeListener(new CubeListener() {

            @Override
            public void cubeTwisted(CubeEvent evt) {
                doUpdateInfo();
            }

            @Override
            public void cubeChanged(CubeEvent evt) {
                doUpdateInfo();
            }

            private void doUpdateInfo() {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        updateInfo();
                    }
                });

            }
        });

        selectCurrentSymbol();

        player.setResetButtonVisible(Applets.getParameter(AbstractPlayerApplet.this, "showResetButton", true));
        player.setScrambleButtonVisible(Applets.getParameter(AbstractPlayerApplet.this, "showScrambleButton", true));

        if (Applets.getParameter(AbstractPlayerApplet.this, "showSettingsButton", true)) {
            ResourceBundle labels = ResourceBundle.getBundle("ch.randelshofer.rubik.player.Labels", getLocale());
            final JToggleButton settingsButton = new JToggleButton();
            Border eastBorder = new BackdropBorder(
                    new ButtonStateBorder(
                            new ImageBevelBorder(
                                    Images.createImage(getClass(), "/org/monte/media/gui/images/Player.borderEast.png"),
                                    new Insets(1, 1, 1, 1), new Insets(0, 4, 1, 4)),
                            new ImageBevelBorder(
                                    Images.createImage(getClass(), "/org/monte/media/gui/images/Player.borderEastP.png"),
                                    new Insets(1, 1, 1, 1), new Insets(0, 4, 1, 4))));
            settingsButton.setIcon(new ImageIcon(Images.createImage(getClass(), "/org/monte/media/gui/images/PlayerActions.png")));
            settingsButton.setUI((ButtonUI) CustomButtonUI.createUI(settingsButton));
            settingsButton.setBorder(eastBorder);
            settingsButton.setMargin(new Insets(0, 0, 0, 0));
            settingsButton.setPreferredSize(new Dimension(16, 16));
            settingsButton.setToolTipText(labels.getString("settings.toolTipText"));
            settingsButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent evt) {
                    setSettingsPanelVisible(settingsButton.isSelected());
                }
            });

            controlsPanel.add("East", settingsButton);
        }

        viewPane = new JLayeredPane();
        viewPane.setOpaque(true);
        viewPane.setBackground(getBackground());
        viewPane.setLayout(new LayeredBorderLayout());
        viewPane.add(view);
        viewPane.setLayer(view, JLayeredPane.DEFAULT_LAYER);

        SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {
                        playerPanel.add("Center", viewPane);
                        if (Applets.getParameter(AbstractPlayerApplet.this, "showController", true)) {
                            playerPanel.add("South", controlsPanel);
                        }

                        controlsPanel.invalidate();

                        if (!Applets.getParameter(AbstractPlayerApplet.this, "showInfo", false)) {
                            getContentPane().remove(infoPanel);
                        }
                        if (!Applets.getParameter(AbstractPlayerApplet.this, "showPlayer", true)) {
                            getContentPane().remove(playerPanel);
                        }

                        getContentPane().invalidate();
                        if (isAutoPlay) {
                            SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    player.start();
                                }
                            });
                        }
                        validate();
                        updateInfo();
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                boolean showRear = Applets.getParameter(AbstractPlayerApplet.this, "showRear", false);
                                if (Applets.getParameter(AbstractPlayerApplet.this, "rearView") != null) {
                                    showRear = Applets.getParameter(AbstractPlayerApplet.this, "rearView", false);
                                    printWarning("The \"rearView\" parameter is deprecated, use \"showRear\" instead.");
                                }
                                setRearViewVisible(showRear);
                            }
                        });

                        ScriptKeyboardHandler skp = new ScriptKeyboardHandler();
                        skp.setCube(player.getCube());
                        skp.setParser(parser);
                        skp.setCanvas(player.getCanvas());
                    }
                });
    }

    private static class SliderLabel extends JLabel {

        private final static long serialVersionUID = 1L;

        public SliderLabel(String text) {
            super(text);
        }

        @Override
        public Color getForeground() {
            return Color.WHITE;
        }
    }

    @SuppressWarnings({"rawtype", "unchecked"})
    protected void setSettingsPanelVisible(boolean b) {
        if (b) {
            if (settingsPanel == null) {
                settingsPanel = new JPanel(new GridBagLayout());
                GridBagConstraints gbx = new GridBagConstraints();
                gbx.gridx = 0;
                gbx.anchor = GridBagConstraints.CENTER;
                settingsPanel.setOpaque(false);
                settingsPanel.setBorder(new HUDBorder());
                final JSlider speedSlider = new JSlider();
                Font labelFont = new Font("Dialog", Font.PLAIN, 9);
                speedSlider.putClientProperty("JComponent.sizeVariant", "mini");
                speedSlider.setOpaque(false);
                speedSlider.setForeground(Color.WHITE);
                speedSlider.setFont(labelFont);
                speedSlider.setMinimum(-400);
                speedSlider.setMaximum(400);
                speedSlider.setValue(0);
                Hashtable labelTable = new Hashtable();
                ResourceBundle labels = ResourceBundle.getBundle("ch.randelshofer.rubik.player.Labels", getLocale());
                labelTable.put(new Integer(-390), new SliderLabel(labels.getString("slow.text")));
                labelTable.put(new Integer(390), new SliderLabel(labels.getString("fast.text")));
                speedSlider.setLabelTable(labelTable);
                for (Iterator i = labelTable.values().iterator(); i.hasNext();) {
                    JLabel label = (JLabel) i.next();
                    label.setFont(labelFont);
                    label.setForeground(Color.WHITE);
                }
                speedSlider.setPaintLabels(true);
                speedSlider.setMajorTickSpacing(400);
                speedSlider.setPaintTicks(true);
                Dimension d = speedSlider.getPreferredSize();
                speedSlider.addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        int value = speedSlider.getValue();
                        if (value < 0) {
                            attributes.setTwistDuration((int) (defaultTwistDuration * (1f - value / 200f)));
                        } else {
                            attributes.setTwistDuration((int) (defaultTwistDuration * (1f - value / 400f)));
                        }
                        //System.out.println("AbstractPlayerApplet twist duration:"+attributes.getTwistDuration());
                    }
                });
                d.width = 120;
                speedSlider.setPreferredSize(d);
                JLabel speedLabel = new JLabel(labels.getString("twistSpeed.text"));
                speedLabel.setHorizontalAlignment(JLabel.CENTER);
                speedLabel.setForeground(Color.WHITE);
                speedLabel.setOpaque(false);
                speedLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
                settingsPanel.add(speedLabel, gbx);
                settingsPanel.add(speedSlider, gbx);

                final JCheckBox showRearCheckBox = new JCheckBox(labels.getString("showRear.text"));
                showRearCheckBox.setFont(new Font("Dialog", Font.PLAIN, 11));
                showRearCheckBox.setOpaque(false);
                showRearCheckBox.setSelected(isRearViewVisible);
                showRearCheckBox.setForeground(Color.WHITE);
                showRearCheckBox.setHorizontalAlignment(JLabel.CENTER);
                showRearCheckBox.putClientProperty("JComponent.sizeVariant", "small");
                showRearCheckBox.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        setRearViewVisible(showRearCheckBox.isSelected());
                    }
                });
                settingsPanel.add(showRearCheckBox, gbx);

                JLabel versionLabel = new JLabel(getAppletVersion() + " © W. Randelshofer");
                versionLabel.setHorizontalAlignment(JLabel.CENTER);
                versionLabel.setForeground(Color.WHITE);
                versionLabel.setOpaque(false);
                versionLabel.setFont(new Font("Dialog", Font.PLAIN, 9));
                gbx.insets = new Insets(10, 0, 0, 0);
                settingsPanel.add(versionLabel, gbx);

            }
            viewPane.add(settingsPanel, LayeredBorderLayout.SOUTH_CENTER);
            viewPane.setLayer(settingsPanel, JLayeredPane.PALETTE_LAYER.intValue() + 1);
            ((LayoutManager2) viewPane.getLayout()).addLayoutComponent(settingsPanel, LayeredBorderLayout.SOUTH_CENTER);
        } else {
            Component[] c = viewPane.getComponentsInLayer(JLayeredPane.PALETTE_LAYER.intValue() + 1);
            for (int i = 0; i < c.length; i++) {
                viewPane.remove(c[i]);
            }
        }
        viewPane.revalidate();
        viewPane.repaint();
    }

    protected void setRearViewVisible(boolean newValue) {
        if (isRearViewVisible != newValue) {
            isRearViewVisible = newValue;
            double rearViewScaleFactor = Applets.getParameter(this, "rearViewScaleFactor", 0.25);
            rearViewScaleFactor = Math.min(1.0, Math.max(0.1, rearViewScaleFactor));
            if (newValue) {
                if (rearCanvas == null) {
                    rearCanvas = createRearCanvas();
                    if (rearCanvas != null) {
                        JComponent component = (JComponent) rearCanvas.getVisualComponent();
                        component.setBorder(new RoundedLineBorder(new Color(0x959595), 8, true));
                        component.setOpaque(false);
                    }
                }
                if (rearCanvas != null) {
                    JComponent component = (JComponent) rearCanvas.getVisualComponent();
                    LayeredBorderLayout lbl = (LayeredBorderLayout) viewPane.getLayout();
                    lbl.setPipScaleFactor(rearViewScaleFactor);
                    viewPane.add(component, LayeredBorderLayout.PIP_SOUTH_EAST);
                    viewPane.setLayer(component, JLayeredPane.PALETTE_LAYER.intValue());
                }
            } else {
                if (rearCanvas != null) {
                    viewPane.remove(rearCanvas.getVisualComponent());
                    rearCanvas.flush();
                    rearCanvas = null;
                }
            }
            viewPane.revalidate();
            viewPane.repaint();
        }
    }

    protected void selectCurrentSymbol() {
        Node s = player.getCurrentNode();
        int p1, p2;
        if (s == null) {
            p1 = p2 = scriptTextArea.getText().length();
        } else {
            p1 = s.getStartPosition();
            p2 = s.getEndPosition() + 1;
        }
        scriptTextArea.select(p1, p2);
        scriptTextArea.setSelectionBackground(player.isProcessingCurrentNode() ? activeSelectionBackground : inactiveSelectionBackground);
    }

    protected Cube getCube() {
        return player.getCube();
    }

    protected void printWarning(String message) {
        if (debug) {
            System.err.println(getAppletName() + " Warning: " + message);
        }
    }

    protected void readParameters()
            throws AppletParameterException {

        if (Applets.getParameter(this, "debug", false)) {
            debug = true;
            Applets.setDebug(true);
            System.out.println();
            System.out.println(getAppletInfo());
            System.out.println();
        }

        if (getParameter("locale") != null) {
            Locale l = new Locale(getParameter("locale"));
            setLocale(l);
            player.getControlPanelComponent().setLocale(l);
        }

        //attributes = (DefaultCubeAttributes) cube3D.getAttributes();
        HashMap<String, Color> colorMap = new HashMap<String, Color>();
        HashMap<String, Color> deprecatedColorTable = new HashMap<String, Color>();
        int[] deprecatedFaceMap = {2, 0, 3, 5, 4, 1}; // maps FRDBLU to RUFLDB
        String[] faceNames = {"f", "r", "d", "b", "l", "u"};

        Hashtable scriptDefMap = new Hashtable();
        String value;
        String initScript;

        String[] keys;
        int[] ints;
        Color color;

        // Read resource data
        XMLElement resources = null;
        XMLElement defaultResources = null;
        String parameter = null;
        value = "";
        Reader in = null;
        try {
            in = new InputStreamReader(getPlayerResources());
            defaultResources = new XMLElement(new HashMap<String, char[]>(), true, false);
            defaultResources.parseFromReader(in);
        } catch (NullPointerException e) {
            printWarning("default resource data missing.");
        } catch (IOException e) {
            throw new AppletParameterException(parameter, value, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e2) {
            }
        }

        if (Applets.getParameter(this, "resourceFile", "").length() != 0
                && Applets.getParameter(this, "resourceData", "").length() != 0) {
            throw new AppletParameterException(
                    "\"resourceFile\" and \"resourceData\" are mutually exclusive.",
                    "resourceFile",
                    Applets.getParameter(this, "resourceFile", ""));
        }

        in = null;
        try {
            String resourceArchive = "";
            String resourceFile = Applets.getParameter(this, "resourceFile", "");
            String resourceData = Applets.getParameter(this, "resourceData", "");
            if (resourceFile.endsWith(".zip")) {
                resourceArchive = resourceFile;
                resourceFile = "";
            }
            if (resourceArchive.length() != 0) {
                parameter = "resourceFile";
                ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new URL(getDocumentBase(), resourceArchive).openStream()));
                if (resourceFile.length() == 0) {
                    while (true) {
                        ZipEntry entry = zin.getNextEntry();
                        if (!entry.isDirectory()) {
                            break;
                        }
                    }
                } else {
                    while (true) {
                        ZipEntry entry = zin.getNextEntry();
                        if (!entry.isDirectory() && entry.getName().equals(resourceFile)) {
                            break;
                        }
                    }
                }
                in = new InputStreamReader(zin);
                resources = new XMLElement(new HashMap<String, char[]>(), true, false);
                resources.parseFromReader(in);
            } else if (resourceFile.length() != 0) {
                parameter = "resourceFile";
                in = new InputStreamReader(new URL(getDocumentBase(), resourceFile).openStream());
                resources = new XMLElement(new HashMap<String, char[]>(), true, false);
                resources.parseFromReader(in);
            } else if (resourceData.length() != 0) {
                parameter = "resourceData";
                //System.out.println("resourceData=" + resourceData);
                in = new StringReader(value);
                resources = new XMLElement(new HashMap<String, char[]>(), true, false);
                resources.parseFromReader(in);
            } else {
                resources = defaultResources;
            }
        } catch (IOException e) {
            throw new AppletParameterException(parameter, value, e);

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e2) {
            }
        }

        // Configure cube from resources
        attributes = (DefaultCubeAttributes) cube3D.getAttributes();
        readCubeAttributes(resources, cube3D.getKind(), attributes);

        // Read applet background color
        color = Applets.getParameter(this, "backgroundColor", attributes.getFrontBgColor());
        if (color == null) {
            color = Color.WHITE;
        }
        Component canvas3D = player.getVisualComponent();
        canvas3D.setBackground(color);
        player.getControlPanelComponent().setBackground(color);
        controlsPanel.setBackground(color);
        attributes.setFrontBgColor(color);
        attributes.setRearBgColor(color);
        infoPanel.setBackground(color);
        infoField.setBackground(color);
        setBackground(color);

        // set initial orientation and scale factor
        if (Applets.getParameter(this, "alpha") != null) {
            attributes.setAlpha(
                    Applets.getParameter(this, "alpha", -25) / 180f * (float) Math.PI);
        }
        if (Applets.getParameter(this, "beta") != null) {
            attributes.setBeta(
                    Applets.getParameter(this, "beta", 45) / 180f * (float) Math.PI);
        }
        if (Applets.getParameter(this, "scaleFactor") != null) {
            attributes.setScaleFactor((float) Applets.getParameter(this, "scaleFactor", attributes.getScaleFactor()));
        }

        if (Applets.getParameter(this, "twistDuration") != null) {
            int intValue = Applets.getParameter(this, "twistDuration", 500);
            attributes.setTwistDuration(intValue);
        }
        defaultTwistDuration = attributes.getTwistDuration();

        // Init color lookup table
        if (Applets.getParameter(this, "colorTable", "").length() == 0
                && attributes.getFaceCount() == 6) {
            // colorTable is deprecated, but still supported
            for (int i = 0, n = attributes.getFaceCount(); i < n; i++) {
                int face = deprecatedFaceMap[i];
                deprecatedColorTable.put(
                        Integer.toString(i),
                        attributes.getStickerFillColor(attributes.getStickerOffset(face)));
            }
        }
        if (Applets.getParameter(this, "colorList", "").length() == 0
                && attributes.getFaceCount() == 6) {
            for (int i = 0, n = attributes.getFaceCount(); i < n; i++) {
                int face = i;
                colorMap.put(
                        Integer.toString(i),
                        attributes.getStickerFillColor(attributes.getStickerOffset(face)));
                colorMap.put(
                        faceNames[i],
                        attributes.getStickerFillColor(attributes.getStickerOffset(face)));
            }
        }

        // Read color lookup table
        boolean hasColorTableOrColorList = false;
        if (attributes.getFaceCount() == 6) {
            value = Applets.getParameter(this, "colorTable", "");
            if (value.length() != 0) {
                printWarning("The \"colorTable\" parameter is deprecated, use \"colorList\" instead.");
                hasColorTableOrColorList = true;
                HashMap<String, String> deprecatedColorTableParam = Applets.getIndexedKeyValueParameters(this, "colorTable", null);

                // Convert the values of the color lookup tables from String to Color
                if (deprecatedColorTableParam!=null) {
                Iterator<String> enumer = deprecatedColorTableParam.keySet().iterator();
                while (enumer.hasNext()) {
                    String key = enumer.next();
                    String item = deprecatedColorTableParam.get(key);
                    Color c = new Color(Applets.decode(item).intValue());

                    int i;
                    try {
                        i = Integer.parseInt(key);
                    } catch (NumberFormatException e) {
                        i = -1;
                    }
                    if (i >= 0) {
                        try {
                            deprecatedColorTable.put(key, c);
                            if (i >= 0 && i < 6) {
                                colorMap.put(Integer.toString(deprecatedFaceMap[i]), c);
                            }
                        } catch (NumberFormatException e) {
                            throw new AppletParameterException("colorTable", value,
                                    value.indexOf(item),
                                    value.indexOf(item) + item.length());
                        }
                    } else if (i != -1) {
                        colorMap.put(Integer.toString(i), c);
                    } else {
                        colorMap.put(key, c);
                    }
                }
                }
            }
        }
        value = Applets.getParameter(this, "colorList", "");
        if (value.length() != 0) {
            hasColorTableOrColorList = true;
            HashMap<String, String> colorMapParam = Applets.getIndexedKeyValueParameters(this, "colorList", null);

            // Convert the values of the color lookup tables from String to Color
            if (colorMapParam != null) {
                Iterator<String> enumer = colorMapParam.keySet().iterator();
                while (enumer.hasNext()) {
                    String key = enumer.next();
                    String item = colorMapParam.get(key);
                    try {
                        colorMap.put(key, new Color(Applets.decode(item).intValue()));
                    } catch (NumberFormatException e) {
                        throw new AppletParameterException("colorList", value,
                                value.indexOf(item),
                                value.indexOf(item) + item.length());
                    }
                }
            }
        }
        // init face colors
        if (hasColorTableOrColorList
                && Applets.getParameter(this, "faces", "").length() == 0
                && Applets.getParameter(this, "faceList", "").length() == 0) {
            for (int i = 0, n = attributes.getFaceCount(); i < n; i++) {
                String key = Integer.toString(i);
                color = colorMap.get(key);
                if (color != null) {
                    int face = i;
                    int offset = attributes.getStickerOffset(face);
                    for (int j = 0, m = attributes.getStickerCount(face); j < m; j++) {
                        attributes.setStickerFillColor(offset + j, color);
                    }
                }
            }
        }
        // Set the colors for all stickers on each side of the cube.
        value = Applets.getParameter(this, "faces", "");
        keys = Applets.getParameters(this, "faces", (String[]) null);
        if (keys != null && keys.length != 0) {
            printWarning("The \"faces\" parameter is deprecated, use \"faceList\" instead.");
            if (keys.length != attributes.getFaceCount()) {
                throw new AppletParameterException(
                        "The Applet parameter \"faces\" has " + keys.length + " items instead of " + attributes.getFaceCount() + " items.",
                        "faces", value);
            }
            for (int i = 0, n = attributes.getFaceCount(); i < n; i++) {
                if (!deprecatedColorTable.containsKey(keys[i])) {
                    throw new AppletParameterException("faces", value,
                            value.indexOf(keys[i]),
                            value.indexOf(keys[i]) + keys[i].length());
                }
                color = deprecatedColorTable.get(keys[i]);
                int face = deprecatedFaceMap[i];
                int offset = attributes.getStickerOffset(face);
                for (int j = 0, m = attributes.getStickerCount(face); j < m; j++) {
                    attributes.setStickerFillColor(offset + j, color);
                }
            }
        }
        value = Applets.getParameter(this, "faceList", "");
        keys = Applets.getParameters(this, "faceList", (String[]) null);
        if (keys != null && keys.length != 0) {
            if (keys.length != attributes.getFaceCount()) {
                throw new AppletParameterException(
                        "The Applet parameter 'faceList' has " + keys.length + " items instead of 6 items.",
                        "faceList", value);
            }
            for (int i = 0, n = attributes.getFaceCount(); i < n; i++) {
                if (!colorMap.containsKey(keys[i])) {
                    throw new AppletParameterException("faceList", value,
                            value.indexOf(keys[i]),
                            value.indexOf(keys[i]) + keys[i].length());
                }
                color = colorMap.get(keys[i]);
                int face = i;
                int offset = attributes.getStickerOffset(face);
                for (int j = 0, m = attributes.getStickerCount(face); j < m; j++) {
                    attributes.setStickerFillColor(offset + j, color);
                }
            }
        }

        // Set the colors for each sticker on each side of the cube.
        // This parameter is deprecated but still supported.
        value = Applets.getParameter(this, "stickers", "");
        keys = Applets.getParameters(this, "stickers", (String[]) null);
        if (keys != null && keys.length != 0) {
            printWarning("The \"stickers\" parameter is deprecated, use \"stickerList\" instead.");
            if (keys.length != 54) {
                throw new AppletParameterException(
                        "The Applet parameter \"stickers\" has " + keys.length + " items instead of 54 items.",
                        "stickers", value);
            }
            for (int i = 0, n = attributes.getFaceCount(); i < n; i++) {
                int face = deprecatedFaceMap[i];
                int offset = attributes.getStickerOffset(face);
                for (int j = 0, m = attributes.getStickerCount(face); j < m; j++) {
                    if (!deprecatedColorTable.containsKey(keys[i])) {
                        throw new AppletParameterException(
                                "stickers", value,
                                value.indexOf(keys[i]), value.indexOf(keys[i]) + keys[i].length());
                    }
                    attributes.setStickerFillColor(offset + j, deprecatedColorTable.get(keys[offset + j]));
                }
            }
        }
        value = Applets.getParameter(this, "stickerList", "");
        keys = Applets.getParameters(this, "stickerList", (String[]) null);
        if (keys != null && keys.length != 0) {
            if (keys.length != 54) {
                throw new AppletParameterException(
                        "The Applet parameter \"stickerList\" has " + keys.length + " items instead of 54 items.",
                        "stickerList", value);
            }
            for (int i = 0, n = attributes.getFaceCount(); i < n; i++) {
                int face = i;
                int offset = attributes.getStickerOffset(face);
                for (int j = 0, m = attributes.getStickerCount(face); j < m; j++) {
                    if (!deprecatedColorTable.containsKey(keys[i])) {
                        throw new AppletParameterException(
                                "stickerList", value,
                                value.indexOf(keys[i]),
                                value.indexOf(keys[i]) + keys[i].length());
                    }
                    attributes.setStickerFillColor(offset + j, deprecatedColorTable.get(keys[offset + j]));
                }
            }
        }

        // Set the colors for each sticker on each side of the cube.
        // XXX - This does only work correctly with 6-faced cubes
        String[] deprecatedParameterNames = {
            "stickersFront", "stickersRight", "stickersLeft",
            "stickersBack", "stickersDown", "stickersUp"
        };
        String[] parameterNames = {
            "stickersRightList", "stickersUpList", "stickersFrontList",
            "stickersLeftList", "stickersDownList", "stickersBackList"
        };
        for (int i = 0, n = Math.min(deprecatedParameterNames.length, attributes.getFaceCount()); i < n; i++) {
            value = Applets.getParameter(this, deprecatedParameterNames[i], "");
            keys = Applets.getParameters(this, deprecatedParameterNames[i], (String[]) null);
            if (keys != null && keys.length != 0) {
                printWarning("Parameter \"" + "\" is deprecated, use \"" + parameterNames[i] + "\" instead.");
                if (keys.length != attributes.getStickerCount(i)) {
                    throw new AppletParameterException(
                            "The Applet parameter \"" + deprecatedParameterNames[i]
                            + "\" has " + keys.length + " items instead of of "
                            + attributes.getStickerCount(i) + " items.",
                            deprecatedParameterNames[i], value);
                }
                int face = deprecatedFaceMap[i];
                int offset = attributes.getStickerOffset(face);
                for (int j = 0, m = attributes.getStickerCount(face); j < m; j++) {
                    if (!deprecatedColorTable.containsKey(keys[j])) {
                        System.out.println("AbstractPlayerApplet deprecatedColorTable:" + deprecatedColorTable);
                        throw new AppletParameterException(
                                deprecatedParameterNames[i], value,
                                value.indexOf(keys[j]),
                                value.indexOf(keys[j]) + keys[j].length() - 1);
                    }
                    attributes.setStickerFillColor(
                            attributes.getStickerOffset(face) + j,
                            deprecatedColorTable.get(keys[j]));
                }
            }
        }
        for (int i = 0, n = Math.min(parameterNames.length, attributes.getFaceCount()); i < n; i++) {
            value = Applets.getParameter(this, parameterNames[i], "");
            keys = Applets.getParameters(this, parameterNames[i], (String[]) null);
            if (keys != null && keys.length != 0) {
                if (keys.length != attributes.getStickerCount(i)) {
                    throw new AppletParameterException(
                            "The Applet parameter \"" + parameterNames[i]
                            + "\" has " + keys.length + " items instead of "
                            + attributes.getStickerCount(i) + " items.",
                            parameterNames[i], value);
                }
                int face = i;
                int offset = attributes.getStickerOffset(face);
                for (int j = 0, m = attributes.getStickerCount(face); j < m; j++) {
                    if (!colorMap.containsKey(keys[j])) {
                        throw new AppletParameterException(
                                parameterNames[i], value,
                                value.indexOf(keys[j]),
                                value.indexOf(keys[j]) + keys[j].length());
                    }
                    attributes.setStickerFillColor(
                            attributes.getStickerOffset(face) + j,
                            colorMap.get(keys[j]));
                }
            }
        }

        // Get Stickers image
        value = Applets.getParameter(this, "stickersImage");
        if (value != null && value.length() != 0) {
            try {
                attributes.setStickersImage(getImage(new URL(getDocumentBase(), value)));
                attributes.setStickersImageVisible(true);
            } catch (MalformedURLException e) {
                throw new AppletParameterException("stickersImage", value);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else {
            // look for stickers.png and stickers.jpg in the .jar bundle
            InputStream sImgIn = AbstractPlayerApplet.class.getResourceAsStream("/StickersImage.png");
            if (sImgIn == null) {
                sImgIn = AbstractPlayerApplet.class.getResourceAsStream("/StickersImage.jpg");
            }
            if (sImgIn == null) {
                sImgIn = AbstractPlayerApplet.class.getResourceAsStream("/StickersImage.gif");
            }
            if (sImgIn != null) {
                try {
                    attributes.setStickersImage(ImageIO.read(sImgIn));
                    attributes.setStickersImageVisible(true);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        // Set stickers image bevelling
        value = Applets.getParameter(this, "stickerBevel");
        if (value != null) {
            try {
                cube3D.setStickerBeveling(Float.valueOf(value).floatValue() / 512f);
            } catch (NumberFormatException ex) {
                System.err.println("Illegal 'stickerBevel' parameter:" + value);
            }
        }

        // Show/Hide corner parts
        value = Applets.getParameter(this, "visibleCornerParts");
        if (Applets.getParameter(this,
                "visibleCornerParts") != null) {
            String[] cornerNames = {"fru", "dfr", "bru", "bdr", "blu", "bdl", "flu", "dfl"};
            String[] values = Applets.getParameters(this, "visibleCornerParts", new String[0]);
            for (int i = 0; i < 8; i++) {
                attributes.setPartVisible(i, false);
            }
            for (int i = 0; i < values.length; i++) {
                char[] name = values[i].toCharArray();
                Arrays.sort(name);
                int corner = ArrayUtil.indexOf(cornerNames, new String(name));
                if (corner == -1) {
                    throw new AppletParameterException("visibleCornerParts", value,
                            value.indexOf(values[i]),
                            value.indexOf(values[i]) + values[i].length());
                } else {
                    attributes.setPartVisible(corner, true);
                }
            }
        }

        // Determine the script language
        value = Applets.getParameter(this, "notation");
        notation = new CubeMarkupNotation(getCube());
        if (value == null || value.length() == 0) {
            try {
                notation.readXML(resources);
            } catch (Exception e) {
                if (defaultResources != null) {
                    notation.readXML(defaultResources);
                }
            }
        } else {
            notation.readXML(resources, value);
//            if (notation.getName().equals(value))
        }

        if (value != null && notation.getLayerCount() != cube3D.getCube().getLayerCount()) {
            throw new AppletParameterException(
                    "The Applet parameter \"notation\" must specify a notation for cubes with " + cube3D.getCube().getLayerCount() + " layers.",
                    "notation", value);
        }

        parser = new ScriptParser(notation);

        // Determine the type of the script. Not case sensitive!
        value = Applets.getParameter(this, "scriptType", "Generator").toLowerCase();
        if (value.equals(
                "solver")) {
            isSolver = true;
        } else if (value.equals(
                "generator")) {
            isSolver = false;
        } else {
            throw new AppletParameterException(
                    "The Applet parameter \"scriptType\" must be \"Generator\" or \"Solver\".",
                    "scriptType", value);
        }

        // Read the script
        script = Applets.getParameter(this, "script");
        if (script == null || script.length() == 0) {
            scriptTextArea.setVisible(false);
        } else {
            // Convert "\n" into newline character
            int p;
            while ((p = script.indexOf("\\n")) != -1) {
                script = script.substring(0, p) + '\n' + script.substring(p + 2);
            }
            try {
                Node root = parser.parse(new StringReader(script));
                scriptTextArea.setText(script);
                player.setScript(root);
            } catch (ParseException e) {
                AppletParameterException iae = new AppletParameterException(e.getMessage(), "script",
                        script, e.getStartPosition(), e.getEndPosition() + 1);
                iae.initCause(e);
                throw (iae);
            } catch (Exception e) {
                AppletParameterException iae = new AppletParameterException(e.getMessage(), "script",
                        script);
                iae.initCause(e);
                throw (iae);
            }
        }

        // Read the init script and apply it to the resetCube.
        initScript = Applets.getParameter(this, "initScript");
        if (initScript == null || initScript.length() == 0) {
            player.getResetCube().reset();
        } else {
            player.getResetCube().reset();

            // Convert "\n" into newline character
            int p;
            while ((p = initScript.indexOf("\\n")) != -1) {
                script = initScript.substring(0, p) + '\n' + initScript.substring(p + 2);
            }
            try {
                Node root = parser.parse(new StringReader(initScript));
                root.applyTo(player.getResetCube(), false);
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.close();
                throw new AppletParameterException(e.getMessage(), "initScript", initScript);
            }
        }

        // If the script is of type "Solver" we apply the
        // inverse of the script to the resetCube.
        if (isSolver) {
            player.getScript().applyTo(player.getResetCube(), true);
        }

        // Reset the player
        // This ensures that the resetCube is applied to the player.
        player.reset();

        // Determine automatic playback
        isAutoPlay = Applets.getParameter(this, "autoPlay", false);

        // Determine the script progress
        // The default value for script progress depends on the type of the
        // script. For a generator script it is -1, for a solver script it is
        // 0.
        try {
            int i = Applets.getParameter(this, "scriptProgress",
                    (isSolver || isAutoPlay) ? 0 : -1);
            if (i < 0) {
                i = player.getTimeModel().getMaximum() - i + 1;
            }
            player.getTimeModel().setValue(i);
        } catch (IndexOutOfBoundsException e) {
            throw new AppletParameterException(e.getMessage(), "scriptProgress", Applets.getParameter(this, "scriptProgress"));
        }

        // Get the number of display lines
        value = Applets.getParameter(this, "displayLines");
        int intValue = (script == null) ? 0 : new StringTokenizer(script, "\n").countTokens();
        if (value != null) {
            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new AppletParameterException(e.getMessage(), "displayLines", Applets.getParameter(this, "displayLines"));
            }
        }
        if (intValue <= 0) {
            scriptTextArea.setVisible(false);
        } else {
            // if "displayLines" is greater 0, we always show the text area,
            // even if no script has been specified.
            if (intValue > 0) {
                scriptTextArea.setVisible(true);
            }
            try {
                scriptTextArea.setMinRows(intValue);
            } catch (NoSuchMethodError e) {
            }
        }
        // Get background image
        value = Applets.getParameter(this, "backgroundImage");
        if (value != null && value.length() != 0) {
            try {
                Image img = getImage(new URL(getDocumentBase(), value));
                attributes.setFrontBgImage(img);
                attributes.setRearBgImage(img);
            } catch (MalformedURLException e) {
                throw new AppletParameterException("Malformed URL", "backgroundImage", value);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * Returns the Default XML Resource Data of this Applet as an Input Stream.
     */
    protected InputStream getPlayerResources() {
        return getClass().getResourceAsStream("/ch/randelshofer/rubik/player/RubikPlayerResources.xml");
    }

    /**
     * Returns version and copyright information.
     */
    @Override
    public String getAppletInfo() {
        return getAppletName() + " " + getAppletVersion() + "\n"
                + getAppletCopyright();
    }

    public String getAppletCopyright() {
        return "© Werner Randelshofer";
    }

    public String getAppletName() {
        String name = getClass().getName();
        int p = name.lastIndexOf('.');
        if (p != -1) {
            name = name.substring(p + 1);
        }

        if (name.endsWith("Applet")) {
            name = name.substring(0, name.length() - 6);
        }

        return name;
    }

    /**
     * Reads cube attributes from the specified XML Element. The XMLElement must
     * be the root of a CubeTwister document.
     *
     * @param resources The XML element which holds the attribute data.
     * @param attr The CubeAttributes onto which we store the attribute values.
     */
    protected void readCubeAttributes(XMLElement resources, CubeKind kind, DefaultCubeAttributes attr) {
        XMLElement cubeElem = null;

        // Find the cube in the resource file
        // ----------------------------------
        if (resources != null) {
            String cubeName = Applets.getParameter(this, "cube");
            if (cubeName == null) {
                for (XMLElement elem : resources.iterableChildren()) {
                    if ("Cube".equals(elem.getName())
                            && kind.isNameOfKind(elem.getStringAttribute("kind", kind.getAlternativeName(0)))) {
                        if (elem.getBooleanAttribute("default", false)) {
                            cubeElem = elem;
                            break;
                        }
                    }
                }
            } else {
                CubeLoop:
                for (XMLElement elem : resources.iterableChildren()) {
                    if ("Cube".equals(elem.getName())) {
                        for (XMLElement elem2 : elem.iterableChildren()) {
                            if ("Name".equals(elem2.getName())
                                    && elem2.getContent().equals(cubeName)) {
                                cubeElem = elem;
                                break CubeLoop;
                            }

                        }
                    }
                }
            }
        }
        if (cubeElem == null) {
            return;
        }

        // Read the cube attributes
        // ------------------------
        XMLElement elem = cubeElem;

        HashMap<String, Color> colorMap = new HashMap<String, Color>(); // new HashMap<String,Color>();

        attr.setScaleFactor(0.01f * elem.getIntAttribute("scale", 25, 300, 100));
        attr.setExplosionFactor(0.01f * elem.getIntAttribute("explode", 0, 200, 0));
        attr.setAlpha(elem.getIntAttribute("alpha", -90, 90, -25) / 180f * (float) Math.PI);
        attr.setBeta(elem.getIntAttribute("beta", -90, 90, 45) / 180f * (float) Math.PI);
        attr.setFrontBgColor(elem.getColorAttribute("backgroundColor", Color.WHITE));

        for (XMLElement elem2 : elem.iterableChildren()) {

            String name = elem2.getName();
            if ("Color".equals(name)) {
                colorMap.put(
                        elem2.getStringAttribute("id"),
                        new Color((int) Long.parseLong(elem2.getStringAttribute("argb", "ff000000"), 16), true));

            } else if ("Part".equals(name)) {
                int partIndex = elem2.getIntAttribute("index");
                if (0 <= partIndex && partIndex < attr.getPartCount()) {
                    attr.setPartVisible(partIndex, elem2.getBooleanAttribute("visible", true));
                    Color color = colorMap.get(elem2.getStringAttribute("fillColorRef"));
                    if (color != null) {
                        attr.setPartFillColor(partIndex, color);
                    }

                    color = colorMap.get(elem2.getStringAttribute("outlineColorRef"));
                    if (color != null) {
                        attr.setPartOutlineColor(partIndex, color);
                    }

                }
            } else if ("Sticker".equals(name)) {
                int stickerIndex = elem2.getIntAttribute("index");
                if (0 <= stickerIndex && stickerIndex < attr.getStickerCount()) {
                    attr.setStickerVisible(stickerIndex, elem2.getBooleanAttribute("visible", true));
                    Color color = colorMap.get(elem2.getStringAttribute("fillColorRef"));
                    if (color != null) {
                        attr.setStickerFillColor(stickerIndex, color);
                        //System.out.println("AbstractPlayerApplet setting stickerFillColor " + stickerIndex + " ref:" + elem2.getStringAttribute("fillColorRef") + " to " + color);
                    }

                }
            } else if ("StickersImage".equals(name)) {
                attr.setStickersImageVisible(elem2.getBooleanAttribute("visible", true));
                attr.setStickersImage(decodeBase64Image(elem2.getContent()));
            } else if ("FrontBgImage".equals(name)) {
                attr.setFrontBgImageVisible(elem2.getBooleanAttribute("visible", true));
                attr.setFrontBgImage(decodeBase64Image(elem2.getContent()));
            } else if ("RearBgImage".equals(name)) {
                attr.setRearBgImageVisible(elem2.getBooleanAttribute("visible", true));
                attr.setRearBgImage(decodeBase64Image(elem2.getContent()));
            }

        }

    }

    protected Image decodeBase64Image(String base64) {
        return null;
    }

    private String repeat(String str, int times) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < times; i++) {
            if (i != 0) {
                buf.append(',');
            }
            buf.append(str);
        }
        return buf.toString();
    }

    @Override
    public String getParameter(String name) {
        try {
            return super.getParameter(name);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Returns information about the parameters supported by this applet.
     */
    @Override
    public String[][] getParameterInfo() {

        // Compute the power 2 of the layer count
        int lc = getLayerCount();
        int plc = lc * lc;

        String[][] info = {
            // Parameter Name,  Kind of Value, Description

            {"alpha", "int", "Vertical orientation of the cube, -90..+90. Default: -25"},
            {"ambientLightIntensity", "double", "DEPRECATED: Intensity of ambient light. Default: 0.6"},
            {"autoPlay", "boolean", "Set this to true, to start the player automatically. Default: false"},
            {"beta", "int", "Horizontal orientation of the cube, -90..+90. Default: 45"},
            {"backgroundColor", "int", "Background color. Default: #ffffff"},
            {"backgroundImage", "URL", "Background image. Default: none"},
            {"colorTable", "[name=]int, ...", "DEPRECATED: RGB color look up table, 6..n entries. Each entry consists of an optional name and a RGB value. FRDBLU. Default: #003373,#ff4600,#f8f8f8,#00732f,#8c000f,#ffd200"},
            {"colorList", "[name=]int, ...", "RGB color list. Each entry consists of an optional name and a RGB value. RUFDLB. Default: r=#ff4600,u=#ffd200,f=#003373,l=#8c000f,d=#f8f8f8,b=#00732f"},
            {"cube", "string", "Name of a cube in the resource file. If you don't specify this parameter, the default cube of the resource file is used."},
            {"debug", "boolean", "Set this to 'true' to print debug informations on the console. Default: false."},
            {"displayLines", "int", "Number of lines of the Script display: set to 0 to switch the display off. Default: 1"},
            {"faces", "name, ...", "DEPRECATED: Maps colors from the color table to the faces of the cube; 6 indices or color names; f, r, d, b, l, u. Default: 0,1,2,3,4,5"},
            {"faceList", "name, ...", "Maps colors from the color map to the faces of the cube; 6 indices or color names; r, u, f, l, d, b. Default: 0,1,2,3,4,5"},
            {"initScript", "string", "This script is used to initialize the cube, and when the reset button is pressed. If you don't specify this parameter, no script is used."},
            {"lightSourceIntensity", "double", "DEPRECATED: Intensity of the light source: set to 0 to switch the light source off. Default: 0.6"},
            {"lightSourcePosition", "int,int,int", "DEPRECATED: X, Y and Z coordinate of the light source. Default: -500, 500, 1000"},
            {"locale", "language", "Locale, for example \"en\", \"de\", \"fr\"."},
            {"notation", "string", "Name of a notation in the resource file. If you don't specify this parameter, the default notation of the resource file is used."},
            {"rearView", "boolean", "DEPRECATED: Set this value to true, to turn the rear view on. Default: false"},
            //{"rearViewBackgroundColor", "int", "Background color. Default: use value of parameter 'backgroundColor'"},
            //{"rearViewBackgroundImage", "URL", "Background image. Default: use value of parameter 'backgroundImage'"},
            {"rearViewScaleFactor", "float", "Scale factor of the rear view. Value between 0.1 and 1.0. Default: 0.125"},
            {"rearViewRotation", "int,int,int", "Rotation of the rear view. Default: 0,0,180"},
            // resource archive is an implicit parameter
            // {"resourceArchive", "URL", "Address of a Zip archive which contains the Cube Markup XML resource file."},
            {"resourceFile", "URL", "Address of a Cube Markup XML resource file. This file can be Zip-compressed to improve the startup time of the applet. The file extension must be .xml or .zip."},
            {"resourceData", "xml", "Cube Markup XML resource data."},
            {"scaleFactor", "float", "Scale factor. Default: 1.0"},
            {"script", "string", "Script. Default value: no script."},
            {"scriptType", "string", "The type of the script: \"solver\" or \"generator\". Default: \"solver\"."},
            {"scriptProgress", "int", "Position of the progress bar. Default value:  end of script if scriptType is 'generator', 0 if script type is 'solver'."},
            {"showPlayer", "boolean", "Set this to false, to hide the player. Default: true"},
            {"showInfo", "boolean", "Set this to true, to display an info field on the right of the player. Default: false"},
            {"showRear", "boolean", "Set this to true, to display a rear view of the cube. Default: false"},
            {"showController", "boolean", "Set this to false, to hide the controller of the player. Default: true"},
            {"showScrambleButton", "boolean", "Set this to false, to hide the scramble button. Default: true"},
            {"showResetButton", "boolean", "Set this to false, to hide the reset button. Default: true."},
            {"showSettingsButton", "boolean", "Set this to false, to hide the settings button. Default: true"},
            {"stickers", "nameindex, ...", "DEPRECATED: Maps colors from the color table to the stickers of the cube; " + (plc * 6) +//
                " indices or color names; front, right, down, back, left, up. Default: " + repeat("0", plc) + ", " + repeat("1", plc) + ", " + repeat("2", plc) + ", " + repeat("3", plc) + ", " + repeat("4", plc) + ", " + repeat("5", plc)},
            {"stickerList", "name, ...", "Maps colors from the color map to the stickers of the cube; " + (plc * 6) +//
                " indices or color names; right, up, front, left, down, back. Default: " + repeat("0", plc) + ", " + repeat("1", plc) + ", " + repeat("2", plc) + ", " + repeat("3", plc) + ", " + repeat("4", plc) + ", " + repeat("5", plc)},
            {"stickerBevel", "int", "Beveling of stickers image on each sticker. Default: 2"},
            {"stickersFront", "(name|index), ...", "DEPRECATED: Maps colors from the color table to the stickers on the side of the cube; " + plc + " indices or color names. Default: " + repeat("0", plc)},
            {"stickersRight", "(name|index), ...", "DEPRECATED: Maps colors from the color table to the stickers on the side of the cube; " + plc + " indices or color names. Default: " + repeat("1", plc)},
            {"stickersDown", "(name|index), ...", "DEPRECATED: Maps colors from the color table to the stickers on the side of the cube; " + plc + " indices or color names. Default: " + repeat("2", plc)},
            {"stickersBack", "(name|index), ...", "DEPRECATED: Maps colors from the color table to the stickers on the side of the cube; " + plc + " indices or color names. Default: " + repeat("3", plc)},
            {"stickersLeft", "(name|index), ...", "DEPRECATED: Maps colors from the color table to the stickers on the side of the cube; " + plc + " indices or color names. Default: " + repeat("4", plc)},
            {"stickersUp", "(name|index), ...", "DEPRECATED: Maps colors from the color table to the stickers on the side of the cube; " + plc + " indices or color names. Default: " + repeat("5", plc)},
            {"stickersRightList", "(name|index), ...", "Maps colors from the color table to the stickers on the side of the cube; " + plc + " indices or color names. Default: " + repeat("r", plc)},
            {"stickersUpList", "(name|index), ...", "Maps colors from the color map to the stickers on the side of the cube; " + plc + " indices or color names. Default: " + repeat("u", plc)},
            {"stickersFrontList", "(name|index), ...", "Maps colors from the color map to the stickers on the side of the cube; " + plc + " indices or color names. Default: " + repeat("f", plc)},
            {"stickersLeftList", "(name|index), ...", "Maps colors from the color map to the stickers on the side of the cube; " + plc + " indices or color names. Default: " + repeat("l", plc)},
            {"stickersDownList", "(name|index), ...", "Maps colors from the color map to the stickers on the side of the cube; " + plc + " indices or color names. Default: " + repeat("d", plc)},
            {"stickersBackList", "(name|index), ...", "Maps colors from the color map to the stickers on the side of the cube; " + plc + " indices or color names. Default: " + repeat("b", plc)},
            {"twistDuration", "int", "Duration of a quarter-turn twist in milliseconds. Default: 400"},
            {"visibleCornerParts", "name, ...", "List of visible corner parts. Default: urf,dfr,ubr,drb,ulb,dbl,ufl,dlf"},//
            {"permissions", "String", "Mandatory security parameter. The value must be set to 'sandbox'."},//
        };

        // Additional parameters for cubes which are not 'flat'
        if (!getClass().getName().endsWith("Flat")) {
            String[][] tmp = new String[info.length + 1][0];
            System.arraycopy(info, 0, tmp, 0, info.length);
            info = tmp;
            info[info.length - 1] = new String[]{"stickersImage", "URL", "Stickers image. Default value: none"};
        }

        // Additional parameters for cubes with more than 2 layers
        if (lc > 2) {
            String[][] tmp = new String[info.length + 2][0];
            System.arraycopy(info, 0, tmp, 0, info.length);
            info = tmp;
            String defaultValue, baseValue;
            defaultValue = baseValue = "ur,rf,dr,bu,rb,bd,ul,lb,dl,fu,lf,fd";
            if (lc > 3) {
                if (lc % 2 == 0) {
                    defaultValue = "";
                    for (int i = 1; i <= lc - 2; i++) {
                        if (i > 1) {
                            defaultValue += ", ";
                        }
                        defaultValue += baseValue.replaceAll("([a-z]+)", "$1" + i);
                    }

                } else {
                    for (int i = 1; i < lc - 2; i++) {
                        defaultValue += ", ";
                        defaultValue += baseValue.replaceAll("([a-z]+)", "$1" + i);
                    }
                }
            }
            info[info.length - 2] = new String[]{"visibleEdgeParts", "name, ...", "List of visible edge parts. Default: " + defaultValue};
            defaultValue = baseValue = "r,u,f,l,d,b";
            if (lc > 3) {
                if (lc % 2 == 0) {
                    defaultValue = "";
                    for (int i = 1, n = (lc - 2) * (lc - 2); i <= n; i++) {
                        if (i > 1) {
                            defaultValue += ", ";
                        }
                        defaultValue += baseValue.replaceAll("([a-z]+)", "$1" + i);
                    }

                } else {
                    for (int i = 1; i < plc; i++) {
                        defaultValue += ", ";
                        defaultValue += baseValue.replaceAll("([a-z]+)", "$1" + i);
                    }
                }
            }
            info[info.length - 1] = new String[]{"visibleSideParts", "name, ...", "List of visible side parts. Default: " + defaultValue};
        }

        return info;
    }

    /**
     * Returns information about the JavaScript API supported by this applet.
     */
    public String[][] getAPIInfo() {
        String[][] info = {
            // API, Description

            {"getCurrentPosition():int", "Returns the current position of the player whithin the script."},
            {"setCurrentPosition(int):void", "Sets the current position of the player."},
            {"getEndPosition():int", "Returns the end position of the script."},
            {"getStartPosition():int", "Returns the start position of the script. Usually, this is 0."},
            {"getPermutation():String", "Returns the current permutation (state) of the cube."},
            {"setPermutation(String):void", "Sets the permutation (state) of the cube."},
            {"getScript():String", "Returns the script."},
            {"setScript(String):void", "Sets the script."},
            {"getScriptType():String", "Returns the script type: \"generator\" or \"solver\"."},
            {"setScriptType(String):void", "Sets the script type. Permitted values are \"generator\" and \"solver\"."},
            {"isPlaying():boolean", "Returns whether the player is currently playing the script."},
            {"pause():void", "Pauses the player."},
            {"play():void", "Starts the player."},
            {"reset():void", "Resets the player and the cube."},
            {"resetCube():void", "Resets the cube."},};
        return info;
    }

    protected void updateInfo() {
        if (infoField.isShowing()) {
            try {
                synchronized (player.getCube()) {
                    MutableAttributeSet smallSpacing = new SimpleAttributeSet();
                    StyleConstants.setFontSize(smallSpacing, 6);
                    MutableAttributeSet bold = new SimpleAttributeSet();
                    StyleConstants.setBold(bold, true);
                    MutableAttributeSet plain = new SimpleAttributeSet();
                    MutableAttributeSet red = new SimpleAttributeSet();
                    StyleConstants.setForeground(red, Color.red);
                    ch.randelshofer.rubik.Cube cube = player.getCube();
                    MutableAttributeSet code = new SimpleAttributeSet();
                    StyleConstants.setFontFamily(code, Fonts.getMonospaceFont().getFamily());
                    StyleConstants.setFontSize(code, 10);

                    if (!(infoField.getDocument() instanceof StyledDocument)) {
                        infoField.setDocument(new DefaultStyledDocument());
                    }

                    if (turnCountInfo == null) {
                        Node scr = player.getScript();
                        if (scr == null) {
                            turnCountInfo = "";
                        } else {
                            turnCountInfo
                                    = scr.getBlockTurnCount() + " btm, "
                                    + scr.getLayerTurnCount() + " ltm, "
                                    + scr.getFaceTurnCount() + " ftm, "
                                    + scr.getQuarterTurnCount() + " qtm";
                        }

                    }

                    StyledDocument doc = (StyledDocument) infoField.getDocument();
                    doc.getStyle(StyleContext.DEFAULT_STYLE).addAttribute(StyleConstants.FontFamily, Fonts.getSmallDialogFont().getFamily());
                    doc.getStyle(StyleContext.DEFAULT_STYLE).addAttribute(StyleConstants.FontSize, new Integer(Fonts.getSmallDialogFont().getSize()));
                    doc.remove(0, doc.getLength());
                    if (turnCountInfo.length() > 0) {
                        doc.insertString(doc.getLength(), "Twists: ", bold);
                        doc.insertString(doc.getLength(), turnCountInfo, plain);
                        doc.insertString(doc.getLength(), "\n\n", smallSpacing);
                    }

                    doc.insertString(doc.getLength(), "Order: ", bold);
                    doc.insertString(doc.getLength(), Integer.toString(Cubes.getVisibleOrder(cube)), plain);
                    doc.insertString(doc.getLength(), " v, ", plain);
                    doc.insertString(doc.getLength(), Integer.toString(Cubes.getOrder(cube)), plain);
                    doc.insertString(doc.getLength(), " r", plain);

                    doc.insertString(doc.getLength(), "\n\n", smallSpacing);
                    if (cube.getLayerCount() == 2) {
                        doc.insertString(doc.getLength(), "Permutation:\n", bold);
                        doc.insertString(doc.getLength(), Cubes.toPermutationString(cube, notation), plain);

                    } else {
                        String noPermutation = notation.getToken(Symbol.PERMUTATION_BEGIN)
                                + notation.getToken(Symbol.PERMUTATION_END);
                        String permutation;

                        doc.insertString(doc.getLength(), "Corner Permutation:\n", bold);
                        permutation
                                = Cubes.toCornerPermutationString(cube, notation);
                        if (permutation.length() == 0) {
                            permutation = noPermutation;
                        }

                        doc.insertString(doc.getLength(), permutation, plain);
                        doc.insertString(doc.getLength(), "\n\n", smallSpacing);
                        doc.insertString(doc.getLength(), "Edge Permutation:\n", bold);
                        permutation
                                = Cubes.toEdgePermutationString(cube, notation);
                        if (permutation.length() == 0) {
                            permutation = noPermutation;
                        }

                        doc.insertString(doc.getLength(), permutation, plain);
                        doc.insertString(doc.getLength(), "\n\n", smallSpacing);
                        doc.insertString(doc.getLength(), "Side Permutation:\n", bold);
                        permutation
                                = Cubes.toSidePermutationString(cube, notation);
                        if (permutation.length() == 0) {
                            permutation = noPermutation;
                        }

                        doc.insertString(doc.getLength(), permutation, plain);
                    }

                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * JavaScript API: Starts/continues playback of a script.
     */
    public void play() {
        if (player != null) {
            player.start();
        }
    }

    /**
     * JavaScript API: Pauses playback of a script.
     */
    public void pause() {
        if (player != null) {
            player.stop();
        }
    }

    /**
     * JavaScript API: Returns true, if a script is currently being played.
     */
    public boolean isPlaying() {
        return player != null && player.isActive();
    }

    /**
     * JavaScript API: Pauses playback, moves playback position to the beginning
     * of a script and resets the cube.
     */
    public void reset() {
        if (player != null) {
            player.reset();
        }
    }

    /**
     * JavaScript API: Resets the cube.
     */
    public void resetCube() {
        if (player != null) {
            player.getCube().reset();
        }
    }

    /**
     * JavaScript API: Sets a script.
     */
    public void setScript(String newValue) {
        if (player != null) {
            player.stop();
            player.setScript(null);

            script = newValue;
            scriptTextArea.setText(script);
            try {
                SequenceNode parsedScript = parser.parse(script);
                player.setScript(parsedScript);
                player.getResetCube().reset();
                if (isSolver) {
                    player.getScript().applyTo(player.getResetCube(), true);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            player.reset();
        }
    }

    /**
     * JavaScript API: Gets a script.
     */
    public String getScript() {
        return script;
    }

    /**
     * JavaScript API: Sets the script type.
     */
    public void setScriptType(String newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException("Illegal scriptType:null. \"solver\" or \"generator\" expected");
        } else if ("solver".equals(newValue)) {
            isSolver = true;
        } else if ("generator".equals(newValue)) {
            isSolver = false;
        } else {
            throw new IllegalArgumentException("Illegal scriptType:" + newValue + ". \"solver\" or \"generator\" expected");
        }

        // set the script again to update the player
        if (script != null) {
            int timeValue = player.getTimeModel().getValue();
            setScript(script);
            player.getResetCube().reset();
            if (isSolver) {
                player.getScript().applyTo(player.getResetCube(), true);
            }
            player.getTimeModel().setValue(timeValue);
        }
        player.reset();
    }

    /**
     * JavaScript API: Gets the script type.
     */
    public String getScriptType() {
        return isSolver ? "solver" : "generator";
    }

    /**
     * JavaScript API: Gets the current permutation of the cube.
     */
    public String getPermutation() {
        return Cubes.toPermutationString(cube3D.getCube(), notation);
    }

    /**
     * JavaScript API: Sets the current permutation of the cube.
     */
    public void setPermutation(String newValue) {
        if (parser != null) {
            try {
                SequenceNode seq;
                seq = parser.parse(newValue);
                cube3D.getCube().reset();
                seq.applyTo(cube3D.getCube(), false);
            } catch (IOException ex) {
                //
            }
        }
    }

    /**
     * JavaScript API: Gets the current position of the playhead.
     */
    public int getCurrentPosition() {
        return (player == null) ? 0 : player.getTimeModel().getValue();
    }

    /**
     * JavaScript API: Sets the current position of the playhead.
     */
    public void setCurrentPosition(int newValue) {
        if (player != null) {
            player.getTimeModel().setValue(newValue);
        }
    }

    /**
     * JavaScript API: Gets the start position of the playhead.
     */
    public int getEndPosition() {
        return (player == null) ? 0 : player.getTimeModel().getMaximum();
    }

    /**
     * JavaScript API: Gets the end position of the playhead.
     */
    public int getStartPosition() {
        return (player == null) ? 0 : player.getTimeModel().getMinimum();
    }

    /**
     * This method is called from within the init() method to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playerPanel = new javax.swing.JPanel();
        infoPanel = new javax.swing.JPanel();
        infoScrollPane = new javax.swing.JScrollPane();
        infoField = new javax.swing.JTextPane();

        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        playerPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(playerPanel);

        infoPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
        infoPanel.setLayout(new java.awt.BorderLayout());

        infoScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        infoField.setEditable(false);
        infoScrollPane.setViewportView(infoField);

        infoPanel.add(infoScrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(infoPanel);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane infoField;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JScrollPane infoScrollPane;
    private javax.swing.JPanel playerPanel;
    // End of variables declaration//GEN-END:variables
}
