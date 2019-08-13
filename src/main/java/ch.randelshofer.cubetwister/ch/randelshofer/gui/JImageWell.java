/* @(#)JImageWell.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui;

import ch.randelshofer.gui.icon.BusyIcon;
import ch.randelshofer.gui.border.ImageBevelBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import javax.accessibility.AccessibleContext;
import org.jhotdraw.app.action.edit.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleSelection;
import javax.imageio.*;
import javax.imageio.stream.*;
import org.jhotdraw.gui.BackgroundTask;
import org.jhotdraw.gui.EditableComponent;
import org.jhotdraw.gui.Worker;
import org.jhotdraw.gui.datatransfer.ImageTransferable;
import org.jhotdraw.util.Images;

/**
 * JImageWell.
 *
 * @author Werner Randelshofer
 */
public class JImageWell extends JComponent implements EditableComponent {
    private final static long serialVersionUID = 1L;

    /**
     * Methods print diagnostic output to System.out, if true.
     */
    private static boolean VERBOSE = false;
    /**
     * The drag gesture recognizer is used to identify a drag initiaging user
     * gesture.
     */
    private DragGestureRecognizer dragGestureRecognizer;
    private ImageWellModel model;
    private JFileChooser fileChooser;
    /**
     * Name of DragAction/DropAction. Used for diagnostic output only.
     */
    private final static HashMap<Integer, String> dndAction = new HashMap<Integer, String>();
    /**
     * The preview dialog. This variable is only non-null, when the preview
     * dialog is showing.
     */
    private PreviewDialog previewDialog;
    /**
     * The text is displayed in the center of the color well. Default value:
     * null.
     */
    private String text;
    private Worker<BufferedImage> thumbnailWorker;
    private BufferedImage thumbnailImage;
    private Image thumbnailSource;

    static {
        dndAction.put(DnDConstants.ACTION_NONE, "NONE");
        dndAction.put(DnDConstants.ACTION_COPY, "COPY");
        dndAction.put(DnDConstants.ACTION_MOVE, "MOVE");
        dndAction.put(DnDConstants.ACTION_COPY_OR_MOVE, "COPY OR MOVE");
        dndAction.put(DnDConstants.ACTION_LINK, "LINK");
        dndAction.put(DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK, "COPY OR MOVE OR LINK");
    }
    /**
     * This attribute is set to true, when the component is an active drag
     * target. The value is set to true, when a dragEnter event is received and
     * set to false when a dragExit or a drop event is received.
     */
    private boolean isUnderDrag;
    /**
     * This attribute is set to true, when the component is the source of the
     * drag and drop operation. This is used to prevent, that the data object is
     * deleted, when we are doing a MOVE on itself.
     */
    private boolean isDragSource;
    private int isBusy;
    public final static String BUSY_PROPERTY = "busy";
    public final static String TEXT_PROPERTY = "text";

    @Override
    public boolean isSelectionEmpty() {
        return !model.hasImage();
    }

    /**
     * This inner class is used to prevent the API from being cluttered by
     * internal listeners.
     */
    private class EventHandler
            implements DropTargetListener, DragSourceListener, DragGestureListener,
            ChangeListener, KeyListener, ClipboardOwner, FocusListener, MouseListener {
        // ===================================================================
        // Methods for drag source behaviour
        // ===================================================================

        public void dragGestureRecognized(DragGestureEvent evt) {
            if (VERBOSE) {
                System.out.println("src dragGestureRecognized dragAction:" + dndAction.get(evt.getDragAction()));
            }

            // Determine where the drag gesture occured
            Point dragOrigin = evt.getDragOrigin();

            // Do nothing if we are disabled or if we don't have
            // a suitable value.
            if (!isEnabled() || model.getImage() == null) {
                if (VERBOSE) {
                    System.out.println("src ignoring drag gesture");
                }
                return;
            }

            // Create a Transferable from the model
            Transferable transferable = exportTransferable();
            if (transferable == null) {
                if (VERBOSE) {
                    System.out.println("src no transferable for selected elements");
                }
                return;
            }

            // Start the drag
            if (VERBOSE) {
                System.out.println("src starting drag");
            }
            if (DragSource.isDragImageSupported()) {
                Rectangle r = getImageRect();
                Point imageOffset = new Point(-dragOrigin.x + r.x, -dragOrigin.y + r.y);
                Image image = getDragImage();
                evt.startDrag(null, image, imageOffset, transferable, this);
            } else {
                evt.startDrag(null, transferable, this);
            }
            isDragSource = true;
        }

        public void dragEnter(DragSourceDragEvent evt) {
            if (VERBOSE) {
                System.out.println("src dragEnter dropAction:" + dndAction.get(evt.getDropAction()));
            }
        }

        public void dragOver(DragSourceDragEvent evt) {
            //if (VERBOSE) System.out.println("src dragOver dropAction:"+dndAction.get(new Integer(evt.getDropAction())));
        }

        public void dropActionChanged(DragSourceDragEvent evt) {
            if (VERBOSE) {
                System.out.println("src dropActionChanged dropAction:" + dndAction.get(evt.getDropAction()));
            }
        }

        public void dragExit(DragSourceEvent evt) {
            if (VERBOSE) {
                System.out.println("src dragExit");
            }
        }

        public void dragDropEnd(DragSourceDropEvent evt) {
            if (VERBOSE) {
                System.out.println("src dragDropEnd success:" + evt.getDropSuccess() + " dropAction:" + dndAction.get(evt.getDropAction()));
            }

            // If the drop was successful and if it was
            // a move operation, we remove the dragged items from
            // our model.
            int dropAction = evt.getDropAction();

            if (evt.getDropSuccess() && (dropAction & getPermittedDragActions()) == DnDConstants.ACTION_MOVE) {
                delete();
            }
            isDragSource = false;
        }

        public void drop(DropTargetDropEvent evt) {
            if (VERBOSE) {
                System.out.println("tgt drop dropAction:" + dndAction.get(evt.getDropAction()));
            }

            // Can not drag/drop on myself
            if (isDragSource) {
                evt.rejectDrop();
            }

            // Reject the drop if the action is ACTION_NONE
            // or if we don't have a suitable model
            int dropAction = evt.getDropAction();
            if (!isEnabled() || ((dropAction & getPermittedDropActions()) == DnDConstants.ACTION_NONE)) {
                if (VERBOSE) {
                    System.out.println("tgt drop reject not enabled or no action match");
                }
                evt.rejectDrop();
                // Erase the drop target state variables
                isUnderDrag = false;
                repaint();

                return;
            }

            // Reject the drop if we can't import the data at the current
            // insertion point
            if (!isImportable(evt.getCurrentDataFlavors(), dropAction)) {
                if (VERBOSE) {
                    System.out.println("tgt drop reject transfer data not importable");
                }
                evt.rejectDrop();

                // Erase the drop target state variables
                isUnderDrag = false;
                repaint();
                return;
            }

            // Accept the drop
            evt.acceptDrop(dropAction);

            // import the data
            try {
                boolean success = importTransferable(evt.getTransferable(), dropAction);
                if (VERBOSE) {
                    System.out.println("tgt drop complete success=" + success);
                }

                // Report success or failure
                evt.dropComplete(success);
            } catch (Exception e) {
                e.printStackTrace();
                if (VERBOSE) {
                    System.out.println("tgt drop complete success=false");
                }
                evt.dropComplete(false);
            }

            // Erase the drop target state variables
            isUnderDrag = false;
            repaint();
        }
        // ===================================================================
        // Methods for drop target behaviour
        // ===================================================================

        public void dragEnter(DropTargetDragEvent evt) {
            if (VERBOSE) {
                System.out.println("tgt dragEnter action:" + dndAction.get(evt.getDropAction()) + " " + evt.getLocation());
                DataFlavor[] flavors = evt.getCurrentDataFlavors();
                for (int i = 0; i < flavors.length; i++) {
                    System.out.println("              flavor:" + flavors[i]);
                }
            }

            // Reject the drag, if we are disabled or if we don't have a suitable model
            if (!isEnabled()) {
                evt.rejectDrag();
                return;
            }

            isUnderDrag = true;
            repaint();
        }

        /**
         * Called when a drag operation is ongoing on the
         * <code>DropTarget</code>.
         * <P>
         * @param evt the <code>DropTargetDragEvent</code>
         */
        public void dragOver(DropTargetDragEvent evt) {
            if (VERBOSE) {
                System.out.println("tgt dragOver action:" + dndAction.get(evt.getDropAction()) + " " + evt.getLocation());
            }
        }

        public void dragExit(DropTargetEvent evt) {
            if (VERBOSE) {
                System.out.println("tgt dragExit");
            }

            isUnderDrag = false;
            repaint();
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent evt) {
            if (VERBOSE) {
                System.out.println("tgt drpActionChanged dropAction:" + dndAction.get(evt.getDropAction()));
            }

            // Reject the drag, if we don't have a suitable model or state
            if (!isEnabled()) {
                evt.rejectDrag();
                return;
            }

            // Check if we can import the data at the current
            // insertion point
            int dropAction = evt.getDropAction();

            evt.acceptDrag(DnDConstants.ACTION_NONE);
        }

        public void stateChanged(ChangeEvent e) {
            if (!isBusy()) {
                repaintThumbnail();
                if (previewDialog != null) {
                    previewDialog.updateImage();
                }
            }
        }

        public void keyPressed(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                    delete();
                    break;
                case KeyEvent.VK_X:
                    cut();
                    break;
                case KeyEvent.VK_C:
                    copy();
                    break;
                case KeyEvent.VK_V:
                    paste();
                    break;
            }
        }

        public void keyTyped(KeyEvent e) {
        }

        public void lostOwnership(Clipboard clipboard, Transferable contents) {
        }

        public void focusGained(FocusEvent e) {
            repaint();
        }

        public void focusLost(FocusEvent e) {
            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (isEnabled()) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
                        && model.getImage() != null) {
                    if (previewDialog != null) {
                        previewDialog.requestFocus();
                    } else {
                        Image image = model.getImage();
                        Object root = SwingUtilities.getRoot(JImageWell.this);
                        previewDialog = null;
                        if (root instanceof Frame) {
                            previewDialog = new PreviewDialog((Frame) root);
                        }
                        if (root instanceof Dialog) {
                            previewDialog = new PreviewDialog((Dialog) root);
                        }
                        if (previewDialog != null) {
                            previewDialog.pack();
                            previewDialog.setVisible(true);
                            previewDialog.addWindowListener(new WindowAdapter() {

                                @Override
                                public void windowClosed(WindowEvent e) {
                                    previewDialog = null;
                                }
                            });
                        }
                    }
                }
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            if (isEnabled()) {
                e.getComponent().requestFocus();
                if (e.isPopupTrigger()) {
                    createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (isEnabled()) {
                if (e.isPopupTrigger()) {
                    createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }

    private class SaveImageAction extends AbstractAction {

        private final static long serialVersionUID = 1L;

        public SaveImageAction() {
            super("Save Image");
        }

        public void actionPerformed(ActionEvent e) {
            saveImage();
        }
    }

    private class LoadImageAction extends AbstractAction {

        private final static long serialVersionUID = 1L;

        public LoadImageAction() {
            super("Load Image");
        }

        public void actionPerformed(ActionEvent e) {
            loadImage();
        }
    }
    private EventHandler eventHandler = new EventHandler();
    private ImageBevelBorder regularBgBorder;
    private ImageBevelBorder armedBgBorder;

    /**
     * Creates a new instance.
     */
    public JImageWell() {
        this(new DefaultImageWellModel());
    }

    /**
     * Constructs a DnDJList with the specified MutableListModel.
     */
    public JImageWell(ImageWellModel m) {
        initComponents();

        dragGestureRecognizer = DragSource.getDefaultDragSource() //.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this.eventHandler);
                .createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this.eventHandler);
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this.eventHandler);

        addKeyListener(eventHandler);
        addFocusListener(eventHandler);
        addMouseListener(eventHandler);
        setFocusable(true);

        setModel(m);

        setTransferHandler(new TransferHandler("image") {
            private final static long serialVersionUID = 1L;

            protected Transferable createTransferable(JComponent c) {
                return exportTransferable();
            }

            public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
                return isImportable(transferFlavors, DnDConstants.ACTION_COPY);
            }

            public boolean importData(JComponent comp, Transferable t) {
                return importTransferable(t, DnDConstants.ACTION_COPY);
            }

            public void exportToClipboard(JComponent comp, Clipboard clip, int action)
                    throws IllegalStateException {

                int clipboardAction = getSourceActions(comp) & action;
                if (clipboardAction == DnDConstants.ACTION_MOVE) {
                    Transferable t = createTransferable(comp);
                    if (t != null) {
                        try {
                            clip.setContents(t, null);
                            setImage(null);
                            exportDone(comp, t, clipboardAction);
                            return;
                        } catch (IllegalStateException ise) {
                            exportDone(comp, t, NONE);
                            throw ise;
                        }
                    }

                    exportDone(comp, null, NONE);
                } else {
                    super.exportToClipboard(comp, clip, action);
                }
            }

            public int getSourceActions(JComponent c) {
                return DnDConstants.ACTION_COPY_OR_MOVE;
            }
        });
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param newValue the text to set
     */
    public void setText(String newValue) {
        String oldValue = text;
        this.text = newValue;
        firePropertyChange(TEXT_PROPERTY, oldValue, newValue);
        repaint();
    }

    public JPopupMenu createPopupMenu() {
        JPopupMenu m = new JPopupMenu();

        boolean hasImage = model.getImage() != null;

        Action a;

        m.add(a = new CutAction());
        a.setEnabled(hasImage);
        m.add(a = new CopyAction());
        a.setEnabled(hasImage);
        m.add(new PasteAction());
        m.add(a = new DeleteAction());
        a.setEnabled(hasImage);

        m.addSeparator();
        m.add(a = new LoadImageAction());
        m.add(a = new SaveImageAction());
        a.setEnabled(hasImage);

        return m;
    }

    private Border getBgBorder() {
        if (isUnderDrag && !isDragSource) {
            if (armedBgBorder == null) {
                armedBgBorder = new ImageBevelBorder(
                        getToolkit().createImage(getClass().getResource("images/ImageWell.armedBorder.png")),
                        new Insets(10, 8, 10, 8));
            }

            return armedBgBorder;
        } else {
            if (regularBgBorder == null) {
                regularBgBorder = new ImageBevelBorder(
                        getToolkit().createImage(getClass().getResource("images/ImageWell.border.png")),
                        new Insets(10, 8, 10, 8));
            }

            return regularBgBorder;
        }

    }

    public ImageWellModel getModel() {
        return model;
    }

    public void setModel(ImageWellModel newValue) {
        ImageWellModel oldValue = model;
        if (model != null) {
            model.removeChangeListener(eventHandler);
        }

        model = newValue;
        if (model != null) {
            model.addChangeListener(eventHandler);
        }
        firePropertyChange("model", oldValue, newValue);
        repaint();
        if (previewDialog != null) {
            previewDialog.updateImage();
        }
    }

    private Image getDragImage() {
        if (model.getImage() != null) {
            Rectangle r = getImageRect();
            return model.getImage().getScaledInstance(r.width, r.height, Image.SCALE_FAST);
        }

        return null;
    }

    private Rectangle getImageRect() {
        Insets insets = getInsets();
        Rectangle r = new Rectangle(
                insets.left + 8, insets.top + 8,
                getWidth() - insets.left - insets.right - 16,
                getHeight() - insets.top - insets.bottom - 16);
        if (model.getImage() != null) {
            Image image = model.getImage();
            int imgWidth = image.getWidth(this);
            int imgHeight = image.getHeight(this);
            if (imgWidth != -1 && imgHeight != -1) {
                float ratio = Math.min(r.width / (float) imgWidth, r.height / (float) imgHeight);
                int scaledWidth, scaledHeight;
                if (ratio > 1) {
                    scaledWidth = imgWidth;
                    scaledHeight
                            = imgHeight;
                } else {
                    scaledWidth = (int) (ratio * imgWidth);
                    scaledHeight
                            = (int) (ratio * imgHeight);
                }

                r.setBounds(
                        r.x + (r.width - scaledWidth) / 2,
                        r.y + (r.height - scaledHeight) / 2,
                        scaledWidth, scaledHeight);
            }

        }
        return r;
    }

    public void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;

        Insets insets = getInsets();
        int width = getWidth();
        int height = getHeight();

        int innerWidth = width - insets.left - insets.right;
        int innerHeight = height - insets.top - insets.bottom;

        if (isDroppable()) {
            getBgBorder().paintBorder(this, g, insets.left, insets.top, innerWidth, innerHeight);
        }

        final Rectangle r = getImageRect();
        final Image image = model.getImage();

        if (image == null) {
            if (text != null && !isBusy()) {
                FontMetrics fm = g.getFontMetrics();
                Rectangle2D sb = fm.getStringBounds(text, g);
                int y = (height + fm.getAscent()) / 2;
                int x = (int) (width - sb.getWidth()) / 2;
                g.setColor(getForeground());
                g.drawString(text, x, y);
            }
        } else {

            if (thumbnailWorker == null //
                    && (thumbnailImage == null || thumbnailSource != image)) {
                setBusy(true);
                thumbnailWorker = new Worker<BufferedImage>() {

                    @Override
                    protected BufferedImage construct() throws Exception {
                        return Images.getScaledInstance(image, r.width, r.height);
                    }

                    @Override
                    protected void done(BufferedImage value) {
                        if (image == model.getImage()) {
                            thumbnailImage = value;
                            thumbnailSource = image;
                        }
                    }

                    @Override
                    protected void finished() {
                        thumbnailWorker = null;
                        setBusy(false);
                        repaint();

                    }
                };
                thumbnailWorker.start();
            } else {
                g.drawImage(thumbnailImage, r.x, r.y, this);
            }
            // g.drawImage(image, r.x,r.y,r.width,r.height,this);
        }

        if (isFocusOwner() && isEnabled()) {
            g.setStroke(new BasicStroke(3));
            g.setColor(new Color(0x94649cd1, true));
            r.setLocation(r.x, r.y);
            r.width--;
            //r.height--;
            g.draw(r);
        }

        if (isBusy()) {
            Icon icon = BusyIcon.getInstance();
            icon.paintIcon(this, g, (width - icon.getIconWidth()) / 2, (height - icon.getIconHeight()) / 2);
        }
    }

    public void removeNotify() {
        if (previewDialog != null) {
            previewDialog.dispose();
            previewDialog = null;
        }
        super.removeNotify();
    }

    private Transferable exportTransferable() {
        if (model != null && model.hasImage()) {
            return new ImageWellTransferable(model);
        }

        return null;
    }

    /**
     * This method sets the permitted source drag action(s) for this DnDJList.
     * <p>
     * This is a bound property.
     *
     * @param actions the permitted source drag action(s). The value is
     * constructed by doing a logical OR of the desired actions specified by the
     * DnDConstants.ACTION_... constants.
     *
     * @see java.awt.dnd.DnDConstants
     */
    public void setPermittedDragActions(int actions) {
        int oldValue = dragGestureRecognizer.getSourceActions();
        dragGestureRecognizer.setSourceActions(actions);
        firePropertyChange("permittedDragActions", oldValue, actions);
    }

    /**
     * This method returns an int representing the type of drag action(s) this
     * DnDJList supports.
     *
     * @return the currently permitted drag action(s) The value is constructed
     * by doing a logical OR of the desired actions specified by the
     * DnDConstants.ACTION_... constants.
     *
     * @see java.awt.dnd.DnDConstants
     */
    public int getPermittedDragActions() {
        return dragGestureRecognizer.getSourceActions();
    }

    /**
     * This method sets the permitted target drop action(s) for this DnDJList.
     *
     * @param actions the permitted target drop action(s). The value is
     * constructed by doing a logical OR of the desired actions specified by the
     * DnDConstants.ACTION_... constants.
     *
     * @see java.awt.dnd.DnDConstants
     */
    public void setPermittedDropActions(int actions) {
        int oldValue = getDropTarget().getDefaultActions();
        getDropTarget().setDefaultActions(actions);
        firePropertyChange("permittedDropActions", oldValue, actions);
    }

    /**
     * This method returns an int representing the type of drop action(s) this
     * DnDJList supports.
     *
     * @return the currently permitted drop action(s) The value is constructed
     * by doing a logical OR of the desired actions specified by the
     * DnDConstants.ACTION_... constants.
     *
     * @see java.awt.dnd.DnDConstants
     */
    public int getPermittedDropActions() {
        return getDropTarget().getDefaultActions();
    }

    public boolean isDroppable() {
        return getPermittedDropActions() != DnDConstants.ACTION_NONE;
    }

    protected boolean isImportable(DataFlavor[] flavors, int dropAction) {
        for (int i = 0; i
                < flavors.length; i++) {
            if (flavors[i].equals(DataFlavor.imageFlavor)
                    || flavors[i].equals(DataFlavor.javaFileListFlavor)
                    || flavors[i].getMimeType().startsWith("image")) {
                return true;
            }

        }
        return false;
    }

    protected boolean importTransferable(Transferable transferable, int dropAction) {
        try {

            // Handle file list
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                @SuppressWarnings("unchecked")
                List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                if (fileList.size() != 1) {
                    return false;
                }

                final File file = fileList.get(0);
                setBusy(true);
                new Worker<byte[]>() {

                    private BufferedImage image;

                    @Override
                    public byte[] construct() throws IOException {
                        byte[] data = readFile(file);
                        image = ImageIO.read(new ByteArrayInputStream(data));
                        return data;
                    }

                    @Override
                    public void done(byte[] value) {
                        model.setImage(value, image);
                    }

                    @Override
                    protected void finished() {
                        setBusy(false);
                    }
                }.start();
                return true;
            }

// Handle input stream with image data
            DataFlavor[] flavors = transferable.getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {

                if (flavors[i].getMimeType().startsWith("image")) {
                    if (InputStream.class.isAssignableFrom(flavors[i].getRepresentationClass())
                            && !flavors[i].getMimeType().startsWith("image/x-pict")) {
                        final InputStream in = (InputStream) transferable.getTransferData(flavors[i]);
                        try {
                            model.setBinaryImage(readStream(in));
                        } finally {
                            try {
                                in.close();
                            } catch (IOException e) {
                            }
                        }

                        return true;
                    }
                }
            }

// Handle image flavor
// Note: We do this last, because we prefer input streams with
// image data over image objects. Apple's JVM returns images, that
// don't work. Therefore we convert them into byte data.
            if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                final Image img = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
                model.setBinaryImage(readImage(img));
                return true;
            }

        } catch (UnsupportedFlavorException e) {
            // The transferable didn't keep its promise. This shouldn't be happening.
            e.printStackTrace();
        } catch (IOException e) {
            // IOException when getting transfer data. This can happen.
            e.printStackTrace();
        } catch (Throwable e) {
            // IOException when getting transfer data. This can happen.
            e.printStackTrace();
        }

        return false;
    }

    private boolean isBusy() {
        return isBusy != 0;
    }

    private void setBusy(boolean newValue) {
        boolean oldValue = isBusy != 0;
        isBusy = newValue ? isBusy + 1 : isBusy - 1;
        firePropertyChange(BUSY_PROPERTY, oldValue, newValue);
        setCursor(Cursor.getPredefinedCursor(newValue ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
        repaint();
    }

    protected byte[] readFile(File f) throws IOException {
        byte[] data = new byte[(int) f.length()];
        DataInputStream in = null;
        try {
            in = new DataInputStream(new FileInputStream(f));
            in.readFully(data);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return data;
    }

    protected byte[] readStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[512];
        int count;
        while (-1 != (count = in.read(buf))) {
            out.write(buf, 0, count);
        }

        return out.toByteArray();
    }

    protected byte[] readImage(Image img) throws IOException {
        Image buf = createImage(img.getWidth(this), img.getHeight(this));
        Graphics g = buf.getGraphics();
        g.drawImage(img, 0, 0, this);
        RenderedImage renderedImage = Images.toBufferedImage(buf);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] result;
        try {
            ImageIO.write(renderedImage, "png", out);
            result
                    = out.toByteArray();
        } catch (IOException e) {
            result = null;
        } finally {
            g.dispose();
            buf.flush();
        }

        return result;
    }

    public void copy() {
        getToolkit().getSystemClipboard().setContents(new ImageWellTransferable(model), eventHandler);
    }

    public void cut() {
        getToolkit().getSystemClipboard().setContents(new ImageWellTransferable(model), eventHandler);
        model.setBinaryImage(null);
    }

    @Override
    public void delete() {
        model.setBinaryImage(null);
    }

    public void paste() {
        importTransferable(getToolkit().getSystemClipboard().getContents(this), DnDConstants.ACTION_COPY);
    }

    public void saveImage() {
        // Determine image format
        String formatName = null;
        ImageInputStream in = null;
        try {
            byte[] binary = model.getBinaryImage();
            if (binary != null) {

                in = ImageIO.createImageInputStream(new ByteArrayInputStream(model.getBinaryImage()));
                for (Iterator<ImageReader> i = ImageIO.getImageReaders(in); i.hasNext();) {
                    ImageReader r = i.next();
                    formatName = r.getFormatName();
                    break;
                }
            } else {
                formatName = "PNG";
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }

        if (formatName != null) {
            if (fileChooser == null) {
                fileChooser = new JFileChooser();
            }

            fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), "image." + formatName));
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                final File file = fileChooser.getSelectedFile();
                new BackgroundTask() {

                    @Override
                    public void construct() throws IOException {
                        if (model.getBinaryImage() == null) {
                            ImageIO.write(Images.toBufferedImage(model.getImage()), "PNG", file);
                        } else {
                            OutputStream out = null;
                            try {
                                out = new FileOutputStream(file);
                                out.write(model.getBinaryImage());
                            } finally {
                                if (out != null) {
                                    out.close();
                                }
                            }
                        }
                    }

                    @Override
                    public void failed(Throwable value) {
                        JOptionPane.showMessageDialog(JImageWell.this,
                                "<html>Couldn't save file " + file.getName() + "<br>" + value);

                    }
                }.start();
            }

        }
    }

    public void loadImage() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
        }

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();
            new Worker<byte[]>() {

                @Override
                public byte[] construct() throws IOException {
                    return readFile(file);
                }

                @Override
                public void done(byte[] value) {
                    model.setBinaryImage(value);
                }

                @Override
                public void failed(Throwable value) {
                    JOptionPane.showMessageDialog(JImageWell.this,
                            "<html>Couldn't load file " + file.getName() + "<br>" + value);
                }
            }.start();
        }

    }

    /**
     * Convenience method for setting an image in the model. Note that "image"
     * is not a bound property. This method is used by the TransferHandler of
     * JImageWell.
     *
     * @param newValue
     */
    public void setImage(Image newValue) {
        model.setImage(newValue);
    }

    /**
     * Convenience method for getting an image from the model. Note that "image"
     * is not a bound property. This method is used by the TransferHandler of
     * JImageWell.
     */
    public Image getImage() {
        return model.getImage();
    }

    @Override
    public void duplicate() {
        //   throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void selectAll() {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    private class PreviewDialog extends javax.swing.JDialog {
    private final static long serialVersionUID = 1L;

        private JLabel label;

        public PreviewDialog(Frame frame) {
            super(frame);
            init();
        }

        public PreviewDialog(Dialog dialog) {
            super(dialog);
            init();
        }

        private void init() {
            label = new JLabel();
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(label);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setTransferHandler(getTransferHandler());
            scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
            getContentPane().add(scrollPane);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            label.addMouseListener(eventHandler);
            updateImage();
        }

        private void updateImage() {
            Image image = getImage();
            if (image == null) {
                label.setIcon(null);
                setTitle("No Image");
            } else {
                label.setIcon(new ImageIcon(image));
                setTitle(image.getWidth(JImageWell.this) + " x " + image.getHeight(JImageWell.this));
            }
        }
    }

    public void repaintThumbnail() {
        thumbnailImage = null;
        repaint();
    }

    public void clearSelection() {
        // nothing to do
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
