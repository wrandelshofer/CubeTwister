/* @(#)CubeModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.undo.*;
import ch.randelshofer.gui.*;
import ch.randelshofer.gui.tree.TreeNodeImpl;
import ch.randelshofer.rubik.*;
import java.awt.*;
import java.beans.*;
import java.util.*;
import javax.swing.undo.*;
import javax.swing.event.*;

/**
 * Holds a description of a Cube.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>2.1.1 2010-03-23 Method createCube is now able to create cubes
 * with 6 and 7 layers too.
 * <br>2.1 2007-08-11 Moved "name" property into superclass.
 * <br>2.0 2006-01-05 Reworked for CubeTwister 2.0.
 * <br>1.2 2002-12-27 Part names shortened.
 * <br>1.1.2 2002-11-14 Coalescing of undoable edit events of
 * setIntScale, setIntExplode, setIntAlpha and setIntBeta removed.
 * <br>1.1.1 2002-05-12 Renamed the term 'kind' into 'kind'.
 * <br>1.1 2001-09-23 Now uses Rubik's Cube color scheme.
 */
public class CubeModel
        extends InfoModel
        implements CubeAttributes, PropertyChangeListener, ChangeListener {
    private final static long serialVersionUID = 1L;
    public final static int PARTS_INDEX = 0;
    public final static int STICKERS_INDEX = 1;
    public final static int COLORS_INDEX = 2;
    
    public static final String KIND_PROPERTY = "Kind";
    public static final String SCALE_PROPERTY = CubeAttributes.SCALE_FACTOR_PROPERTY;
    public static final String INT_SCALE_PROPERTY = "intScale";
    public static final String EXPLODE_PROPERTY = CubeAttributes.EXPLOSION_FACTOR_PROPERTY;
    public static final String INT_EXPLODE_PROPERTY = "intExplode";
    public static final String INT_ALPHA_PROPERTY = "intAlpha";
    public static final String INT_BETA_PROPERTY = "intBeta";
    
    private CubeKind kind;
    private Class<?> cube3DClass, simpleCube3DClass;
    private int alpha = -25, beta = 45, scale = 100, explode;
    private int stickerCount;
    private int partCount;
    
    protected int[] stickerCountPerFace;
    
    protected Color frontBgColor;
    protected Color rearBgColor;
    protected DefaultImageWellModel stickersImageModel = new DefaultImageWellModel();
    protected DefaultImageWellModel frontBgImageModel = new DefaultImageWellModel();
    protected DefaultImageWellModel rearBgImageModel = new DefaultImageWellModel();
    
    protected boolean rearBgImageVisible;
    protected boolean frontBgImageVisible;
    protected boolean stickersImageVisible;
    
    private boolean valueIsAdjusting;
    private int twistDuration = 400;
    
    /**
     * If this value is greater than 0, then property change
     * events of the child parts, color and sticker model are suppressed.
     */
    private int suppressPropertyChangeEvents = 0;
    //private Throwable constructed;
    /** Creates new CubeScript */
    public CubeModel() {
        //  this(null);
        this(CubeKind.RUBIK);
    }
    public CubeModel(CubeKind kind) {
        //constructed = new Throwable();
        setAllowsChildren(true);
        
        add(new EntityModel("Parts", true));
        add(new EntityModel("Stickers", true));
        add(new CubeColorsModel("Colors", true));
        
        if (kind != null) {
            basicSetKind(kind);
        }
        
        stickersImageModel.addChangeListener(this);
        frontBgImageModel.addChangeListener(this);
        rearBgImageModel.addChangeListener(this);
    }

    public Cube createCube() {
        switch (getLayerCount()) {
            case 2 :
                return new PocketCube();
            case 3 :
                return new RubiksCube();
            case 4 :
                return new RevengeCube();
            case 5 :
                return new ProfessorCube();
            case 6 :
                return new Cube6();
            case 7 :
                return new Cube7();
        }
        return null;
    }
    
    public int getLayerCount() {
        return kind.getLayerCount();
    }
    
    public int getIntScale() {
        return scale;
    }
    
    public void setIntScale(int value) {
        if (value != scale) {
            int oldValue = scale;
            basicSetIntScale(value);
            
            firePropertyChange(INT_SCALE_PROPERTY, oldValue, value);
            firePropertyChange(CubeAttributes.SCALE_FACTOR_PROPERTY, new Float(oldValue / 100f), new Float(value / 100f));
            
            UndoableEdit edit = new UndoableIntEdit(this, "Scale", oldValue, value, getValueIsAdjusting()) {
    private final static long serialVersionUID = 1L;
                @Override
                public void revert(int a, int b) {
                    scale = b;
                    firePropertyChange(INT_SCALE_PROPERTY, a, b);
                    firePropertyChange(CubeAttributes.SCALE_FACTOR_PROPERTY, new Float(a / 100f), new Float(b / 100f));
                }
            };
            fireUndoableEditHappened(edit);
        }
    }
    public void basicSetIntScale(int value) {
        scale = value;
    }
    
    
    public int getIntExplode() {
        return explode;
    }
    
    public void setIntExplode(int value) {
        if (value != explode) {
            int oldValue = explode;
            basicSetIntExplode(value);
            
            firePropertyChange(INT_EXPLODE_PROPERTY, oldValue, value);
            firePropertyChange(CubeAttributes.EXPLOSION_FACTOR_PROPERTY, new Float(oldValue / 100f), new Float(value / 100f));
            
            UndoableEdit edit = new UndoableIntEdit(this, "Explode", oldValue, value, getValueIsAdjusting()) {
    private final static long serialVersionUID = 1L;
                @Override
                public void revert(int a, int b) {
                    explode = b;
                    firePropertyChange(INT_EXPLODE_PROPERTY, a, b);
                    firePropertyChange(CubeAttributes.EXPLOSION_FACTOR_PROPERTY, new Float(a / 100f), new Float(b / 100f));
                }
            };
            fireUndoableEditHappened(edit);
        }
    }
    public void basicSetIntExplode(int value) {
        explode = value;
    }
    public int getIntAlpha() {
        return alpha;
    }
    
    public void setIntAlpha(int value) {
        if (value != alpha) {
            int oldValue = alpha;
            basicSetIntAlpha(value);
            
            firePropertyChange(INT_ALPHA_PROPERTY, oldValue, value);
            firePropertyChange(CubeAttributes.ALPHA_PROPERTY, new Double(oldValue / 180d * Math.PI), new Double(value / 180d * Math.PI));
            
            UndoableEdit edit = new UndoableIntEdit(this, "Alpha", oldValue, value, getValueIsAdjusting()) {
    private final static long serialVersionUID = 1L;
                @Override
                public void revert(int a, int b) {
                    alpha = b;
                    firePropertyChange(INT_ALPHA_PROPERTY, a, b);
                    firePropertyChange(CubeAttributes.ALPHA_PROPERTY, new Double(a / 180d * Math.PI), new Double(b / 180d * Math.PI));
                }
            };
            fireUndoableEditHappened(edit);
        }
    }
    public void basicSetIntAlpha(int value) {
        alpha = value;
    }
    public int getIntBeta() {
        return beta;
    }
    
    public void setIntBeta(int value) {
        if (value != beta) {
            int oldValue = beta;
            basicSetIntBeta(value);
            
            firePropertyChange(INT_BETA_PROPERTY, oldValue, value);
            firePropertyChange(CubeAttributes.BETA_PROPERTY, new Double(oldValue / 180d * Math.PI), new Double(value / 180d * Math.PI));
            
            UndoableEdit edit = new UndoableIntEdit(this, "Beta", oldValue, value, getValueIsAdjusting()) {
    private final static long serialVersionUID = 1L;
                @Override
                public void revert(int a, int b) {
                    beta = b;
                    firePropertyChange(INT_BETA_PROPERTY, a, b);
                    firePropertyChange(CubeAttributes.BETA_PROPERTY, new Double(a / 180d * Math.PI), new Double(b / 180d * Math.PI));
                }
            };
            fireUndoableEditHappened(edit);
        }
    }
    public void basicSetIntBeta(int value) {
        beta = value;
    }
    
    public CubeKind getKind() {
        return kind;
    }
    
    public void setKind(final CubeKind value) {
        boolean hasParent = getParent() != null;
        CompositeEdit ce = null;
        if (hasParent) {
            ce = new CompositeEdit();
            fireUndoableEditHappened(ce);
        }
        
        final CubeKind oldValue = kind;
        final Class<?> oldCube3DClass = cube3DClass;
        final Class<?> oldSimpleCube3DClass = simpleCube3DClass;
        
        if (hasParent) fireUndoableEditHappened(
                new AbstractUndoableEdit() {
    private final static long serialVersionUID = 1L;
            @Override
            public void undo() {
                super.undo();
                kind = oldValue;
                cube3DClass = oldCube3DClass;
                simpleCube3DClass = oldSimpleCube3DClass;
                firePropertyChange(KIND_PROPERTY, value, oldValue);
            }
            @Override
            public String getPresentationName() {
                return "Kind";
            }
        }
        );
        
        basicSetKind(value);
        
        final Class<?> newCube3DClass = cube3DClass;
        final Class<?> newSimpleCube3DClass = simpleCube3DClass;
        
        if (hasParent) fireUndoableEditHappened(
                new AbstractUndoableEdit() {
    private final static long serialVersionUID = 1L;
            @Override
            public void redo() {
                super.redo();
                kind = value;
                cube3DClass = newCube3DClass;
                simpleCube3DClass = newSimpleCube3DClass;
                firePropertyChange(KIND_PROPERTY, oldValue, value);
            }
            @Override
            public String getPresentationName() {
                return "Kind";
            }
        }
        );
        if (hasParent) fireUndoableEditHappened(ce);
    }
    
    public void basicSetKind(CubeKind value) {
        suppressPropertyChangeEvents++;
        CubeKind oldValue = kind;
        kind = value;
        
        
        if (kind == CubeKind.POCKET) {
            constructPocket();
        } else if (kind == CubeKind.RUBIK) {
            constructRubik();
        } else if (kind == CubeKind.REVENGE) {
            constructRevenge();
        } else if (kind == CubeKind.PROFESSOR) {
            constructProfessor();
        } else if (kind == CubeKind.BARREL) {
            constructBarrel();
        } else if (kind == CubeKind.DIAMOND) {
            constructDiamond();
        } else if (kind == CubeKind.CUBOCTAHEDRON) {
            constructCuboctahedron();
        } else if (kind == CubeKind.VCUBE_6) {
            constructVCube6();
        } else if (kind == CubeKind.VCUBE_7) {
            constructVCube7();
        } else if (kind == CubeKind.CUBE_6) {
            constructCube6();
        } else if (kind == CubeKind.CUBE_7) {
            constructCube7();
        }
        
        suppressPropertyChangeEvents--;
        firePropertyChange(KIND_PROPERTY, oldValue, value);
    }
    
    public Class<?> getCube3DClass() {
        //return simpleCube3DClass;
        return cube3DClass;
    }
    
    public EntityModel getParts() {
        return getChildAt(PARTS_INDEX);
    }
    public EntityModel getColors() {
        return getChildAt(COLORS_INDEX);
    }
    public EntityModel getStickers() {
        return getChildAt(STICKERS_INDEX);
    }
    
    public Class<?> getSimpleCube3DClass() {
        Iterator<EntityModel> enumer = getParts().getChildren().iterator();
        while (enumer.hasNext()) {
            CubePartModel m = (CubePartModel) enumer.next();
            if (! m.isVisible()) break;
        }
        
        if (! enumer.hasNext() && explode == 0) {
            return simpleCube3DClass;
        } else {
            return cube3DClass;
        }
    }
    
    public CubeAttributes getCubeAttributes() {
        return this;
    }
    
    public void addNotify(EntityModel m) {
        if (m.getParent() == getColors()) {
            m.addPropertyChangeListener(this);
        }
    }
    public void removeNotify(EntityModel m) {
        if (m.getParent() == getColors()) {
            m.removePropertyChangeListener(this);
        }
    }

    public void setPartVisible(int partIndex, boolean newValue) {
               ((CubePartModel) getParts().getChildAt(partIndex)).setVisible(newValue);
    }
    
    private void constructPocket() {
        cube3DClass = PocketCubeIdx3D.class;
        simpleCube3DClass = PocketCubeGeom3D.class;
        
        EntityModel colors = getColors();
        EntityModel parts = getParts();
        EntityModel stickers = getStickers();
        
        int fsc = 4;
        stickerCount = 6 * 4;
        stickerCountPerFace = new int[] {fsc,fsc,fsc,fsc,fsc,fsc};
        partCount = 9;
        
        CubeColorModel sc[] = new CubeColorModel[9];
        int i;
        
        String[] partNames = {
            "Corner urf",  "Corner dfr",
            "Corner ubr", "Corner drb",
            "Corner ulb", "Corner dbl",
            "Corner ufl", "Corner dlf",
            "Center"
        };
        String[] stickerNames = {
            "Right", "Up", "Front", "Left", "Down", "Back"
        };
        colors.removeAllChildren();
        colors.add(sc[0] = new CubeColorModel("Stickers Right", new Color(255,210,0))); //Right: yellow
        colors.add(sc[1] = new CubeColorModel("Stickers Up",    new Color(0,51,115))); //Up:    blue
        colors.add(sc[2] = new CubeColorModel("Stickers Front", new Color(140,0,15))); //Front: red
        colors.add(sc[3] = new CubeColorModel("Stickers Left",  new Color(248,248,248))); //Left:  white
        colors.add(sc[4] = new CubeColorModel("Stickers Down",  new Color(0,115,47))); //Down:  green
        colors.add(sc[5] = new CubeColorModel("Stickers Back",  new Color(255,70,0))); //Back:  orange
        colors.add(sc[6] = new CubeColorModel("Outer Parts",    new Color(16,16,16))); //dark gray
        colors.add(sc[7] = new CubeColorModel("Center Part",    new Color(248,248,248))); //white
        colors.add(sc[8] = new CubeColorModel("Outlines",       new Color(  0,  0,  0))); //black
        
        // Try to preserve existing part nodes
        if (parts.getChildCount() != getPartCount()) {
            //            parts.removeAllChildren();
            parts.removeAllChildren();
            for (i=getPartCount() - 1; i > -1; i--) {
                CubePartModel m = new CubePartModel();
                //                parts.add(m);
                parts.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the part nodes
        for (i=8; i > -1; i--) {
            CubePartModel m = (CubePartModel) parts.getChildAt(i);
            m.setFillColorModel((i == getPartCount() - 1) ? sc[7] : sc[6]);
            m.setOutlineColorModel(sc[8]);
            m.setName(partNames[i]);
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        
        // Try to preserve existing sticker nodes
        if (stickers.getChildCount() != getStickerCount()) {
            //            stickers.removeAllChildren();
            stickers.removeAllChildren();
            for (i=getStickerCount() - 1; i > -1; i--) {
                CubeStickerModel m = new CubeStickerModel();
                //                stickers.add(m);
                stickers.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the sticker nodes
        for (i=getStickerCount() - 1; i > -1; i--) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i);
            m.setFillColorModel(sc[i / 4]);
            m.setName(stickerNames[i / 4]+' '+(i % 4 + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
    }
    private void constructRevenge() {
        cube3DClass = RevengeCubeIdx3D.class;
        simpleCube3DClass = RevengeCubeGeom3D.class;
        
        EntityModel colors = getColors();
        EntityModel parts = getParts();
        EntityModel stickers = getStickers();
        
        int fsc = 4*4;
        stickerCount = 6 * fsc;
        stickerCountPerFace = new int[] {fsc,fsc,fsc,fsc,fsc,fsc};
        partCount = 8+2*12+4*6+1;
        
        CubeColorModel sc[] = new CubeColorModel[9];
        int i;
        
        String[] partNames = {
            "Corner urf",  "Corner dfr",
            "Corner ubr", "Corner drb",
            "Corner ulb", "Corner dbl",
            "Corner ufl", "Corner dlf",
            
            "Edge ur1",
            "Edge rf1",
            "Edge dr1",
            "Edge bu1",
            "Edge rb1",
            "Edge bd1",
            "Edge ul1",
            "Edge lb1",
            "Edge dl1",
            "Edge fu1",
            "Edge lf1",
            "Edge fd1",
            
            "Edge ur2",
            "Edge rf2",
            "Edge dr2",
            "Edge bu2",
            "Edge rb2",
            "Edge bd2",
            "Edge ul2",
            "Edge lb2",
            "Edge dl2",
            "Edge fu2",
            "Edge lf2",
            "Edge fd2",
            
            "Side r1",
            "Side u1",
            "Side f1",
            "Side l1",
            "Side d1",
            "Side b1",

            "Side r2",
            "Side u2",
            "Side f2",
            "Side l2",
            "Side d2",
            "Side b2",

            "Side r3",
            "Side u3",
            "Side f3",
            "Side l3",
            "Side d3",
            "Side b3",

            "Side r4",
            "Side u4",
            "Side f4",
            "Side l4",
            "Side d4",
            "Side b4",
            
            "Center"
        };
        String[] stickerNames = {
            "Right", "Up", "Front", "Left", "Down", "Back"
        };
        colors.removeAllChildren();
        colors.add(sc[0] = new CubeColorModel("Stickers Right", new Color(255,210,0))); //Right: yellow
        colors.add(sc[1] = new CubeColorModel("Stickers Up",    new Color(0,51,115))); //Up:    blue
        colors.add(sc[2] = new CubeColorModel("Stickers Front", new Color(140,0,15))); //Front: red
        colors.add(sc[3] = new CubeColorModel("Stickers Left",  new Color(248,248,248))); //Left:  white
        colors.add(sc[4] = new CubeColorModel("Stickers Down",  new Color(0,115,47))); //Down:  green
        colors.add(sc[5] = new CubeColorModel("Stickers Back",  new Color(255,70,0))); //Back:  orange
        colors.add(sc[6] = new CubeColorModel("Outer Parts",    new Color(16,16,16))); //dark gray
        colors.add(sc[7] = new CubeColorModel("Center Part",    new Color(248,248,248))); //white
        colors.add(sc[8] = new CubeColorModel("Outlines",       new Color(  0,  0,  0))); //black
        
        // Try to preserve existing part nodes
        if (parts.getChildCount() != getPartCount()) {
            //            parts.removeAllChildren();
            parts.removeAllChildren();
            for (i=getPartCount() - 1; i > -1; i--) {
                CubePartModel m = new CubePartModel();
                //                parts.add(m);
                parts.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the part nodes
        for (i=getPartCount() - 1; i > -1; i--) {
            CubePartModel m = (CubePartModel) parts.getChildAt(i);
            m.setFillColorModel((i == getPartCount() - 1) ? sc[7] : sc[6]);
            m.setOutlineColorModel(sc[8]);
            m.setName(partNames[i]);
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        
        // Try to preserve existing sticker nodes
        if (stickers.getChildCount() != getStickerCount()) {
            //            stickers.removeAllChildren();
            stickers.removeAllChildren();
            for (i=getStickerCount() - 1; i > -1; i--) {
                CubeStickerModel m = new CubeStickerModel();
                //                stickers.add(m);
                stickers.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the sticker nodes
        for (i=getStickerCount() - 1; i > -1; i--) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i);
            m.setFillColorModel(sc[i / fsc]);
            m.setName(stickerNames[i / fsc]+' '+(i % fsc + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
    }
    private void constructProfessor() {
        cube3DClass = ProfessorCubeIdx3D.class;
        simpleCube3DClass = ProfessorCubeGeom3D.class;
        
        EntityModel colors = getColors();
        EntityModel parts = getParts();
        EntityModel stickers = getStickers();
        
        int fsc = 5*5;
        stickerCount = 6 * fsc;
        stickerCountPerFace = new int[] {fsc,fsc,fsc,fsc,fsc,fsc};
        partCount = 8+3*12+9*6+1;
        
        CubeColorModel sc[] = new CubeColorModel[9];
        int i;
        
        String[] partNames = {
            "Corner urf",  "Corner dfr",
            "Corner ubr", "Corner drb",
            "Corner ulb", "Corner dbl",
            "Corner ufl", "Corner dlf",
            
            "Edge ur1",
            "Edge rf1",
            "Edge dr1",
            "Edge bu1",
            "Edge rb1",
            "Edge bd1",
            "Edge ul1",
            "Edge lb1",
            "Edge dl1",
            "Edge fu1",
            "Edge lf1",
            "Edge fd1",
            
            "Edge ur2",
            "Edge rf2",
            "Edge dr2",
            "Edge bu2",
            "Edge rb2",
            "Edge bd2",
            "Edge ul2",
            "Edge lb2",
            "Edge dl2",
            "Edge fu2",
            "Edge lf2",
            "Edge fd2",
            
            "Edge ur3",
            "Edge rf3",
            "Edge dr3",
            "Edge bu3",
            "Edge rb3",
            "Edge bd3",
            "Edge ul3",
            "Edge lb3",
            "Edge dl3",
            "Edge fu3",
            "Edge lf3",
            "Edge fd3",
            
            "Side r1",
            "Side u1",
            "Side f1",
            "Side l1",
            "Side d1",
            "Side b1",

            "Side r2",
            "Side u2",
            "Side f2",
            "Side l2",
            "Side d2",
            "Side b2",

            "Side r3",
            "Side u3",
            "Side f3",
            "Side l3",
            "Side d3",
            "Side b3",

            "Side r4",
            "Side u4",
            "Side f4",
            "Side l4",
            "Side d4",
            "Side b4",

            "Side r5",
            "Side u5",
            "Side f5",
            "Side l5",
            "Side d5",
            "Side b5",

            "Side r6",
            "Side u6",
            "Side f6",
            "Side l6",
            "Side d6",
            "Side b6",

            "Side r7",
            "Side u7",
            "Side f7",
            "Side l7",
            "Side d7",
            "Side b7",

            "Side r8",
            "Side u8",
            "Side f8",
            "Side l8",
            "Side d8",
            "Side b8",

            "Side r9",
            "Side u9",
            "Side f9",
            "Side l9",
            "Side d9",
            "Side b9",
            
            "Center"
        };
        String[] stickerNames = {
            "Right", "Up", "Front", "Left", "Down", "Back"
        };
        colors.removeAllChildren();
        colors.add(sc[0] = new CubeColorModel("Stickers Right", new Color(255,210,0))); //Right: yellow
        colors.add(sc[1] = new CubeColorModel("Stickers Up",    new Color(0,51,115))); //Up:    blue
        colors.add(sc[2] = new CubeColorModel("Stickers Front", new Color(140,0,15))); //Front: red
        colors.add(sc[3] = new CubeColorModel("Stickers Left",  new Color(248,248,248))); //Left:  white
        colors.add(sc[4] = new CubeColorModel("Stickers Down",  new Color(0,115,47))); //Down:  green
        colors.add(sc[5] = new CubeColorModel("Stickers Back",  new Color(255,70,0))); //Back:  orange
        colors.add(sc[6] = new CubeColorModel("Outer Parts",    new Color(16,16,16))); //dark gray
        colors.add(sc[7] = new CubeColorModel("Center Part",    new Color(248,248,248))); //white
        colors.add(sc[8] = new CubeColorModel("Outlines",       new Color(  0,  0,  0))); //black
        
        // Try to preserve existing part nodes
        if (parts.getChildCount() != getPartCount()) {
            //            parts.removeAllChildren();
            parts.removeAllChildren();
            for (i=getPartCount() - 1; i > -1; i--) {
                CubePartModel m = new CubePartModel();
                //                parts.add(m);
                parts.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the part nodes
        for (i=getPartCount() - 1; i > -1; i--) {
            CubePartModel m = (CubePartModel) parts.getChildAt(i);
            m.setFillColorModel((i == getPartCount() - 1) ? sc[7] : sc[6]);
            m.setOutlineColorModel(sc[8]);
            m.setName(partNames[i]);
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        
        // Try to preserve existing sticker nodes
        if (stickers.getChildCount() != getStickerCount()) {
            //            stickers.removeAllChildren();
            stickers.removeAllChildren();
            for (i=getStickerCount() - 1; i > -1; i--) {
                CubeStickerModel m = new CubeStickerModel();
                //                stickers.add(m);
                stickers.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the sticker nodes
        for (i=getStickerCount() - 1; i > -1; i--) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i);
            m.setFillColorModel(sc[i / fsc]);
            m.setName(stickerNames[i / fsc]+' '+(i % fsc + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
    }
    private void constructRubik() {
        cube3DClass = RubiksCubeIdx3D.class;
        simpleCube3DClass = RubiksCubeGeom3D.class;
        
        EntityModel colors = getColors();
        EntityModel parts = getParts();
        EntityModel stickers = getStickers();
        
        stickerCount = 6 * 9;
        partCount = 8+12+6+1;
        int fsc = 9;
        stickerCountPerFace = new int[] {fsc,fsc,fsc,fsc,fsc,fsc};
        
        CubeColorModel sc[] = new CubeColorModel[9];
        int i;
        
        String[] partNames = {
            "Corner urf",  "Corner dfr",
            "Corner ubr", "Corner drb",
            "Corner ulb", "Corner dbl",
            "Corner ufl", "Corner dlf",
            "Edge ur", "Edge rf", "Edge dr",
            "Edge bu", "Edge rb", "Edge bd",
            "Edge ul", "Edge lb", "Edge dl",
            "Edge fu",  "Edge lf",  "Edge fd",
            "Side r", "Side u", "Side f",
            "Side l", "Side d", "Side b",
            "Center"
        };
        String[] stickerNames = {
            "Right", "Up", "Front", "Left", "Down", "Back"
        };
        colors.removeAllChildren();
        colors.add(sc[0] = new CubeColorModel("Stickers Right", new Color(255,210,0))); //Right: yellow
        colors.add(sc[1] = new CubeColorModel("Stickers Up",    new Color(0,51,115))); //Up:    blue
        colors.add(sc[2] = new CubeColorModel("Stickers Front", new Color(140,0,15))); //Front: red
        colors.add(sc[3] = new CubeColorModel("Stickers Left",  new Color(248,248,248))); //Left:  white
        colors.add(sc[4] = new CubeColorModel("Stickers Down",  new Color(0,115,47))); //Down:  green
        colors.add(sc[5] = new CubeColorModel("Stickers Back",  new Color(255,70,0))); //Back:  orange
        colors.add(sc[6] = new CubeColorModel("Outer Parts",    new Color(16,16,16))); //dark gray
        colors.add(sc[7] = new CubeColorModel("Center Part",    new Color(248,248,248))); //white
        colors.add(sc[8] = new CubeColorModel("Outlines",       new Color(  0,  0,  0))); //black
        
        // Try to preserve existing part nodes
        if (parts.getChildCount() != getPartCount()) {
            //            parts.removeAllChildren();
            parts.removeAllChildren();
            for (i=getPartCount() - 1; i > -1; i--) {
                CubePartModel m = new CubePartModel();
                //                parts.add(m);
                parts.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the part nodes
        for (i=getPartCount() - 1; i > -1; i--) {
            CubePartModel m = (CubePartModel) parts.getChildAt(i);
            m.setFillColorModel((i == getPartCount() - 1) ? sc[7] : sc[6]);
            m.setOutlineColorModel(sc[8]);
            m.setName(partNames[i]);
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        
        // Try to preserve existing sticker nodes
        if (stickers.getChildCount() != getStickerCount()) {
            //            stickers.removeAllChildren();
            stickers.removeAllChildren();
            for (i=getStickerCount() - 1; i > -1; i--) {
                CubeStickerModel m = new CubeStickerModel();
                //                stickers.add(m);
                stickers.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the sticker nodes
        for (i=getStickerCount() - 1; i > -1; i--) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i);
            m.setFillColorModel(sc[i / 9]);
            m.setName(stickerNames[i / 9]+' '+(i % 9 + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
    }
    
    private void constructBarrel() {
        cube3DClass = RubiksBarrelIdx3D.class;
        simpleCube3DClass = RubiksBarrelGeom3D.class;
        
        EntityModel colors = getColors();
        EntityModel parts = getParts();
        EntityModel stickers = getStickers();
        
        stickerCount = 8 * 3 + 2 * 9;
        stickerCountPerFace = new int[] {3,3,3,3,3,3,3,3,9,9};
        partCount = 8+12+6+1;
        
        CubeColorModel sc[] = new CubeColorModel[13];
        int i;
        
        String[] partNames = {
            "Corner urf",  "Corner dfr",
            "Corner ubr", "Corner drb",
            "Corner ulb", "Corner dbl",
            "Corner ufl", "Corner dlf",
            "Edge ur", "Oblique fr", "Edge dr",
            "Edge bu", "Oblique br", "Edge bd",
            "Edge ul", "Oblique bl", "Edge dl",
            "Edge fu",  "Oblique fl",  "Edge fd",
            "Side r", "Side u", "Side f",
            "Side l", "Side d", "Side b",
            "Center"
        };
        String[] stickerNames = {
            "Front ", "Front-Right ", "Right ", "Back-Right ",
            "Back ", "Back-Left ", "Left ", "Front-Left ",
            "Bottom ", "Top "
        };
        
        colors.removeAllChildren();
        colors.add(sc[0] = new CubeColorModel("Stickers Front", new Color(140,0,15))); //red
        colors.add(sc[1] = new CubeColorModel("Stickers Front-Right", new Color(155,0,97))); //dark purple
        colors.add(sc[2] = new CubeColorModel("Stickers Right", new Color(70,188,218))); //cyan
        colors.add(sc[3] = new CubeColorModel("Stickers Back-Right",  new Color(225,60,124))); //bright purple
        colors.add(sc[4] = new CubeColorModel("Stickers Back",  new Color(0,115,47))); //green
        colors.add(sc[5] = new CubeColorModel("Stickers Back-Left",  new Color(255,70,0))); //orange
        colors.add(sc[6] = new CubeColorModel("Stickers Left",  new Color(124,83,0))); //gold
        colors.add(sc[7] = new CubeColorModel("Stickers Front-Left", new Color(255,210,0))); //yellow
        colors.add(sc[8] = new CubeColorModel("Stickers Down",  new Color(248,248,248))); //white
        colors.add(sc[9] = new CubeColorModel("Stickers Up",    new Color(0,41,103))); //blue
        colors.add(sc[10] = new CubeColorModel("Outer Parts",    new Color(16,16,16))); //dark gray
        colors.add(sc[11] = new CubeColorModel("Center Part",    new Color(248,248,248))); //white
        colors.add(sc[12] = new CubeColorModel("Outlines",       new Color(  0,  0,  0))); //black
        
        // Try to preserve existing part nodes
        if (parts.getChildCount() != getPartCount()) {
            //            parts.removeAllChildren();
            parts.removeAllChildren();
            for (i=getPartCount() - 1; i > -1; i--) {
                CubePartModel m = new CubePartModel();
                //                parts.add(m);
                parts.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the part nodes
        for (i=getPartCount() - 1; i > -1; i--) {
            CubePartModel m = (CubePartModel) parts.getChildAt(i);
            m.setFillColorModel((i == getPartCount() - 1) ? sc[11] : sc[10]);
            m.setOutlineColorModel(sc[12]);
            m.setName(partNames[i]);
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        
        // Try to preserve existing sticker nodes
        if (stickers.getChildCount() != getStickerCount()) {
            //            stickers.removeAllChildren();
            stickers.removeAllChildren();
            for (i=getStickerCount() - 1; i > -1; i--) {
                CubeStickerModel m = new CubeStickerModel();
                //                stickers.add(m);
                stickers.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the sticker nodes
        for (i = 0; i < 8 * 3; i++) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i);
            m.setFillColorModel(sc[i / 3]);
            m.setName(stickerNames[i / 3]+' '+(i % 3 + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        for (i = 0; i < 2 * 9; i++) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i + 8 * 3);
            m.setFillColorModel(sc[i / 9 + 8]);
            m.setName(stickerNames[i / 9 + 8]+' '+(i % 9 + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
    }
    private void constructDiamond() {
        cube3DClass = RubiksDiamondIdx3D.class;
        simpleCube3DClass = RubiksDiamondIdx3D.class;
        
        stickerCount = 8 * 3 + 2 * 1;
        stickerCountPerFace = new int[] {3,3,3,3,3,3,3,3,1,1};
        partCount = 8+12+6+1;
        
        EntityModel colors = getColors();
        EntityModel parts = getParts();
        EntityModel stickers = getStickers();
        
        
        CubeColorModel sc[] = new CubeColorModel[13];
        int i;
        
        String[] partNames = {
            "Triangle urf",  "Triangle dfr",
            "Triangle ubr", "Triangle drb",
            "Triangle ulb", "Triangle dbl",
            "Triangle ufl", "Triangle dlf",
            "Oblique ur", "Oblique fr", "Oblique dr",
            "Oblique ub", "Oblique br", "Oblique db",
            "Oblique ul", "Oblique bl", "Oblique dl",
            "Oblique uf",  "Oblique fl",  "Oblique df",
            "Side r", "Side u", "Side f",
            "Side l", "Side d", "Side b",
            "Center"
        };
        String[] stickerNames = {
            "Front ", "Front-Right ", "Right ", "Back-Right ",
            "Back ", "Back-Left ", "Left ", "Front-Left ",
            "Bottom ", "Top "
        };
        
        colors.removeAllChildren();
        colors.add(sc[0] = new CubeColorModel("Stickers Front", new Color(255,210,0))); //yellow
        colors.add(sc[1] = new CubeColorModel("Stickers Front-Right", new Color(0,115,47))); //green
        colors.add(sc[2] = new CubeColorModel("Stickers Right",  new Color(0,41,103))); //blue
        colors.add(sc[3] = new CubeColorModel("Stickers Back-Right", new Color(207,35,112))); //dark purple
        colors.add(sc[4] = new CubeColorModel("Stickers Back",  new Color(255,70,0))); //orange
        colors.add(sc[5] = new CubeColorModel("Stickers Back-Left",  new Color(148,121,169))); //pink
        colors.add(sc[6] = new CubeColorModel("Stickers Left",   new Color(70,188,218))); //cyan
        colors.add(sc[7] = new CubeColorModel("Stickers Front-Left", new Color(124,83,0))); //gold
        colors.add(sc[8] = new CubeColorModel("Stickers Down",  new Color(248,248,248))); //white
        colors.add(sc[9] = new CubeColorModel("Stickers Up",  new Color(140,0,15))); //red
        colors.add(sc[10] = new CubeColorModel("Outer Parts",    new Color(16,16,16))); //dark gray
        colors.add(sc[11] = new CubeColorModel("Center Part",    new Color(248,248,248))); //white
        colors.add(sc[12] = new CubeColorModel("Outlines",       new Color(  0,  0,  0))); //black
        
        // Try to preserve existing part nodes
        if (parts.getChildCount() != getPartCount()) {
            parts.removeAllChildren();
            for (i=getPartCount() - 1; i > -1; i--) {
                CubePartModel m = new CubePartModel();
                parts.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the part nodes
        for (i=getPartCount() - 1; i > -1; i--) {
            CubePartModel m = (CubePartModel) parts.getChildAt(i);
            m.setFillColorModel((i == getPartCount() - 1) ? sc[11] : sc[10]);
            m.setOutlineColorModel(sc[12]);
            m.setName(partNames[i]);
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        
        // Try to preserve existing sticker nodes
        if (stickers.getChildCount() != getStickerCount()) {
            stickers.removeAllChildren();
            for (i=getStickerCount() - 1; i > -1; i--) {
                CubeStickerModel m = new CubeStickerModel();
                stickers.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the sticker nodes
        for (i = 0; i < 8 * 3; i++) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i);
            m.setFillColorModel(sc[i / 3]);
            m.setName(stickerNames[i / 3]+' '+(i % 3 + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        for (i = 0; i < 2; i++) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i + 8 * 3);
            m.setFillColorModel(sc[i + 8]);
            m.setName(stickerNames[i + 8]);
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
    }
    private void constructCuboctahedron() {
        cube3DClass = RubiksCuboctahedronIdx3D.class;
        simpleCube3DClass = RubiksCuboctahedronIdx3D.class;
        
        EntityModel colors = getColors();
        EntityModel parts = getParts();
        EntityModel stickers = getStickers();
        
        stickerCount = 6 * 9 + 8 * 4;
        stickerCountPerFace = new int[] {9,9,9,9,9,9,4,4,4,4,4,4,4,4};
        partCount = 8+12+6+1;
        
        CubeColorModel sc[] = new CubeColorModel[17];
        int i;
        
        String[] partNames = {
            "Corner ufl",  "Corner dfl",
            "Corner ufr", "Corner dfr",
            "Corner ubr", "Corner dbr",
            "Corner ubl", "Corner dbl",
            "Edge fu",  "Edge lf",  "Edge fd",
            "Edge ur", "Edge rf", "Edge dr",
            "Edge bu", "Edge rb", "Edge bd",
            "Edge ul", "Edge lb", "Edge dl",
            "Side f", "Side r", "Side d",
            "Side b", "Side l", "Side u",
            "Center"
        };
        String[] stickerNames = {
            "Front ", "Right ", "Down ", "Back ", "Left ", "Up ",
            "Up-Front-Right ", "Up-Back-Right ", "Up-Back-Left ", "Up-Front-Left ",
            "Down-Front-Right ", "Down-Back-Right ", "Down-Back-Left ", "Down-Front-Left ",
        };
        
        colors.removeAllChildren();
        colors.add(sc[0] = new CubeColorModel("Stickers Front", new Color(140,0,15))); //Front: red
        colors.add(sc[1] = new CubeColorModel("Stickers Right", new Color(255,210,0))); //Right: yellow
        colors.add(sc[2] = new CubeColorModel("Stickers Down",  new Color(0,115,47))); //Down:  green
        colors.add(sc[3] = new CubeColorModel("Stickers Back",  new Color(255,70,0))); //Back:  orange
        colors.add(sc[4] = new CubeColorModel("Stickers Left",  new Color(248,248,248))); //Left:  white
        colors.add(sc[5] = new CubeColorModel("Stickers Up",    new Color(0,51,115))); //Up:    blue
        colors.add(sc[6] = new CubeColorModel("Stickers Up-Front-Right", new Color(16,16,16))); //
        colors.add(sc[7] = new CubeColorModel("Stickers Up-Back-Right",    new Color(16,16,16))); //
        colors.add(sc[8] = new CubeColorModel("Stickers Up-Back-Left",    new Color(16,16,16))); //
        colors.add(sc[9] = new CubeColorModel("Stickers Up-Front-Left",    new Color(16,16,16))); //
        colors.add(sc[10] = new CubeColorModel("Stickers Down-Front-Right",    new Color(16,16,16))); //
        colors.add(sc[11] = new CubeColorModel("Stickers Down-Back-Right",    new Color(16,16,16))); //
        colors.add(sc[12] = new CubeColorModel("Stickers Down-Back-Left",    new Color(16,16,16))); //
        colors.add(sc[13] = new CubeColorModel("Stickers Down-Front-Left",    new Color(16,16,16))); //
        colors.add(sc[14] = new CubeColorModel("Outer Parts",    new Color(16,16,16))); //dark gray
        colors.add(sc[15] = new CubeColorModel("Center Part",    new Color(248,248,248))); //white
        colors.add(sc[16] = new CubeColorModel("Outlines",       new Color(  0,  0,  0))); //black
        
        // Try to preserve existing part nodes
        if (parts.getChildCount() != getPartCount()) {
            parts.removeAllChildren();
            for (i=getPartCount() - 1; i > -1; i--) {
                CubePartModel m = new CubePartModel();
                parts.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the part nodes
        for (i=getPartCount() - 1; i > -1; i--) {
            CubePartModel m = (CubePartModel) parts.getChildAt(i);
            m.setFillColorModel((i == getPartCount() - 1) ? sc[15] : sc[14]);
            m.setOutlineColorModel(sc[16]);
            m.setName(partNames[i]);
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        
        // Try to preserve existing sticker nodes
        if (stickers.getChildCount() != getStickerCount()) {
            stickers.removeAllChildren();
            for (i=getStickerCount() - 1; i > -1; i--) {
                CubeStickerModel m = new CubeStickerModel();
                stickers.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the sticker nodes
        for (i=0; i < 6*9; i++) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i);
            m.setFillColorModel(sc[i / 9]);
            m.setName(stickerNames[i / 9]+' '+(i % 9 + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        for (i = 0; i < 8 * 4; i++) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i + 6 * 9);
            m.setFillColorModel(sc[i / 4 + 6]);
            m.setName(stickerNames[i / 4 + 6]+' '+(i % 4 + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
    }
    private void constructCube6() {
        cube3DClass = Cube6Idx3D.class;
        simpleCube3DClass = Cube6Geom3D.class;
        
        EntityModel colors = getColors();
        EntityModel parts = getParts();
        EntityModel stickers = getStickers();
        
        int fsc = 6*6;
        stickerCount = 6 * fsc;
        stickerCountPerFace = new int[] {fsc,fsc,fsc,fsc,fsc,fsc};
        partCount = 8+4*12+16*6+1;
        
        CubeColorModel sc[] = new CubeColorModel[9];
        int i;
        
        String[] partNames = {
            "Corner urf",  "Corner dfr",
            "Corner ubr", "Corner drb",
            "Corner ulb", "Corner dbl",
            "Corner ufl", "Corner dlf",
            
            "Edge ur1",
            "Edge rf1",
            "Edge dr1",
            "Edge bu1",
            "Edge rb1",
            "Edge bd1",
            "Edge ul1",
            "Edge lb1",
            "Edge dl1",
            "Edge fu1",
            "Edge lf1",
            "Edge fd1",
            
            "Edge ur2",
            "Edge rf2",
            "Edge dr2",
            "Edge bu2",
            "Edge rb2",
            "Edge bd2",
            "Edge ul2",
            "Edge lb2",
            "Edge dl2",
            "Edge fu2",
            "Edge lf2",
            "Edge fd2",
            
            "Edge ur3",
            "Edge rf3",
            "Edge dr3",
            "Edge bu3",
            "Edge rb3",
            "Edge bd3",
            "Edge ul3",
            "Edge lb3",
            "Edge dl3",
            "Edge fu3",
            "Edge lf3",
            "Edge fd3",
            
            "Edge ur4",
            "Edge rf4",
            "Edge dr4",
            "Edge bu4",
            "Edge rb4",
            "Edge bd4",
            "Edge ul4",
            "Edge lb4",
            "Edge dl4",
            "Edge fu4",
            "Edge lf4",
            "Edge fd4",
            
            "Side r1",
            "Side u1",
            "Side f1",
            "Side l1",
            "Side d1",
            "Side b1",

            "Side r2",
            "Side u2",
            "Side f2",
            "Side l2",
            "Side d2",
            "Side b2",

            "Side r3",
            "Side u3",
            "Side f3",
            "Side l3",
            "Side d3",
            "Side b3",

            "Side r4",
            "Side u4",
            "Side f4",
            "Side l4",
            "Side d4",
            "Side b4",

            "Side r5",
            "Side u5",
            "Side f5",
            "Side l5",
            "Side d5",
            "Side b5",

            "Side r6",
            "Side u6",
            "Side f6",
            "Side l6",
            "Side d6",
            "Side b6",

            "Side r7",
            "Side u7",
            "Side f7",
            "Side l7",
            "Side d7",
            "Side b7",

            "Side r8",
            "Side u8",
            "Side f8",
            "Side l8",
            "Side d8",
            "Side b8",

            "Side r9",
            "Side u9",
            "Side f9",
            "Side l9",
            "Side d9",
            "Side b9",

            "Side r10",
            "Side u10",
            "Side f10",
            "Side l10",
            "Side d10",
            "Side b10",

            "Side r11",
            "Side u11",
            "Side f11",
            "Side l11",
            "Side d11",
            "Side b11",

            "Side r12",
            "Side u12",
            "Side f12",
            "Side l12",
            "Side d12",
            "Side b12",

            "Side r13",
            "Side u13",
            "Side f13",
            "Side l13",
            "Side d13",
            "Side b13",

            "Side r14",
            "Side u14",
            "Side f14",
            "Side l14",
            "Side d14",
            "Side b14",

            "Side r15",
            "Side u15",
            "Side f15",
            "Side l15",
            "Side d15",
            "Side b15",

            "Side r16",
            "Side u16",
            "Side f16",
            "Side l16",
            "Side d16",
            "Side b16",
            
            "Center"
        };
        String[] stickerNames = {
            "Right", "Up", "Front", "Left", "Down", "Back"
        };
        colors.removeAllChildren();
        colors.add(sc[0] = new CubeColorModel("Stickers Right", new Color(255,210,0))); //Right: yellow
        colors.add(sc[1] = new CubeColorModel("Stickers Up",    new Color(0,51,115))); //Up:    blue
        colors.add(sc[2] = new CubeColorModel("Stickers Front", new Color(140,0,15))); //Front: red
        colors.add(sc[3] = new CubeColorModel("Stickers Left",  new Color(248,248,248))); //Left:  white
        colors.add(sc[4] = new CubeColorModel("Stickers Down",  new Color(0,115,47))); //Down:  green
        colors.add(sc[5] = new CubeColorModel("Stickers Back",  new Color(255,70,0))); //Back:  orange
        colors.add(sc[6] = new CubeColorModel("Outer Parts",    new Color(16,16,16))); //dark gray
        colors.add(sc[7] = new CubeColorModel("Center Part",    new Color(248,248,248))); //white
        colors.add(sc[8] = new CubeColorModel("Outlines",       new Color(  0,  0,  0))); //black
        
        // Try to preserve existing part nodes
        if (parts.getChildCount() != getPartCount()) {
            //            parts.removeAllChildren();
            parts.removeAllChildren();
            for (i=getPartCount() - 1; i > -1; i--) {
                CubePartModel m = new CubePartModel();
                //                parts.add(m);
                parts.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the part nodes
        for (i=getPartCount() - 1; i > -1; i--) {
            CubePartModel m = (CubePartModel) parts.getChildAt(i);
            m.setFillColorModel((i == getPartCount() - 1) ? sc[7] : sc[6]);
            m.setOutlineColorModel(sc[8]);
            m.setName(partNames[i]);
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        
        // Try to preserve existing sticker nodes
        if (stickers.getChildCount() != getStickerCount()) {
            //            stickers.removeAllChildren();
            stickers.removeAllChildren();
            for (i=getStickerCount() - 1; i > -1; i--) {
                CubeStickerModel m = new CubeStickerModel();
                //                stickers.add(m);
                stickers.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the sticker nodes
        for (i=getStickerCount() - 1; i > -1; i--) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i);
            m.setFillColorModel(sc[i / fsc]);
            m.setName(stickerNames[i / fsc]+' '+(i % fsc + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
    }
    private void constructCube7() {
        cube3DClass = Cube7Idx3D.class;
        simpleCube3DClass = Cube7Geom3D.class;
        
        EntityModel colors = getColors();
        EntityModel parts = getParts();
        EntityModel stickers = getStickers();
        
        int fsc = 7*7;
        stickerCount = 6 * fsc;
        stickerCountPerFace = new int[] {fsc,fsc,fsc,fsc,fsc,fsc};
        partCount = 8+5*12+25*6+1;
        
        CubeColorModel sc[] = new CubeColorModel[9];
        int i;
        
        String[] partNames = {
            "Corner urf",  "Corner dfr",
            "Corner ubr", "Corner drb",
            "Corner ulb", "Corner dbl",
            "Corner ufl", "Corner dlf",
            
            "Edge ur1",
            "Edge rf1",
            "Edge dr1",
            "Edge bu1",
            "Edge rb1",
            "Edge bd1",
            "Edge ul1",
            "Edge lb1",
            "Edge dl1",
            "Edge fu1",
            "Edge lf1",
            "Edge fd1",
            
            "Edge ur2",
            "Edge rf2",
            "Edge dr2",
            "Edge bu2",
            "Edge rb2",
            "Edge bd2",
            "Edge ul2",
            "Edge lb2",
            "Edge dl2",
            "Edge fu2",
            "Edge lf2",
            "Edge fd2",
            
            "Edge ur3",
            "Edge rf3",
            "Edge dr3",
            "Edge bu3",
            "Edge rb3",
            "Edge bd3",
            "Edge ul3",
            "Edge lb3",
            "Edge dl3",
            "Edge fu3",
            "Edge lf3",
            "Edge fd3",
            
            "Edge ur4",
            "Edge rf4",
            "Edge dr4",
            "Edge bu4",
            "Edge rb4",
            "Edge bd4",
            "Edge ul4",
            "Edge lb4",
            "Edge dl4",
            "Edge fu4",
            "Edge lf4",
            "Edge fd4",
            
            "Edge ur5",
            "Edge rf5",
            "Edge dr5",
            "Edge bu5",
            "Edge rb5",
            "Edge bd5",
            "Edge ul5",
            "Edge lb5",
            "Edge dl5",
            "Edge fu5",
            "Edge lf5",
            "Edge fd5",
            
            "Side r1",
            "Side u1",
            "Side f1",
            "Side l1",
            "Side d1",
            "Side b1",

            "Side r2",
            "Side u2",
            "Side f2",
            "Side l2",
            "Side d2",
            "Side b2",

            "Side r3",
            "Side u3",
            "Side f3",
            "Side l3",
            "Side d3",
            "Side b3",

            "Side r4",
            "Side u4",
            "Side f4",
            "Side l4",
            "Side d4",
            "Side b4",

            "Side r5",
            "Side u5",
            "Side f5",
            "Side l5",
            "Side d5",
            "Side b5",

            "Side r6",
            "Side u6",
            "Side f6",
            "Side l6",
            "Side d6",
            "Side b6",

            "Side r7",
            "Side u7",
            "Side f7",
            "Side l7",
            "Side d7",
            "Side b7",

            "Side r8",
            "Side u8",
            "Side f8",
            "Side l8",
            "Side d8",
            "Side b8",

            "Side r9",
            "Side u9",
            "Side f9",
            "Side l9",
            "Side d9",
            "Side b9",

            "Side r10",
            "Side u10",
            "Side f10",
            "Side l10",
            "Side d10",
            "Side b10",

            "Side r11",
            "Side u11",
            "Side f11",
            "Side l11",
            "Side d11",
            "Side b11",

            "Side r12",
            "Side u12",
            "Side f12",
            "Side l12",
            "Side d12",
            "Side b12",

            "Side r13",
            "Side u13",
            "Side f13",
            "Side l13",
            "Side d13",
            "Side b13",

            "Side r14",
            "Side u14",
            "Side f14",
            "Side l14",
            "Side d14",
            "Side b14",

            "Side r15",
            "Side u15",
            "Side f15",
            "Side l15",
            "Side d15",
            "Side b15",

            "Side r16",
            "Side u16",
            "Side f16",
            "Side l16",
            "Side d16",
            "Side b16",

            "Side r17",
            "Side u17",
            "Side f17",
            "Side l17",
            "Side d17",
            "Side b17",

            "Side r18",
            "Side u18",
            "Side f18",
            "Side l18",
            "Side d18",
            "Side b18",

            "Side r19",
            "Side u19",
            "Side f19",
            "Side l19",
            "Side d19",
            "Side b19",

            "Side r20",
            "Side u20",
            "Side f20",
            "Side l20",
            "Side d20",
            "Side b20",

            "Side r21",
            "Side u21",
            "Side f21",
            "Side l21",
            "Side d21",
            "Side b21",

            "Side r22",
            "Side u22",
            "Side f22",
            "Side l22",
            "Side d22",
            "Side b22",

            "Side r23",
            "Side u23",
            "Side f23",
            "Side l23",
            "Side d23",
            "Side b23",

            "Side r24",
            "Side u24",
            "Side f24",
            "Side l24",
            "Side d24",
            "Side b24",

            "Side r25",
            "Side u25",
            "Side f25",
            "Side l25",
            "Side d25",
            "Side b25",
            
            "Center"
        };
        String[] stickerNames = {
            "Right", "Up", "Front", "Left", "Down", "Back"
        };
        colors.removeAllChildren();
        colors.add(sc[0] = new CubeColorModel("Stickers Right", new Color(255,210,0))); //Right: yellow
        colors.add(sc[1] = new CubeColorModel("Stickers Up",    new Color(0,51,115))); //Up:    blue
        colors.add(sc[2] = new CubeColorModel("Stickers Front", new Color(140,0,15))); //Front: red
        colors.add(sc[3] = new CubeColorModel("Stickers Left",  new Color(248,248,248))); //Left:  white
        colors.add(sc[4] = new CubeColorModel("Stickers Down",  new Color(0,115,47))); //Down:  green
        colors.add(sc[5] = new CubeColorModel("Stickers Back",  new Color(255,70,0))); //Back:  orange
        colors.add(sc[6] = new CubeColorModel("Outer Parts",    new Color(16,16,16))); //dark gray
        colors.add(sc[7] = new CubeColorModel("Center Part",    new Color(248,248,248))); //white
        colors.add(sc[8] = new CubeColorModel("Outlines",       new Color(  0,  0,  0))); //black
        
        // Try to preserve existing part nodes
        if (parts.getChildCount() != getPartCount()) {
            //            parts.removeAllChildren();
            parts.removeAllChildren();
            for (i=getPartCount() - 1; i > -1; i--) {
                CubePartModel m = new CubePartModel();
                //                parts.add(m);
                parts.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the part nodes
        for (i=getPartCount() - 1; i > -1; i--) {
            CubePartModel m = (CubePartModel) parts.getChildAt(i);
            m.setFillColorModel((i == getPartCount() - 1) ? sc[7] : sc[6]);
            m.setOutlineColorModel(sc[8]);
            m.setName(partNames[i]);
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        
        // Try to preserve existing sticker nodes
        if (stickers.getChildCount() != getStickerCount()) {
            //            stickers.removeAllChildren();
            stickers.removeAllChildren();
            for (i=getStickerCount() - 1; i > -1; i--) {
                CubeStickerModel m = new CubeStickerModel();
                //                stickers.add(m);
                stickers.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the sticker nodes
        for (i=getStickerCount() - 1; i > -1; i--) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i);
            m.setFillColorModel(sc[i / fsc]);
            m.setName(stickerNames[i / fsc]+' '+(i % fsc + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
    }
    private void constructVCube7() {
        cube3DClass = VCube7Idx3D.class;
        simpleCube3DClass = Cube7Geom3D.class;
        
        EntityModel colors = getColors();
        EntityModel parts = getParts();
        EntityModel stickers = getStickers();
        
        int fsc = 7*7;
        stickerCount = 6 * fsc;
        stickerCountPerFace = new int[] {fsc,fsc,fsc,fsc,fsc,fsc};
        partCount = 8+5*12+25*6+1;
        
        CubeColorModel sc[] = new CubeColorModel[9];
        int i;
        
        String[] partNames = {
            "Corner urf",  "Corner dfr",
            "Corner ubr", "Corner drb",
            "Corner ulb", "Corner dbl",
            "Corner ufl", "Corner dlf",
            
            "Edge ur1",
            "Edge rf1",
            "Edge dr1",
            "Edge bu1",
            "Edge rb1",
            "Edge bd1",
            "Edge ul1",
            "Edge lb1",
            "Edge dl1",
            "Edge fu1",
            "Edge lf1",
            "Edge fd1",
            
            "Edge ur2",
            "Edge rf2",
            "Edge dr2",
            "Edge bu2",
            "Edge rb2",
            "Edge bd2",
            "Edge ul2",
            "Edge lb2",
            "Edge dl2",
            "Edge fu2",
            "Edge lf2",
            "Edge fd2",
            
            "Edge ur3",
            "Edge rf3",
            "Edge dr3",
            "Edge bu3",
            "Edge rb3",
            "Edge bd3",
            "Edge ul3",
            "Edge lb3",
            "Edge dl3",
            "Edge fu3",
            "Edge lf3",
            "Edge fd3",
            
            "Edge ur4",
            "Edge rf4",
            "Edge dr4",
            "Edge bu4",
            "Edge rb4",
            "Edge bd4",
            "Edge ul4",
            "Edge lb4",
            "Edge dl4",
            "Edge fu4",
            "Edge lf4",
            "Edge fd4",
            
            "Edge ur5",
            "Edge rf5",
            "Edge dr5",
            "Edge bu5",
            "Edge rb5",
            "Edge bd5",
            "Edge ul5",
            "Edge lb5",
            "Edge dl5",
            "Edge fu5",
            "Edge lf5",
            "Edge fd5",
            
            "Side r1",
            "Side u1",
            "Side f1",
            "Side l1",
            "Side d1",
            "Side b1",
            
            "Side r2",
            "Side u2",
            "Side f2",
            "Side l2",
            "Side d2",
            "Side b2",
            
            "Side r3",
            "Side u3",
            "Side f3",
            "Side l3",
            "Side d3",
            "Side b3",
            
            "Side r4",
            "Side u4",
            "Side f4",
            "Side l4",
            "Side d4",
            "Side b4",
            
            "Side r5",
            "Side u5",
            "Side f5",
            "Side l5",
            "Side d5",
            "Side b5",
            
            "Side r6",
            "Side u6",
            "Side f6",
            "Side l6",
            "Side d6",
            "Side b6",
            
            "Side r7",
            "Side u7",
            "Side f7",
            "Side l7",
            "Side d7",
            "Side b7",
            
            "Side r8",
            "Side u8",
            "Side f8",
            "Side l8",
            "Side d8",
            "Side b8",
            
            "Side r9",
            "Side u9",
            "Side f9",
            "Side l9",
            "Side d9",
            "Side b9",
            
            "Side r10",
            "Side u10",
            "Side f10",
            "Side l10",
            "Side d10",
            "Side b10",
            
            "Side r11",
            "Side u11",
            "Side f11",
            "Side l11",
            "Side d11",
            "Side b11",
            
            "Side r12",
            "Side u12",
            "Side f12",
            "Side l12",
            "Side d12",
            "Side b12",
            
            "Side r13",
            "Side u13",
            "Side f13",
            "Side l13",
            "Side d13",
            "Side b13",
            
            "Side r14",
            "Side u14",
            "Side f14",
            "Side l14",
            "Side d14",
            "Side b14",
            
            "Side r15",
            "Side u15",
            "Side f15",
            "Side l15",
            "Side d15",
            "Side b15",
            
            "Side r16",
            "Side u16",
            "Side f16",
            "Side l16",
            "Side d16",
            "Side b16",
            
            "Side r17",
            "Side u17",
            "Side f17",
            "Side l17",
            "Side d17",
            "Side b17",
            
            "Side r18",
            "Side u18",
            "Side f18",
            "Side l18",
            "Side d18",
            "Side b18",
            
            "Side r19",
            "Side u19",
            "Side f19",
            "Side l19",
            "Side d19",
            "Side b19",
            
            "Side r20",
            "Side u20",
            "Side f20",
            "Side l20",
            "Side d20",
            "Side b20",
            
            "Side r21",
            "Side u21",
            "Side f21",
            "Side l21",
            "Side d21",
            "Side b21",
            
            "Side r22",
            "Side u22",
            "Side f22",
            "Side l22",
            "Side d22",
            "Side b22",
            
            "Side r23",
            "Side u23",
            "Side f23",
            "Side l23",
            "Side d23",
            "Side b23",
            
            "Side r24",
            "Side u24",
            "Side f24",
            "Side l24",
            "Side d24",
            "Side b24",
            
            "Side r25",
            "Side u25",
            "Side f25",
            "Side l25",
            "Side d25",
            "Side b25",
            
            "Center"
        };
        String[] stickerNames = {
            "Right", "Up", "Front", "Left", "Down", "Back"
        };
        colors.removeAllChildren();
        colors.add(sc[0] = new CubeColorModel("Stickers Right", new Color(255,210,0))); //Right: yellow
        colors.add(sc[1] = new CubeColorModel("Stickers Up",    new Color(0,51,115))); //Up:    blue
        colors.add(sc[2] = new CubeColorModel("Stickers Front", new Color(140,0,15))); //Front: red
        colors.add(sc[3] = new CubeColorModel("Stickers Left",  new Color(248,248,248))); //Left:  white
        colors.add(sc[4] = new CubeColorModel("Stickers Down",  new Color(0,115,47))); //Down:  green
        colors.add(sc[5] = new CubeColorModel("Stickers Back",  new Color(255,70,0))); //Back:  orange
        colors.add(sc[6] = new CubeColorModel("Outer Parts",    new Color(16,16,16))); //dark gray
        colors.add(sc[7] = new CubeColorModel("Center Part",    new Color(248,248,248))); //white
        colors.add(sc[8] = new CubeColorModel("Outlines",       new Color(  0,  0,  0))); //black
        
        // Try to preserve existing part nodes
        if (parts.getChildCount() != getPartCount()) {
            //            parts.removeAllChildren();
            parts.removeAllChildren();
            for (i=getPartCount() - 1; i > -1; i--) {
                CubePartModel m = new CubePartModel();
                //                parts.add(m);
                parts.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the part nodes
        for (i=getPartCount() - 1; i > -1; i--) {
            CubePartModel m = (CubePartModel) parts.getChildAt(i);
            m.setFillColorModel((i == getPartCount() - 1) ? sc[7] : sc[6]);
            m.setOutlineColorModel(sc[8]);
            m.setName(partNames[i]);
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        
        // Try to preserve existing sticker nodes
        if (stickers.getChildCount() != getStickerCount()) {
            //            stickers.removeAllChildren();
            stickers.removeAllChildren();
            for (i=getStickerCount() - 1; i > -1; i--) {
                CubeStickerModel m = new CubeStickerModel();
                //                stickers.add(m);
                stickers.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the sticker nodes
        for (i=getStickerCount() - 1; i > -1; i--) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i);
            m.setFillColorModel(sc[i / fsc]);
            m.setName(stickerNames[i / fsc]+' '+(i % fsc + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
    }
    private void constructVCube6() {
        cube3DClass = VCube6Idx3D.class;
        simpleCube3DClass = null;
        
        EntityModel colors = getColors();
        EntityModel parts = getParts();
        EntityModel stickers = getStickers();
        
        int fsc = 6*6;
        stickerCount = 6 * fsc;
        stickerCountPerFace = new int[] {fsc,fsc,fsc,fsc,fsc,fsc};
        partCount = 8+4*12+16*6+1;
        
        CubeColorModel sc[] = new CubeColorModel[9];
        int i;
        
        String[] partNames = {
            "Corner urf",  "Corner dfr",
            "Corner ubr", "Corner drb",
            "Corner ulb", "Corner dbl",
            "Corner ufl", "Corner dlf",
            
            "Edge ur1",
            "Edge rf1",
            "Edge dr1",
            "Edge bu1",
            "Edge rb1",
            "Edge bd1",
            "Edge ul1",
            "Edge lb1",
            "Edge dl1",
            "Edge fu1",
            "Edge lf1",
            "Edge fd1",
            
            "Edge ur2",
            "Edge rf2",
            "Edge dr2",
            "Edge bu2",
            "Edge rb2",
            "Edge bd2",
            "Edge ul2",
            "Edge lb2",
            "Edge dl2",
            "Edge fu2",
            "Edge lf2",
            "Edge fd2",
            
            "Edge ur3",
            "Edge rf3",
            "Edge dr3",
            "Edge bu3",
            "Edge rb3",
            "Edge bd3",
            "Edge ul3",
            "Edge lb3",
            "Edge dl3",
            "Edge fu3",
            "Edge lf3",
            "Edge fd3",
            
            "Edge ur4",
            "Edge rf4",
            "Edge dr4",
            "Edge bu4",
            "Edge rb4",
            "Edge bd4",
            "Edge ul4",
            "Edge lb4",
            "Edge dl4",
            "Edge fu4",
            "Edge lf4",
            "Edge fd4",
            
            "Side r1",
            "Side u1",
            "Side f1",
            "Side l1",
            "Side d1",
            "Side b1",

            "Side r2",
            "Side u2",
            "Side f2",
            "Side l2",
            "Side d2",
            "Side b2",

            "Side r3",
            "Side u3",
            "Side f3",
            "Side l3",
            "Side d3",
            "Side b3",

            "Side r4",
            "Side u4",
            "Side f4",
            "Side l4",
            "Side d4",
            "Side b4",

            "Side r5",
            "Side u5",
            "Side f5",
            "Side l5",
            "Side d5",
            "Side b5",

            "Side r6",
            "Side u6",
            "Side f6",
            "Side l6",
            "Side d6",
            "Side b6",

            "Side r7",
            "Side u7",
            "Side f7",
            "Side l7",
            "Side d7",
            "Side b7",

            "Side r8",
            "Side u8",
            "Side f8",
            "Side l8",
            "Side d8",
            "Side b8",

            "Side r9",
            "Side u9",
            "Side f9",
            "Side l9",
            "Side d9",
            "Side b9",

            "Side r10",
            "Side u10",
            "Side f10",
            "Side l10",
            "Side d10",
            "Side b10",

            "Side r11",
            "Side u11",
            "Side f11",
            "Side l11",
            "Side d11",
            "Side b11",

            "Side r12",
            "Side u12",
            "Side f12",
            "Side l12",
            "Side d12",
            "Side b12",

            "Side r13",
            "Side u13",
            "Side f13",
            "Side l13",
            "Side d13",
            "Side b13",

            "Side r14",
            "Side u14",
            "Side f14",
            "Side l14",
            "Side d14",
            "Side b14",

            "Side r15",
            "Side u15",
            "Side f15",
            "Side l15",
            "Side d15",
            "Side b15",

            "Side r16",
            "Side u16",
            "Side f16",
            "Side l16",
            "Side d16",
            "Side b16",
            
            "Center"
        };
        String[] stickerNames = {
            "Right", "Up", "Front", "Left", "Down", "Back"
        };
        colors.removeAllChildren();
        colors.add(sc[0] = new CubeColorModel("Stickers Right", new Color(255,210,0))); //Right: yellow
        colors.add(sc[1] = new CubeColorModel("Stickers Up",    new Color(0,51,115))); //Up:    blue
        colors.add(sc[2] = new CubeColorModel("Stickers Front", new Color(140,0,15))); //Front: red
        colors.add(sc[3] = new CubeColorModel("Stickers Left",  new Color(248,248,248))); //Left:  white
        colors.add(sc[4] = new CubeColorModel("Stickers Down",  new Color(0,115,47))); //Down:  green
        colors.add(sc[5] = new CubeColorModel("Stickers Back",  new Color(255,70,0))); //Back:  orange
        colors.add(sc[6] = new CubeColorModel("Outer Parts",    new Color(16,16,16))); //dark gray
        colors.add(sc[7] = new CubeColorModel("Center Part",    new Color(248,248,248))); //white
        colors.add(sc[8] = new CubeColorModel("Outlines",       new Color(  0,  0,  0))); //black
        
        // Try to preserve existing part nodes
        if (parts.getChildCount() != getPartCount()) {
            //            parts.removeAllChildren();
            parts.removeAllChildren();
            for (i=getPartCount() - 1; i > -1; i--) {
                CubePartModel m = new CubePartModel();
                //                parts.add(m);
                parts.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the part nodes
        for (i=getPartCount() - 1; i > -1; i--) {
            CubePartModel m = (CubePartModel) parts.getChildAt(i);
            m.setFillColorModel((i == getPartCount() - 1) ? sc[7] : sc[6]);
            m.setOutlineColorModel(sc[8]);
            m.setName(partNames[i]);
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
        
        // Try to preserve existing sticker nodes
        if (stickers.getChildCount() != getStickerCount()) {
            //            stickers.removeAllChildren();
            stickers.removeAllChildren();
            for (i=getStickerCount() - 1; i > -1; i--) {
                CubeStickerModel m = new CubeStickerModel();
                //                stickers.add(m);
                stickers.add(m);
                m.addPropertyChangeListener(this);
            }
        }
        // Initialize the sticker nodes
        for (i=getStickerCount() - 1; i > -1; i--) {
            CubeStickerModel m = (CubeStickerModel) stickers.getChildAt(i);
            m.setFillColorModel(sc[i / fsc]);
            m.setName(stickerNames[i / fsc]+' '+(i % fsc + 1));
            //m.setVisible(true);   ignore: we reuse setVisible from the previous Cube
        }
    }
    
    /**
     * Creates a deep copy of this cube model.
     */
    @Override
    public CubeModel clone() {
        int i;
        
        CubeModel that = (CubeModel) super.clone();
        
        // Add the holders for the parts, stickers and colors.
        // ---------------------------------------------------
        that.add(new EntityModel("Parts", true));
        that.add(new EntityModel("Stickers", true));
        that.add(new EntityModel("Colors", true));
        
        // Now clone the children
        // ----------------------
        // We clone the color models first, so that we can
        // make the clone refer to the cloned colors.
        for (EntityModel child:getColors().getChildren()) {
            CubeColorModel thisItem = (CubeColorModel) child;
            CubeColorModel thatItem = (CubeColorModel) thisItem.clone();
            that.getColors().add(thatItem);
            thatItem.addPropertyChangeListener(that);
        }
        
        // Clone the stickers and make them refer to the cloned colors.
        for (EntityModel child:getStickers().getChildren()) {
            CubeStickerModel thisItem = (CubeStickerModel) child;
            CubeStickerModel thatItem = (CubeStickerModel) thisItem.clone();
            thatItem.setFillColorModel((CubeColorModel) that.getColors().getChildAt(this.getColors().getIndex(thisItem.getFillColorModel())));
            that.getStickers().add(thatItem);
            thatItem.addPropertyChangeListener(that);
        }
        
        // Clone the parts and make them refer to the cloned colors.
        for(EntityModel child:getParts().getChildren()) {
            CubePartModel thisItem = (CubePartModel) child;
            CubePartModel thatItem = (CubePartModel) thisItem.clone();
            thatItem.setFillColorModel((CubeColorModel) that.getColors().getChildAt(this.getColors().getIndex(thisItem.getFillColorModel())));
            thatItem.setOutlineColorModel((CubeColorModel) that.getColors().getChildAt(this.getColors().getIndex(thisItem.getOutlineColorModel())));
            that.getParts().add(thatItem);
            thatItem.addPropertyChangeListener(that);
        }
        
        return that;
    }
    
    @Override
    public int getPartCount() {
        return partCount;
    }
    
    @Override
    public int getStickerCount() {
        return stickerCount;
    }
    
    @Override
    public Color getStickerFillColor(int stickerIndex) {
        if (stickerIndex >= getStickers().getChildCount()) return null;
        return ((CubeStickerModel) getStickers().getChildAt(stickerIndex)).getFillColorModel().getColor();
    }
    
    @Override
    public boolean isStickerVisible(int stickerIndex) {
        if (stickerIndex >= getStickers().getChildCount()) return false;
        return ((CubeStickerModel) getStickers().getChildAt(stickerIndex)).isVisible();
    }
    
    @Override
    public Color getPartOutlineColor(int partIndex) {
        return ((CubePartModel) getParts().getChildAt(partIndex)).getOutlineColorModel().getColor();
    }
    
    @Override
    public boolean isPartVisible(int partIndex) {
        return ((CubePartModel) getParts().getChildAt(partIndex)).isVisible();
    }
    
    @Override
    public Color getPartFillColor(int partIndex) {
        return ((CubePartModel) getParts().getChildAt(partIndex)).getFillColorModel().getColor();
    }
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (suppressPropertyChangeEvents == 0) {
            Object source = evt.getSource();
            if (source instanceof CubeColorModel) {
                firePropertyChange(evt.getPropertyName()+"."+getColors().getIndex((CubeColorModel) source), evt.getOldValue(), evt.getNewValue());
            } else if (source instanceof CubePartModel) {
                firePropertyChange(evt.getPropertyName()+"."+getParts().getIndex((CubePartModel) source), evt.getOldValue(), evt.getNewValue());
            } else if (source instanceof CubeStickerModel) {
                firePropertyChange(evt.getPropertyName()+"."+getStickers().getIndex((CubeStickerModel) source), evt.getOldValue(), evt.getNewValue());
            }
        }
    }
    
    @Override
    public float getScaleFactor() {
        return scale / 100f;
    }
    
    @Override
    public float getExplosionFactor() {
        return explode / 100f;
    }
    
    @Override
    public float getAlpha() {
        return (float) (alpha / 180d * Math.PI);
    }
    
    @Override
    public float getBeta() {
        return (float) (beta / 180d * Math.PI);
    }
    
    public void setAlpha(double d) {
    }
    
    public void setBeta(double d) {
    }
    /**
     * Returns true if the node may be removed from its parent.
     */
    @Override
    public boolean isRemovable() {
        return super.isRemovable();
        /* Only return true, if cube is not the default
        if (getDocument() == null || getDocument().getDefaultCube() != this) {
            return super.isRemovable();
        } else {
            return false;
        }*/
    }
    @Override
    public void reset() {
        // XXX - Implement me.
        // What do I need this for?
    }
    @Override
    public int getStickerOffset(int index) {
        int offset = 0;
        for (int i=0; i < index; i++) {
            offset += stickerCountPerFace[i];
        }
        return offset;
    }
    
    @Override
    public int getFaceCount() {
        return stickerCountPerFace.length;
    }
    
    @Override
    public int getStickerCount(int face) {
        return stickerCountPerFace[face];
    }
    
    @Override
    public Color getStickerOutlineColor(int index) {
        // Sticker outline colors are not (yet) supported.
        // Maybe we don't need them at all?
        return null;
    }
    
    @Override
    public Image getStickersImage() {
        return stickersImageModel.getImage();
    }
    
    public void setFrontBgColor(Color newValue) {
        Color oldValue = frontBgColor;
        frontBgColor = newValue;
        firePropertyChange(FRONT_BG_COLOR_PROPERTY, oldValue, newValue);
    }
    
    public void setFrontBgImage(Image newValue) {
        Image oldValue = frontBgImageModel.getImage();
        frontBgImageModel.setImage(newValue);
        firePropertyChange(FRONT_BG_IMAGE_PROPERTY, oldValue, newValue);
    }
    
    @Override
    public Color getFrontBgColor() {
        return frontBgColor;
    }
    
    @Override
    public Image getFrontBgImage() {
        return frontBgImageModel.getImage();
    }
    
    public void setRearBgColor(Color newValue) {
        Color oldValue = rearBgColor;
        rearBgColor = newValue;
        firePropertyChange(REAR_BG_COLOR_PROPERTY, oldValue, newValue);
    }
    
    public void setRearBgImage(Image newValue) {
        Image oldValue = rearBgImageModel.getImage();
        rearBgImageModel.setImage(newValue);
        firePropertyChange(REAR_BG_IMAGE_PROPERTY, oldValue, newValue);
    }
    @Override
    public Color getRearBgColor() {
        return rearBgColor;
    }
    
    @Override
    public Image getRearBgImage() {
        return rearBgImageModel.getImage();
    }
    
    public ImageWellModel getStickersImageModel() {
        return stickersImageModel;
    }
    public ImageWellModel getRearBgImageModel() {
        return rearBgImageModel;
    }
    public ImageWellModel getFrontBgImageModel() {
        return frontBgImageModel;
    }
    
    @Override
    public boolean isFrontBgImageVisible() {
        return frontBgImageVisible;
    }
    
    @Override
    public boolean isRearBgImageVisible() {
        return rearBgImageVisible;
    }
    
    @Override
    public boolean isStickersImageVisible() {
        return stickersImageVisible;
    }
    
    public void basicSetFrontBgImageVisible(boolean newValue) {
        frontBgImageVisible = newValue;
    }
    public void setFrontBgImageVisible(boolean newValue) {
        boolean oldValue = frontBgImageVisible;
        basicSetFrontBgImageVisible(newValue);
        firePropertyChange(FRONT_BG_IMAGE_VISIBLE_PROPERTY, oldValue, newValue);
    }
    public void basicSetRearBgImageVisible(boolean newValue) {
        rearBgImageVisible = newValue;
    }
    public void setRearBgImageVisible(boolean newValue) {
        boolean oldValue = rearBgImageVisible;
        basicSetRearBgImageVisible(newValue);
        firePropertyChange(REAR_BG_IMAGE_VISIBLE_PROPERTY, oldValue, newValue);
    }
    public void basicSetStickersImageVisible(boolean value) {
        stickersImageVisible = value;
    }
    public void setStickersImageVisible(boolean newValue) {
        boolean oldValue = stickersImageVisible;
        basicSetStickersImageVisible(newValue);
        firePropertyChange(STICKERS_IMAGE_VISIBLE_PROPERTY, oldValue, newValue);
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        Object src = e.getSource();
        if (src == stickersImageModel) {
            firePropertyChange(STICKERS_IMAGE_PROPERTY, null, stickersImageModel.getImage());
        } else if (src == frontBgImageModel) {
            firePropertyChange(FRONT_BG_IMAGE_PROPERTY, null, frontBgImageModel.getImage());
        } else if (src == rearBgImageModel) {
            firePropertyChange(REAR_BG_IMAGE_PROPERTY, null, rearBgImageModel.getImage());
        }
    }
    /*
    public void finalize() {
        System.out.println("CubeModel.finalize "+this);
    }*/
    
    @Override
    public void dispose() {
        super.dispose();
        
        if (stickersImageModel != null) {
            stickersImageModel.removeChangeListener(this);
            stickersImageModel.setImage(null);
            stickersImageModel = null;
        }
        if (frontBgImageModel != null) {
            frontBgImageModel.removeChangeListener(this);
            frontBgImageModel.setImage(null);
            frontBgImageModel = null;
        }
        if (rearBgImageModel != null) {
            rearBgImageModel.removeChangeListener(this);
            rearBgImageModel.setImage(null);
            rearBgImageModel = null;
        }
    }
    
    @Override
    public void setValueIsAdjusting(boolean newValue) {
        boolean oldValue = valueIsAdjusting;
        valueIsAdjusting = newValue;
        firePropertyChange("valueIsAdjusting", oldValue, newValue);
        
    }
    
    @Override
    public boolean getValueIsAdjusting() {
        return valueIsAdjusting;
    }

    @Override
    public float getPartExplosion(int index) {
        return 0f;
    }

    @Override
    public float getStickerExplosion(int index) {
        return 0f;
    }

    @Override
    public int getTwistDuration() {
        return twistDuration;
    }
    public void setTwistDuration(int newValue) {
        int oldValue = twistDuration;
        twistDuration = newValue;
                firePropertyChange(TWIST_DURATION_PROPERTY, oldValue, newValue);
    }
    @Override
    public boolean isDefaultCube() {
        return getDocument().getDefaultCube() == this;
    }

    @Override
    public void setTo(CubeAttributes that) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}