/* @(#)AbstractVirtualCubeApplet.java
 * Copyright (c) 2007 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.virtual;

import base64.Base64;
import ch.randelshofer.gui.border.BackdropBorder;
import ch.randelshofer.gui.border.ButtonStateBorder;
import ch.randelshofer.gui.border.ImageBevelBorder;
import ch.randelshofer.rubik.AbstractCubeIdx3D;
import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.Cube3DEvent;
import ch.randelshofer.rubik.Cube3DListener;
import ch.randelshofer.rubik.CubeAttributes;
import ch.randelshofer.rubik.CubeEvent;
import ch.randelshofer.rubik.CubeListener;
import ch.randelshofer.rubik.Cubes;
import ch.randelshofer.rubik.DefaultCubeAttributes;
import ch.randelshofer.rubik.notation.DefaultNotation;
import ch.randelshofer.rubik.parser.Node;
import ch.randelshofer.rubik.parser.ScriptKeyboardHandler;
import ch.randelshofer.rubik.parser.ScriptParser;
import ch.randelshofer.util.AppletParameterException;
import ch.randelshofer.util.Applets;
import ch.randelshofer.util.Cookies;
import ch.randelshofer.util.Images;
import idx3d.idx3d_JCanvas;
import idx3d.idx3d_Scene;
import nanoxml.XMLElement;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;
import org.monte.media.av.Interpolator;
import org.monte.media.interpolator.SplineInterpolator;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This is the base class for Applets featuring a Rubik's Cube like puzzle
 * which can be taken apart.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractVirtualCubeApplet extends javax.swing.JApplet {

    @Nullable
    protected AbstractCubeIdx3D cube3d;
    protected int tool;
    @Nullable
    protected idx3d_JCanvas canvas;
    //protected idx3d_CanvasAWT canvas;
    protected Random random;
    @Nullable
    protected idx3d_Scene scene;
    @Nullable
    protected ResourceBundle labels;
    protected float defaultScaleFactor;
    /**
     * Index of the part which is being lifted.
     */
    private int liftingPart;
    /**
     * Index of the part which is being pressed.
     */
    private int pressedPart;
    /**
     * These attributes are used to reset the cube.
     */
    private CubeAttributes resetAttributes;
    @Nullable
    private EventHandler previousHandler;
    private ScriptParser parser;
    @Nullable
    private String cookieName;

    public class EventHandler implements Cube3DListener, MouseListener, MouseMotionListener {

        protected boolean isEntered;
        protected boolean isDragged;

        public void activate() {
        }

        public void deactivate() {
        }

        @Override
        public void actionPerformed(Cube3DEvent evt) {
        }

        @Override
        public void mousePressed(Cube3DEvent evt) {
        }

        @Override
        public void mouseReleased(Cube3DEvent evt) {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseExited(@Nonnull Cube3DEvent evt) {
            if (evt.getStickerIndex() != -1) {
                isEntered = false;
                updateCursor();
            }
        }

        @Override
        public void mouseEntered(@Nonnull Cube3DEvent evt) {
            if (evt.getStickerIndex() != -1) {
                isEntered = true;
                updateCursor();
            }
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            isDragged = false;
            updateCursor();
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            updateCursor();
        }

        protected void updateCursor() {
            if (isDragged) {
                canvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else if (isEntered) {
                canvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                canvas.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!isDragged) {
                isDragged = true;
                updateCursor();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }
    protected EventHandler cube3DHandler;
    protected EventHandler twistHandler;
    protected EventHandler linkHandler;
    protected EventHandler stickersHandler;
    protected EventHandler partsHandler;
    /**
     * Each array element holds a link on a sticker of the cube.
     * If an element is null, the sticker does not represent a link.
     */
    protected String[] stickerLinks;
    /**
     * Target frame for sticker links. Is not null, when sticker links are present.
     */
    @Nullable
    protected String linkTarget;

    public AbstractVirtualCubeApplet() {
        System.out.println(getAppletInfo());
        JPanel p = (JPanel) getContentPane();
        p.setBackground(Color.WHITE);
        p.setOpaque(true);
    }

    /** Initializes the applet. */
    @Override
    public void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
        }

        // Determine the cookie name
        try {
            cookieName = getDocumentBase() == null ? null : getDocumentBase().getPath();
            if (cookieName != null) {
                int p = cookieName.lastIndexOf('/');
                if (p != -1) {
                    cookieName = cookieName.substring(p + 1);
                }
                cookieName += ".state";
            }
        } catch (Throwable t) {
            cookieName = null;
        }

        Runnable initializer = new Runnable() {

            @Override
            public void run() {
                try {
                    doInit();
                } catch (final Throwable e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            removeAll();
                            add(new JLabel(e.getMessage()));
                            removeGlassPane();
                        }
                    });
                }
            }
        };
        //initializer.run();
        //SwingUtilities.invokeLater(initializer);
        new Thread(initializer).start();

        System.out.println(getAppletInfo());
    }

    private void removeGlassPane() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        setGlassPane(p);
        p.setVisible(false);
    }

    /** Flush resources used by the applet. */
    @Override
    public void stop() {
        if (cube3d != null) {
            cube3d.stopAnimation();
        }
        if (canvas != null) {
            canvas.flush();
        }
        Runtime.getRuntime().gc();
    }

    /** Destroys the applet. */
    @Override
    public void destroy() {
        if (cube3d != null) {
            cube3d.getAttributes().dispose();
            cube3d.dispose();
            cube3d = null;
        }
        if (canvas != null) {
            canvas.dispose();
            canvas = null;
        }
        scene = null;
        labels = null;
    }

    @Nullable
    @Override
    public String getParameter(String name) {
        try {
            return super.getParameter(name);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Initializes the applet. This method must be called on a worker thread.
     */
    protected void doInit() throws AppletParameterException {
        readParameters();
    }

    protected void readParameters() throws AppletParameterException {
        String value;
        Locale locale;

        // Read resource data
        Reader in = null;
        XMLElement resources = null;
        XMLElement defaultResources = null;
        String parameter = null;
        value = "";
        if (getParameter("resourceFile") != null && getParameter("resourceData") != null) {
            throw new AppletParameterException("resourceFile", "The parameters 'resourceFile' and 'resourceData' are mutually exclusive.");
        }
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
                resources = new XMLElement(null, true, false);
                resources.parseFromReader(in);
            } else if (resourceFile.length() != 0) {
                parameter = "resourceFile";
                in = new InputStreamReader(new URL(getDocumentBase(), resourceFile).openStream());
                resources = new XMLElement(null, true, false);
                resources.parseFromReader(in);
            } else if (resourceData.length() != 0) {
                parameter = "resourceData";
                System.out.println("resourceData=" + resourceData);
                in = new StringReader(value);
                resources = new XMLElement(null, true, false);
                resources.parseFromReader(in);
            } else {
                resources = defaultResources;
            }
        } catch (IOException e) {
            //throw new IllegalArgumentException("Invalid parameter '" + parameter + "', value '" + value + "'");
            throw new AppletParameterException(parameter, value, e);

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e2) {
            }
        }

        value = Applets.getParameter(this, "locale");
        locale = (value == null) ? Locale.getDefault() : new Locale(value, "");
        // FIXME - Use a locale variable of my own to set the language
        /*if (value != null) {
        Locale.setDefault(locale);
        }*/
        labels = ResourceBundle.getBundle("ch.randelshofer.rubik.virtual.Labels", locale);

        initComponents();


        Border eastBorder = new BackdropBorder(
                new ButtonStateBorder(
                new ImageBevelBorder(
                Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/Player.borderEast.png"),
                new Insets(1, 1, 1, 1), new Insets(0, 4, 1, 4)),
                new ImageBevelBorder(
                Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/Player.borderEastP.png"),
                new Insets(1, 1, 1, 1), new Insets(0, 4, 1, 4))));

        Border westBorder = new BackdropBorder(
                new ButtonStateBorder(
                new ImageBevelBorder(
                Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/Player.borderWest.png"),
                new Insets(1, 1, 1, 0), new Insets(0, 4, 1, 4)),
                new ImageBevelBorder(
                Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/Player.borderWestP.png"),
                new Insets(1, 1, 1, 0), new Insets(0, 4, 1, 4))));
/*
        ImageIcon resetIcon, partialResetIcon;
        resetButton.setIcon(resetIcon = new ImageIcon(Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/PlayerReset.png")));
        resetButton.setDisabledIcon(new ImageIcon(Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/PlayerReset.disabled.png")));
        partialResetIcon = new ImageIcon(Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/PlayerPartialReset.png"));
        resetButton.setUI((ButtonUI) CustomButtonUI.createUI(resetButton));
        resetButton.setBorder(westBorder);
        resetButton.setMargin(new Insets(0, 0, 0, 0));
        resetButton.setPreferredSize(new Dimension(16, 16));
        resetButton.setFocusable(false);
        resetButton.setToolTipText(resetButton.getText());
        resetButton.setText(null);
        scrambleButton.setIcon(new ImageIcon(Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/PlayerScramble.png")));
        scrambleButton.setDisabledIcon(new ImageIcon(Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/PlayerScramble.disabled.png")));
        scrambleButton.setUI((ButtonUI) CustomButtonUI.createUI(scrambleButton));
        scrambleButton.setBorder(westBorder);
        scrambleButton.setMargin(new Insets(0, 0, 0, 0));
        scrambleButton.setPreferredSize(new Dimension(16, 16));
        scrambleButton.setFocusable(false);
        scrambleButton.setToolTipText(scrambleButton.getText());
        scrambleButton.setText(null);
       optionsButton.setIcon(new ImageIcon(Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/PlayerActions.png")));
        optionsButton.setDisabledIcon(new ImageIcon(Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/PlayerActions.disabled.png")));
        optionsButton.setUI((ButtonUI) CustomButtonUI.createUI(optionsButton));
        optionsButton.setBorder(westBorder);
        optionsButton.setMargin(new Insets(0, 0, 0, 0));
        optionsButton.setPreferredSize(new Dimension(16, 16));
        optionsButton.setFocusable(false);
        optionsButton.setToolTipText(optionsButton.getText());
        optionsButton.setText(null);*/

        slidersPanel.setMinimumSize(new Dimension(10,slidersPanel.getPreferredSize().height));
/*
        JComponent[] components={explosionLabel,explosionSlider,scaleFactorLabel,scaleFactorSlider,
        twistTool,linkTool,partsTool,stickersTool};
        for (JComponent c:components) {
            c.putClientProperty("JComponent.sizeVariant","small");
        }*/

        partsTool.setVisible(Applets.getParameter(this, "enablePartRemoval", canBeDisassembled()));
        stickersTool.setVisible(Applets.getParameter(this, "enableStickerRemoval", true));
        twistTool.setVisible(
                Applets.getParameter(this, "enablePartRemoval", canBeDisassembled())
                || Applets.getParameter(this, "enableStickerRemoval", true));
        scaleFactorLabel.setVisible(Applets.getParameter(this, "enableScaling", true));
        scaleFactorSlider.setVisible(Applets.getParameter(this, "enableScaling", true));
        explosionLabel.setVisible(Applets.getParameter(this, "enableExploding", canBeDisassembled()));
        explosionSlider.setVisible(Applets.getParameter(this, "enableExploding", canBeDisassembled()));

        canvas = new idx3d_JCanvas();
        canvas.setSwipeTimeout(Integer.MAX_VALUE);
        canvas.setUpdateCursor(false);
        //canvas = new idx3d_CanvasAWT();

        cube3d = createCube3D();
        DefaultCubeAttributes attr = (DefaultCubeAttributes) cube3d.getAttributes();
        readCubeAttributes(resources, attr);

        cube3d.addChangeListener(canvas);
        cube3d.addCube3DListener(getCube3DHandler());
        cube3d.setShowGhostParts(true);
        cube3d.setAnimated(true);

        // Set alpha and beta values
        // set initial orientation
        if (getParameter("alpha") != null) {
            attr.setAlpha(
                    Applets.getParameter(this, "alpha", -25) / 180f * (float) Math.PI);
        }
        if (getParameter("beta") != null) {
            attr.setBeta(
                    Applets.getParameter(this, "beta", 45) / 180f * (float) Math.PI);
        }
        defaultScaleFactor = (float) Applets.getParameter(this, "scaleFactor", attr.getScaleFactor());
        attr.setScaleFactor(defaultScaleFactor);

        // Set stickers image
        value = getParameter("stickersImage");
        if (value != null) {
            try {
                attr.setStickersImage(
                        getImage(getDocumentBase(), value));
                attr.setStickersImageVisible(true);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        // Set stickers image bevelling
        value = getParameter("stickerBevel");
        if (value != null) {
            try {
                cube3d.setStickerBeveling(Float.valueOf(value).floatValue() / 512f);
            } catch (NumberFormatException ex) {
                throw new AppletParameterException("stickerBevel", value, ex);
            }
        }

        // Read color lookup table
        HashMap<String,Color> colorMap = new HashMap<String,Color>();
        // set initial colors of color map
        for (int i = 0; i < attr.getFaceCount(); i++) {
            // Rubik's Cube Color scheme
            colorMap.put(Integer.toString(i), attr.getStickerFillColor(attr.getStickerOffset(i)));
        }
        HashMap<String,String> colorMapParam = Applets.getIndexedKeyValueParameters(this, "colorTable", null);

        // Convert the values of the color lookup tables from String to Color
        if (colorMapParam!=null)
        for (Map.Entry<String,String> entry : colorMapParam.entrySet()) {
            try {
                colorMap.put(entry.getKey(), new Color(Applets.decode(entry.getValue()).intValue()));
            } catch (NumberFormatException e) {
                throw new AppletParameterException("colorTable", "Value " + entry.getValue() + " for entry " + entry.getKey() + " is illegal.", e);
            }
        }

        // set sticker colors
        if (getParameter("colorTable") != null && getParameter("faces") == null) {
            for (int i = 0; i < attr.getFaceCount(); i++) {
                String key = Integer.toString(i);
                if (!colorMap.containsKey(key)) {
                    throw new IllegalArgumentException("Invalid parameter 'colorTable', entry number " + key + " missing.");
                }
                Color color = colorMap.get(key);
                for (int j = 0; j < attr.getStickerCount(i); j++) {
                    attr.setStickerFillColor(attr.getStickerOffset(i) + j, color);
                }
            }
        }

        // Set the colors for all stickers on each face of the cube.
        String[] keys = Applets.getParameters(this, "faces", (String[]) null);
        if (keys != null) {
            if (keys.length != 6) {
                throw new AppletParameterException("faces", "\"faces\" has " + keys.length + " instead of 6 entries.");
            }
            for (int i = 0; i < 6; i++) {
                if (!colorMap.containsKey(keys[i])) {
                    throw new AppletParameterException("faces", "unknown entry '" + keys[i] + "'.");
                }
                Color color = colorMap.get(keys[i]);
                for (int j = 0; j < 9; j++) {
                    attr.setStickerFillColor(i * 9 + j, color);
                }
            }
        }

        // Get links on the stickers of the cube.
        stickerLinks = new String[attr.getStickerCount()];
        boolean hasLinks = false;
        for (int i = 0; i < stickerLinks.length; i++) {
            stickerLinks[i] = getParameter("sticker[" + i + "].link");
            if (stickerLinks[i] != null) {
                hasLinks = true;
            }
        }
        linkTool.setVisible(hasLinks);
        if (hasLinks) {
            linkTarget = Applets.getParameter(this, "linkTarget", "_self");
        }


        scene = (idx3d_Scene) cube3d.getScene();
        canvas.setScene(scene);
        canvas.setCamera("Front");
        canvas.addMouseListener(getCube3DHandler());
        canvas.addMouseMotionListener(getCube3DHandler());

        ItemListener itemHandler = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                cube3d.setShowGhostParts(partsTool.isSelected());
            }
        };
        twistTool.addItemListener(itemHandler);
        partsTool.addItemListener(itemHandler);
        stickersTool.addItemListener(itemHandler);

        resetAttributes = (CubeAttributes) cube3d.getAttributes().clone();
        ActionListener resetButtonHandler = new ActionListener() {

            @Override
            public void actionPerformed(@Nonnull ActionEvent evt) {
                if ((evt.getModifiers() & (ActionEvent.ALT_MASK | ActionEvent.CTRL_MASK)) == 0) {
                    reset();
                } else {
                    resetCanvas();
                }
            }
        };
        resetButton.addActionListener(resetButtonHandler);

        ActionListener scrambleButtonHandler = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                scramble();
            }
        };
        scrambleButton.addActionListener(scrambleButtonHandler);

        String selectedTool = Applets.getParameter(this, "selectedTool", "twist");
        if ("parts".equals(selectedTool)) {
            cube3d.setShowGhostParts(true);
            partsTool.setSelected(true);
        } else if ("stickers".equals(selectedTool)) {
            cube3d.setShowGhostParts(false);
            stickersTool.setSelected(true);
        } else if ("link".equals(selectedTool)) {
            cube3d.setShowGhostParts(false);
            linkTool.setSelected(true);
        } else if ("twist".equals(selectedTool)) {
            cube3d.setShowGhostParts(false);
            twistTool.setSelected(true);
        }
        random = new Random();
        parser = new ScriptParser(new DefaultNotation(cube3d.getCube().getLayerCount()));
        ScriptKeyboardHandler skp = new ScriptKeyboardHandler();
        skp.setCube(cube3d.getCube());
        skp.setParser(parser);
        skp.addTo(canvas);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                getContentPane().removeAll();
                getContentPane().setLayout(new BorderLayout());
                getContentPane().add(canvas, java.awt.BorderLayout.CENTER);
                getContentPane().add(controlsPanel, BorderLayout.SOUTH);
                controlsPanel.setVisible(true);
                removeGlassPane();
                invalidate();
                validate();
                reset();
                readStateFromCookies();
                cube3d.getCube().addCubeListener(new CubeListener() {

                    @Override
                    public void cubeTwisted(CubeEvent evt) {
                        writeStateToCookies();
                    }

                    @Override
                    public void cubeChanged(CubeEvent evt) {
                        writeStateToCookies();
                    }
                });
                repaint();
            }
        });
    }

    protected void readStateFromCookies() {
        if (cookieName != null) {
            String state = Cookies.getCookie(this, cookieName, "");

            try {
                Node seq = parser.parse(state);
                seq.applyTo(cube3d.getCube(), false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void writeStateToCookies() {
        if (cookieName != null) {
            // Workaround for Safari 3 and 4: We need to disable the canvas while we
            // write cookies because Safari generates spurious mouse events.
            canvas.setEnabled(false);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 1);
            Cookies.putCookie(AbstractVirtualCubeApplet.this, cookieName, Cubes.toPermutationString((Cube) cube3d.getCube().clone(), parser.getNotation()), cal.getTime());
            canvas.setEnabled(true);
        }
    }

    /**
     * Reads cube attributes from the specified XML Element.
     *
     * @param resources The XML element which holds the attribute data.
     * @param attr      The CubeAttributes onto which we set the attribute values.
     */
    protected void readCubeAttributes(@Nullable XMLElement resources, @Nonnull DefaultCubeAttributes attr) {
        XMLElement cubeElem = null;

        // Find the cube in the resource file
        // -----------------------------------
        if (resources != null) {
            String cubeName = getParameter("cube");
            if (cubeName == null) {
                for (XMLElement elem : resources.iterableChildren()) {
                    if (elem.getName().equals("Cube")) {
                        if (elem.getBooleanAttribute("default", false)) {
                            cubeElem = elem;
                            break;
                        }
                    }
                }
            } else {
                CubeLoop:
        for (XMLElement elem : resources.iterableChildren()) {
                    if (elem.getName().equals("Cube")) {
        for (XMLElement elem2 : elem.iterableChildren()) {
                            if (elem2.getName().equals("Name")
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

        // Read cube attributes
        // --------------------
        XMLElement elem = cubeElem;

        HashMap<String,Color> colorMap = new HashMap<String,Color>();

        attr.setFrontBgColor(elem.getColorAttribute("backgroundColor", attr.getFrontBgColor()));
        attr.setScaleFactor(0.01f * elem.getIntAttribute("scale", 25, 300, 100));
        attr.setExplosionFactor(0.01f * elem.getIntAttribute("explode", 0, 200, 0));
        attr.setAlpha(elem.getIntAttribute("alpha", -90, 90, -25) / 180f * (float) Math.PI);
        attr.setBeta(elem.getIntAttribute("beta", -90, 90, 45) / 180f * (float) Math.PI);

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

    @Nullable
    protected Image decodeBase64Image(@Nonnull String base64) {
        try {
            byte[] bytes = Base64.decode(base64);
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException ex) {
            System.err.println("Warning: Can't decode base64 encoded image.");
            ex.printStackTrace();
            return null;
        }
    }

    abstract protected AbstractCubeIdx3D createCube3D();

    protected boolean canBeDisassembled() {
        return true;
    }

    @Nullable
    private EventHandler getCurrentHandler() {
        EventHandler currentHandler = null;

        if (twistTool.isSelected()) {
            currentHandler = getTwistHandler();
        } else if (linkTool.isSelected()) {
            currentHandler = getLinkHandler();
        } else if (stickersTool.isSelected()) {
            currentHandler = getStickersHandler();
        } else if (partsTool.isSelected()) {
            currentHandler = getPartsHandler();
        }

        if (currentHandler != previousHandler) {
            if (previousHandler != null) {
                previousHandler.deactivate();
            }
            if (currentHandler != null) {
                currentHandler.activate();
            }
            previousHandler = currentHandler;
        }

        return currentHandler;
    }

    protected EventHandler getCube3DHandler() {
        if (cube3DHandler == null) {
            cube3DHandler = new EventHandler() {

                @Override
                public void actionPerformed(Cube3DEvent evt) {
                    getCurrentHandler().actionPerformed(evt);
                }

                @Override
                public void mouseExited(@Nonnull Cube3DEvent evt) {
                    getCurrentHandler().mouseExited(evt);
                }

                @Override
                public void mouseEntered(@Nonnull Cube3DEvent evt) {
                    getCurrentHandler().mouseEntered(evt);
                }

                @Override
                public void mousePressed(Cube3DEvent evt) {
                    getCurrentHandler().mousePressed(evt);
                }

                @Override
                public void mouseReleased(Cube3DEvent evt) {
                    getCurrentHandler().mouseReleased(evt);
                }

                @Override
                public void mouseExited(MouseEvent evt) {
                    getCurrentHandler().mouseExited(evt);
                }

                @Override
                public void mouseEntered(MouseEvent evt) {
                    getCurrentHandler().mouseEntered(evt);
                }

                @Override
                public void mousePressed(MouseEvent evt) {
                    getCurrentHandler().mousePressed(evt);
                }

                @Override
                public void mouseReleased(MouseEvent evt) {
                    getCurrentHandler().mouseReleased(evt);
                }

                @Override
                public void mouseClicked(MouseEvent evt) {
                    getCurrentHandler().mouseClicked(evt);
                }

                @Override
                public void mouseDragged(MouseEvent evt) {
                    getCurrentHandler().mouseDragged(evt);
                }

                @Override
                public void mouseMoved(MouseEvent evt) {
                    getCurrentHandler().mouseMoved(evt);
                }
            };
        }
        return cube3DHandler;
    }

    protected EventHandler getTwistHandler() {
        if (twistHandler == null) {
            twistHandler = new EventHandler() {

                @Override
                public void activate() {
                    canvas.setUpdateCursor(true);

                }

                @Override
                public void deactivate() {
                    canvas.setUpdateCursor(false);
                }

                @Override
                protected void updateCursor() {
                    /** Cursor is updated by Canvas. */
                }

                @Override
                public void actionPerformed(@Nonnull Cube3DEvent evt) {
                    DefaultCubeAttributes attr = (DefaultCubeAttributes) cube3d.getAttributes();
                    int partIndex = evt.getPartIndex();
                    int stickerIndex = evt.getStickerIndex();
                    int sideIndex = evt.getSideIndex();
                    int offset, length;

                    Cube cube = cube3d.getCube();

                    if (sideIndex != -1) {
                        evt.applyTo(cube);
                    }
                }
            };
        }
        return twistHandler;
    }

    protected EventHandler getLinkHandler() {
        if (linkHandler == null) {
            linkHandler = new EventHandler() {

                @Override
                public void actionPerformed(@Nonnull Cube3DEvent evt) {
                    if (evt.getStickerIndex() == -1
                            || stickerLinks[evt.getStickerIndex()] == null) {
                        getToolkit().beep();
                    } else {
//                        pressedPart = evt.getPartIndex();
//                        cube3d.dispatch(new PartExploder());
                        try {
                            getAppletContext().showDocument(
                                    new URL(getDocumentBase(), stickerLinks[evt.getStickerIndex()]),
                                    linkTarget);
                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        } catch (MalformedURLException ex) {
                            ex.printStackTrace();
                        }

                    }
                }

                @Override
                public void mouseExited(@Nonnull Cube3DEvent evt) {
                    if (evt.getStickerIndex() != -1
                            && stickerLinks[evt.getStickerIndex()] != null) {
                        isEntered = false;
                        updateCursor();
                        // ((DefaultCubeAttributes) cube3d.getAttributes()).setPartExplosion(evt.getPartIndex(), 0f);
                    }
                }

                @Override
                public void mouseEntered(@Nonnull Cube3DEvent evt) {
                    if (evt.getStickerIndex() != -1
                            && stickerLinks[evt.getStickerIndex()] != null) {
                        DefaultCubeAttributes attr = (DefaultCubeAttributes) cube3d.getAttributes();
                        liftingPart = evt.getPartIndex();
                        cube3d.dispatch(new PartExploder());
                        isEntered = true;
                        updateCursor();
                    }
                }
            };
        }
        return linkHandler;
    }

    protected EventHandler getStickersHandler() {
        if (stickersHandler == null) {
            stickersHandler = new EventHandler() {

                @Override
                public void actionPerformed(@Nonnull Cube3DEvent evt) {
                    DefaultCubeAttributes attr = (DefaultCubeAttributes) cube3d.getAttributes();
                    int modifiersEx = evt.getModifiersEx();
                    int partIndex = evt.getPartIndex();
                    int stickerIndex = evt.getStickerIndex();
                    int sideIndex = evt.getSideIndex();
                    int offset, length;
                    Cube cube = cube3d.getCube();

                    if (evt.getStickerIndex() != -1) {
                        boolean endState = !attr.isStickerVisible(stickerIndex);

                        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
                        int relevantModifiersEx = (isMac) ? //
                                modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK) : //
                                modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
                        switch (relevantModifiersEx) {
                            case InputEvent.SHIFT_DOWN_MASK:
                                // Shift affects all stickers of the same color
                                offset = 0;
                                int face = Cubes.getFaceOfSticker(attr, stickerIndex);
                                offset = attr.getStickerOffset(face);
                                length = attr.getStickerCount(face);

                                for (int i = 0; i < length; i++) {
                                    attr.setStickerVisible(i + offset, endState);
                                }
                                break;


                            case InputEvent.ALT_DOWN_MASK:
                                // Alt affects all stickers on the parts of the same type
                                int indices[];
                                if (partIndex < cube.getCornerCount()) {
                                    indices = new int[cube.getCornerCount()];
                                    for (int i = 0; i < indices.length; i++) {
                                        indices[i] = i;
                                    }
                                } else if (partIndex < cube.getCornerCount() + cube.getEdgeCount()) {
                                    indices = new int[cube.getEdgeCount()];
                                    offset = cube.getCornerCount();
                                    for (int i = 0; i < indices.length; i++) {
                                        indices[i] = offset + i;
                                    }
                                } else if (partIndex < cube.getCornerCount() + cube.getEdgeCount() + cube.getSideCount()) {
                                    indices = new int[cube.getSideCount()];
                                    offset = cube.getCornerCount() + cube.getEdgeCount();
                                    for (int i = 0; i < indices.length; i++) {
                                        indices[i] = offset + i;
                                    }
                                } else {
                                    offset = cube.getCornerCount() + cube.getEdgeCount() + cube.getSideCount();
                                    indices = new int[attr.getPartCount() - offset];
                                    for (int i = 0; i < indices.length; i++) {
                                        indices[i] = offset + i;
                                    }
                                }
                                for (int i = 0; i < attr.getStickerCount(); i++) {
                                    int p = cube3d.getPartIndexForStickerIndex(i);
                                    int j;
                                    for (j = 0; j < indices.length; j++) {
                                        if (indices[j] == p) {
                                            break;
                                        }
                                    }
                                    if (j < indices.length) {
                                        attr.setStickerVisible(i, endState);
                                    }
                                }
                                break;

                            case InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK:
                                // Shift+Alt affects all stickers
                                for (int i = 0, n = attr.getStickerCount(); i < n; i++) {
                                    attr.setStickerVisible(i, endState);
                                }
                                break;

                            case 0:
                                // No modifiersEx affects a single sticker only
                                attr.setStickerVisible(stickerIndex, endState);
                                break;

                        }
                    }
                }
            };
        }
        return stickersHandler;
    }

    protected EventHandler getPartsHandler() {
        if (partsHandler == null) {
            partsHandler = new EventHandler() {

                @Override
                public void actionPerformed(@Nonnull Cube3DEvent evt) {
                    DefaultCubeAttributes attr = (DefaultCubeAttributes) cube3d.getAttributes();
                    int modifiersEx = evt.getModifiersEx();
                    int partIndex = evt.getPartIndex();
                    int stickerIndex = evt.getStickerIndex();
                    int sideIndex = evt.getSideIndex();
                    int offset, length;

                    Cube cube = cube3d.getCube();

                    boolean endState = !attr.isPartVisible(partIndex);
                    switch (modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) {
                        case InputEvent.SHIFT_DOWN_MASK:
                            // Shift affects all parts of the same color
                            offset = 0;
                            int face = Cubes.getFaceOfSticker(attr, stickerIndex);
                            offset = attr.getStickerOffset(face);
                            length = attr.getStickerCount(face);
                            for (int i = 0; i < length; i++) {
                                attr.setPartVisible(
                                        cube3d.getPartIndexForStickerIndex(i + offset),
                                        endState);
                            }
                            break;

                        case InputEvent.META_DOWN_MASK:
                        case InputEvent.CTRL_DOWN_MASK:
                            // Meta affects all parts on the same face
                            offset = attr.getStickerOffset(sideIndex);
                            for (int i = 0; i < attr.getStickerCount(sideIndex); i++) {
                                attr.setPartVisible(
                                        cube.getPartAt(cube3d.getPartIndexForStickerIndex(i + offset)),
                                        endState);
                            }
                            break;

                        case InputEvent.ALT_DOWN_MASK:
                        case InputEvent.ALT_GRAPH_DOWN_MASK:
                            // Alt affects all parts of the same type
                            int indices[];
                            if (partIndex < cube.getCornerCount()) {
                                indices = new int[cube.getCornerCount()];
                                for (int i = 0; i < indices.length; i++) {
                                    indices[i] = i;
                                }
                            } else if (partIndex < cube.getCornerCount() + cube.getEdgeCount()) {
                                indices = new int[cube.getEdgeCount()];
                                offset = cube.getCornerCount();
                                for (int i = 0; i < indices.length; i++) {
                                    indices[i] = offset + i;
                                }
                            } else if (partIndex < cube.getCornerCount() + cube.getEdgeCount() + cube.getSideCount()) {
                                indices = new int[cube.getSideCount()];
                                offset = cube.getCornerCount() + cube.getEdgeCount();
                                for (int i = 0; i < indices.length; i++) {
                                    indices[i] = offset + i;
                                }
                            } else {
                                offset = cube.getCornerCount() + cube.getEdgeCount() + cube.getSideCount();
                                indices = new int[attr.getPartCount() - offset];
                                for (int i = 0; i < indices.length; i++) {
                                    indices[i] = offset + i;
                                }
                            }
                            for (int i = 0; i < indices.length; i++) {
                                attr.setPartVisible(indices[i], endState);
                            }
                            break;

                        case InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK:
                        case InputEvent.ALT_DOWN_MASK | InputEvent.META_DOWN_MASK:
                        case InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK:
                        case InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK:
                        case InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK:
                        case InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.CTRL_DOWN_MASK:
                        case InputEvent.SHIFT_DOWN_MASK | InputEvent.META_DOWN_MASK:
                        case InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK:
                            // Shift+Alt affects all parts except the center part
                            for (int i = 0, n = cube3d.getPartCount() - 1; i < n; i++) {
                                attr.setPartVisible(i, endState);
                            }
                            break;

                        case 0:
                            // No modifiersEx affects a single part only
                            attr.setPartVisible(partIndex, endState);
                            break;
                    }
                }

                @Override
                public void mouseExited(@Nonnull Cube3DEvent evt) {
                    if (evt.getStickerIndex() == -1) {
                        isEntered = false;
                        updateCursor();
                    }
                }

                @Override
                public void mouseEntered(@Nonnull Cube3DEvent evt) {
                    if (evt.getStickerIndex() == -1) {
                        isEntered = true;
                        updateCursor();
                    }
                }
            };
        }
        return partsHandler;
    }

    protected void scramble() {
        Cube scrambler = (Cube) cube3d.getCube().clone();
        //RubiksCubeCore scrambler = (RubiksCubeCore) cube3d.getCube();
        int[] angles = {1, -1, 2};
        int lastAxis = -1;
        int axis;
        int layerCount = scrambler.getLayerCount();
        for (int i = 0; i < 18; i++) {
            while ((axis = random.nextInt(3)) == lastAxis) {
            }
            lastAxis = axis;
            scrambler.transform(
                    axis,
                    1 << random.nextInt(layerCount),
                    angles[random.nextInt(3)]);
        }
        cube3d.getCube().transform(scrambler);
    }

    protected void resetCanvas() {
        if (canvas != null) {
            canvas.reset();
            canvas.repaint();
        }
    }

    protected void reset() {
        if (resetAttributes != null) {
            cube3d.getAttributes().setTo(resetAttributes);
        }
        cube3d.getCube().reset();

        canvas.repaint();
        DefaultCubeAttributes attr = (DefaultCubeAttributes) cube3d.getAttributes();
        scene.matrix.reset();
        scene.normalmatrix.reset();
        liftingPart = -1;
        cube3d.dispatch(new PartExploder());
        scaleFactorSlider.setValue((int) (defaultScaleFactor * 100));
        attr.setScaleFactor(defaultScaleFactor);
        explosionSlider.setValue(0);
        attr.setExplosionFactor(0f);
    }

    /**
     * Returns information about the parameters that are understood by
     * this applet. An applet should override this method to return an
     * array of <code>Strings</code> describing these parameters.
     * <p>
     * Each element of the array should be a set of three
     * <code>Strings</code> containing the name, the type, and a
     * description. For example:
     * <p><blockquote><pre>
     * String pinfo[][] = {
     *	 {"fps",    "1-10",    "frames per second"},
     *	 {"repeat", "boolean", "repeat image loop"},
     *	 {"imgs",   "url",     "images directory"}
     * };
     * </pre></blockquote>
     * <p>
     * The implementation of this method provided by the
     * <code>Applet</code> class returns <code>null</code>.
     *
     * @return  an array describing the parameters this applet looks for.
     */
    @Nonnull
    @Override
    public String[][] getParameterInfo() {
        return new String[][]{
                    // Parameter Name,  Kind of Value, Description
                    {"resourceFile", "URL", "Address of a Cube Markup XML resource file. This file can be Zip-compressed to improve the startup time of the applet. The file extension must be .xml or .zip."},
                    {"resourceData", "xml", "Cube Markup XML resource data."},
                    {"cube", "string", "Name of a cube in the resource file. Default: default cube of the resource file"},
                    {"locale", "language", "Locale, for example \"en\", \"de\", \"fr\"."},
                    {"alpha", "int", "Vertical orientation of the cube, -90..+90. Default: -25"},
                    {"beta", "int", "Horizontal orientation of the cube, -90..+90. Default: 45"},
                    {"colorTable", "[name=]int, ...", "RGB color look up table, 6..n entries. Each entry consists of an optional name and a hex value. Default: 0x003373,0xff4600,0xf8f8f8,0x00732f,0x8c000f,0xffd200"},
                    {"faces", "name, ...", "Maps colors from the color table to the faces of the cube; 6 integer values; front, right, down, back, left, up. Default: 0,1,2,3,4,5"},
                    {"scaleFactor", "float", "Scale factor. Default: 1.0"},
                    {"stickersImage", "URL", "Stickers image to be applied on the stickers. Default: none"},
                    {"stickerBevel", "int", "Beveling of stickers image on each sticker. Default value: 2"},
                    {"enablePartRemoval", "boolean", "Enables removal of parts. Default value: true"},
                    {"enableStickerRemoval", "boolean", "Enables removal of stickers. Default value: true"},
                    {"enableScaling", "boolean", "Enables scaling. Default value: true"},
                    {"enableExploding", "boolean", "Enables explosion. Default value: true"},
                    {"sticker[0].link", "URL", "Turns the sticker with the specified index into a link. Default value: no link"},
                    {"linkTarget", "String", "Target frame for sticker links. Default value: \"_self\""},
                    {"selectedTool", "twist|parts|stickers|link", "Selects the specified tool. Default value: \"twist\""}
                };
    }

    @Nonnull
    protected String getAppletVersion() {
        String version = AbstractVirtualCubeApplet.class.getPackage().getImplementationVersion();
        return version == null ? "" : version;
    }

    /**
     * Returns information about this applet. An applet should override
     * this method to return a <code>String</code> containing information
     * about the author, version, and copyright of the applet.
     * <p>
     * The implementation of this method provided by the
     * <code>Applet</code> class returns <code>null</code>.
     *
     * @return  a string containing information about the author, version, and
     *          copyright of the applet.
     */
    @Nonnull
    @Override
    public String getAppletInfo() {
        return "VirtualCube " + getAppletVersion() + "\n"
                + "© Werner Randelshofer";
    }

    public static void main(@Nonnull final AbstractVirtualCubeApplet applet) {
        applet.init();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame f = new JFrame(applet.getAppletInfo());
                f.add(applet);
                f.setSize(400, 400);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setVisible(true);
            }
        });
    }

    private class PartExploder extends SplineInterpolator {

        private final static float maxExpand = 4f;

        public PartExploder() {
            super(0.25f, 0f, 0.75f, 1f, 500);
//            super(0, 1f, 500);
        }

        @Override
        protected void update(float value) {
            DefaultCubeAttributes attr = (DefaultCubeAttributes) cube3d.getAttributes();
            for (int i = 0, n = attr.getPartCount(); i < n; i++) {
                if (i != pressedPart && i == liftingPart) {
                    attr.setPartExplosion(
                            i,
                            Math.min(attr.getPartExplosion(i) + 0.4f, maxExpand));
                } else {
                    attr.setPartExplosion(
                            i,
                            Math.max(attr.getPartExplosion(i) - 0.4f, 0));
                }
            }
        }

        protected void destroy() {
        }

        @Override
        public boolean replaces(@Nonnull Interpolator that) {
            return that.getClass() == this.getClass();
        }
    }
    /*
    private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    toolGroup = new javax.swing.ButtonGroup();
    controlsPanel = new javax.swing.JPanel();
    panel1 = new javax.swing.JPanel();
    twistTool = new javax.swing.JRadioButton();
    linkTool = new javax.swing.JRadioButton();
    partsTool = new javax.swing.JRadioButton();
    stickersTool = new javax.swing.JRadioButton();
    panel2 = new javax.swing.JPanel();
    scaleFactorLabel = new javax.swing.JLabel();
    scaleFactorSlider = new javax.swing.JSlider();
    explosionLabel = new javax.swing.JLabel();
    explosionSlider = new javax.swing.JSlider();
    panel3 = new javax.swing.JPanel();
    resetButton = new javax.swing.JButton();
    scrambleButton = new javax.swing.JButton();

    controlsPanel.setLayout(new java.awt.GridBagLayout());

    controlsPanel.setBackground(Color.WHITE);
    panel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 6, 0));

    panel1.setBackground(Color.WHITE);
    twistTool.setBackground(Color.WHITE);
    toolGroup.add(twistTool);
    twistTool.setSelected(true);
    twistTool.setText(labels.getString("twist"));
    twistTool.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    twistTool.setMargin(new java.awt.Insets(0, 0, 0, 0));
    twistTool.addItemListener(new java.awt.event.ItemListener() {

    public void itemStateChanged(java.awt.event.ItemEvent evt) {
    toolChanged(evt);
    }
    });

    panel1.add(twistTool);

    linkTool.setBackground(Color.WHITE);
    toolGroup.add(linkTool);
    linkTool.setText(labels.getString("link"));
    linkTool.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    linkTool.setMargin(new java.awt.Insets(0, 0, 0, 0));
    panel1.add(linkTool);

    partsTool.setBackground(Color.WHITE);
    toolGroup.add(partsTool);
    partsTool.setText(labels.getString("parts"));
    partsTool.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    partsTool.setMargin(new java.awt.Insets(0, 0, 0, 0));
    panel1.add(partsTool);

    stickersTool.setBackground(Color.WHITE);
    toolGroup.add(stickersTool);
    stickersTool.setText(labels.getString("stickers"));
    stickersTool.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    stickersTool.setMargin(new java.awt.Insets(0, 0, 0, 0));
    panel1.add(stickersTool);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
    controlsPanel.add(panel1, gridBagConstraints);

    panel2.setLayout(new java.awt.GridBagLayout());

    panel2.setBackground(Color.WHITE);
    scaleFactorLabel.setBackground(Color.WHITE);
    scaleFactorLabel.setText(labels.getString("Scale"));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
    panel2.add(scaleFactorLabel, gridBagConstraints);

    scaleFactorSlider.setBackground(Color.WHITE);
    scaleFactorSlider.setMaximum(200);
    scaleFactorSlider.setMinimum(20);
    scaleFactorSlider.setValue(100);
    scaleFactorSlider.addChangeListener(new javax.swing.event.ChangeListener() {

    public void stateChanged(javax.swing.event.ChangeEvent evt) {
    scaleFactorAdjusted(evt);
    }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
    panel2.add(scaleFactorSlider, gridBagConstraints);

    explosionLabel.setBackground(Color.WHITE);
    explosionLabel.setText(labels.getString("Explode"));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
    panel2.add(explosionLabel, gridBagConstraints);

    explosionSlider.setBackground(Color.WHITE);
    explosionSlider.setValue(0);
    explosionSlider.addChangeListener(new javax.swing.event.ChangeListener() {

    public void stateChanged(javax.swing.event.ChangeEvent evt) {
    explosionAdjusted(evt);
    }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
    panel2.add(explosionSlider, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    controlsPanel.add(panel2, gridBagConstraints);

    panel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 6, 0));

    panel3.setBackground(Color.WHITE);
    resetButton.setBackground(Color.WHITE);
    resetButton.setText(labels.getString("reset"));
    panel3.add(resetButton);

    scrambleButton.setBackground(Color.WHITE);
    scrambleButton.setText(labels.getString("scramble"));
    panel3.add(scrambleButton);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
    controlsPanel.add(panel3, gridBagConstraints);

    //getContentPane().add(controlsPanel, java.awt.BorderLayout.SOUTH);

    }*/

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        toolGroup = new javax.swing.ButtonGroup();
        controlsPanel = new javax.swing.JPanel();
        toolsPanel = new javax.swing.JPanel();
        twistTool = new javax.swing.JRadioButton();
        linkTool = new javax.swing.JRadioButton();
        partsTool = new javax.swing.JRadioButton();
        stickersTool = new javax.swing.JRadioButton();
        panel3 = new javax.swing.JPanel();
        resetButton = new javax.swing.JButton();
        scrambleButton = new javax.swing.JButton();
        slidersPanel = new javax.swing.JPanel();
        scaleFactorLabel = new javax.swing.JLabel();
        scaleFactorSlider = new javax.swing.JSlider();
        explosionLabel = new javax.swing.JLabel();
        explosionSlider = new javax.swing.JSlider();

        controlsPanel.setBackground(new java.awt.Color(255, 255, 255));
        controlsPanel.setLayout(new java.awt.GridBagLayout());

        toolsPanel.setBackground(new java.awt.Color(255, 255, 255));
        toolsPanel.setLayout(new javax.swing.BoxLayout(toolsPanel, javax.swing.BoxLayout.LINE_AXIS));

        twistTool.setBackground(new java.awt.Color(255, 255, 255));
        toolGroup.add(twistTool);
        twistTool.setSelected(true);
        twistTool.setText(labels.getString("twist")); // NOI18N
        twistTool.setMargin(new java.awt.Insets(0, 0, 0, 0));
        twistTool.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toolChanged(evt);
            }
        });
        toolsPanel.add(twistTool);

        linkTool.setBackground(new java.awt.Color(255, 255, 255));
        toolGroup.add(linkTool);
        linkTool.setText(labels.getString("link")); // NOI18N
        linkTool.setMargin(new java.awt.Insets(0, 0, 0, 0));
        toolsPanel.add(linkTool);

        partsTool.setBackground(new java.awt.Color(255, 255, 255));
        toolGroup.add(partsTool);
        partsTool.setText(labels.getString("parts")); // NOI18N
        partsTool.setMargin(new java.awt.Insets(0, 0, 0, 0));
        toolsPanel.add(partsTool);

        stickersTool.setBackground(new java.awt.Color(255, 255, 255));
        toolGroup.add(stickersTool);
        stickersTool.setText(labels.getString("stickers")); // NOI18N
        stickersTool.setMargin(new java.awt.Insets(0, 0, 0, 0));
        toolsPanel.add(stickersTool);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        controlsPanel.add(toolsPanel, gridBagConstraints);

        panel3.setBackground(new java.awt.Color(255, 255, 255));
        panel3.setLayout(new javax.swing.BoxLayout(panel3, javax.swing.BoxLayout.LINE_AXIS));

        resetButton.setBackground(new java.awt.Color(255, 255, 255));
        resetButton.setText(labels.getString("reset")); // NOI18N
        panel3.add(resetButton);

        scrambleButton.setBackground(new java.awt.Color(255, 255, 255));
        scrambleButton.setText(labels.getString("scramble")); // NOI18N
        panel3.add(scrambleButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        controlsPanel.add(panel3, gridBagConstraints);

        slidersPanel.setBackground(new java.awt.Color(255, 255, 255));
        slidersPanel.setLayout(new java.awt.GridBagLayout());

        scaleFactorLabel.setBackground(new java.awt.Color(255, 255, 255));
        scaleFactorLabel.setText(labels.getString("Scale")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        slidersPanel.add(scaleFactorLabel, gridBagConstraints);

        scaleFactorSlider.setBackground(new java.awt.Color(255, 255, 255));
        scaleFactorSlider.setMaximum(200);
        scaleFactorSlider.setMinimum(20);
        scaleFactorSlider.setValue(100);
        scaleFactorSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                scaleFactorAdjusted(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        slidersPanel.add(scaleFactorSlider, gridBagConstraints);

        explosionLabel.setBackground(new java.awt.Color(255, 255, 255));
        explosionLabel.setText(labels.getString("Explode")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        slidersPanel.add(explosionLabel, gridBagConstraints);

        explosionSlider.setBackground(new java.awt.Color(255, 255, 255));
        explosionSlider.setValue(0);
        explosionSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                explosionAdjusted(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        slidersPanel.add(explosionSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        controlsPanel.add(slidersPanel, gridBagConstraints);

        getContentPane().add(controlsPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void toolChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_toolChanged
        if (cube3d != null) {
            DefaultCubeAttributes attr = (DefaultCubeAttributes) cube3d.getAttributes();
            if (attr != null) {
                liftingPart = -1;
                cube3d.dispatch(new PartExploder());
            }
        }
    }//GEN-LAST:event_toolChanged

    private void explosionAdjusted(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_explosionAdjusted
        DefaultCubeAttributes attr = (DefaultCubeAttributes) cube3d.getAttributes();
        attr.setExplosionFactor(explosionSlider.getValue() / 100f);
    }//GEN-LAST:event_explosionAdjusted

    private void scaleFactorAdjusted(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_scaleFactorAdjusted
        DefaultCubeAttributes attr = (DefaultCubeAttributes) cube3d.getAttributes();
        attr.setScaleFactor(scaleFactorSlider.getValue() / 100f);
    }//GEN-LAST:event_scaleFactorAdjusted

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel controlsPanel;
    private javax.swing.JLabel explosionLabel;
    private javax.swing.JSlider explosionSlider;
    private javax.swing.JRadioButton linkTool;
    private javax.swing.JPanel panel3;
    private javax.swing.JRadioButton partsTool;
    private javax.swing.JButton resetButton;
    private javax.swing.JLabel scaleFactorLabel;
    private javax.swing.JSlider scaleFactorSlider;
    private javax.swing.JButton scrambleButton;
    private javax.swing.JPanel slidersPanel;
    private javax.swing.JRadioButton stickersTool;
    private javax.swing.ButtonGroup toolGroup;
    private javax.swing.JPanel toolsPanel;
    private javax.swing.JRadioButton twistTool;
    // End of variables declaration//GEN-END:variables
}
