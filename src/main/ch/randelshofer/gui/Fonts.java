/*
 * @(#)Fonts.java  1.1.1  2007-06-07
 * Copyright (c) 2002 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui;

import java.util.*;
import java.awt.*;
import javax.swing.*;
/**
 * Fonts.
 *
 * @author Werner Randelshofer
 * @version 1.1.1 2007-06-07 Fixed small dialog font tag. 
 * <br>1.1 2004-04-04 Changed font settings to provide better fonts on
 * Mac OS X and on Windows.
 * <br>1.0 March 19, 2003 Created.
 */
public class Fonts {
    private static HashMap<String,Object> fonts;
    
    /** Creates a new instance. */
    private Fonts() {
    }
    
    private static void init() {
        if (fonts == null) {
            fonts = new HashMap<String,Object>();
            Font dialogFont = UIManager.getFont("Label.font");
            
            Font emphasizedDialogFont = dialogFont.deriveFont(Font.BOLD);
            Font smallDialogFont;
            if (dialogFont.getSize() >= 13) {
                smallDialogFont = dialogFont.deriveFont((float) (dialogFont.getSize() - 2));
            } else {
                smallDialogFont = dialogFont;
            }
            Font emphasizedSmallDialogFont = smallDialogFont.deriveFont(Font.BOLD);
            
            
            fonts.put("Dialog", dialogFont);
            fonts.put("EmphasizedDialog", emphasizedDialogFont);
            fonts.put("SmallDialog", smallDialogFont);
            fonts.put("EmphasizedSmallDialog", emphasizedSmallDialogFont);
            fonts.put("Application", dialogFont.deriveFont(Font.PLAIN));
            fonts.put("Label", dialogFont.deriveFont(10f));
            fonts.put("MiniDialog", dialogFont.deriveFont(9f));
            fonts.put("Monospace", new Font("Courier", Font.PLAIN, dialogFont.getSize()));
            
            
            if (System.getProperty("java.version").startsWith("1.4")) {
                fonts.put("DialogTag",  "");
                fonts.put("/DialogTag",  "");
                fonts.put("SmallDialogTag",  "<font size=\"-1\">");
                fonts.put("/SmallDialogTag",  "</font>");
                fonts.put("EmphasizedDialogTag",  "<b>");
                fonts.put("/EmphasizedDialogTag",  "</b>");
            } else {
                fonts.put("DialogTag",  "<font face=\""+dialogFont.getName()+"\">");
                fonts.put("/DialogTag",  "</font>");
                fonts.put("SmallDialogTag",  "<font face=\""+dialogFont.getName()+"\" size=\"-2\">");
                fonts.put("/SmallDialogTag",  "</font>");
                fonts.put("EmphasizedDialogTag",  "<font face='"+dialogFont.getName()+"'><b>");
                fonts.put("/EmphasizedDialogTag",  "</b></font>");
            }
        }
    }
    
    /**
     * The dialog font is used for text in menus, modeless dialogs, and titles
     * of document windows.
     */
    public static Font getDialogFont() {
        init();
        return (Font) fonts.get("Dialog");
    }
    /**
     * Use emphasized dialog fonts sparingly. Emphasized (bold) dialog font is
     * used in only two places in the interface: the application name in an
     * About window and the message text in an option pane.
     */
    public static Font getEmphasizedDialogFont() {
        init();
        return (Font) fonts.get("EmphasizedDialog");
    }
    /**
     * The small dialog font is used for informative text in alerts.
     * It is also the default font for headings in lists, for help tags, and for
     * text in the small versions of many controls. You can also use it to
     * provide additional information about settings in various windows.
     */
    public static Font getSmallDialogFont() {
        init();
        return (Font) fonts.get("SmallDialog");
    }
    /**
     * You might use emphasized small dialog font to title a group of settings
     * that appear without a group box, or for brief informative text below a
     * text field.
     */
    public static Font getEmphasizedSmallDialogFont() {
        init();
        return (Font) fonts.get("EmphasizedSmallDialog");
    }
    /**
     * If your application creates text documents, use the application font as
     * the default for user-created content.
     */
    public static Font getApplicationFont() {
        init();
        return (Font) fonts.get("Application");
    }
    /**
     * If your application needs monospaced fonts, use the monospace font.
     */
    public static Font getMonospaceFont() {
        init();
        return (Font) fonts.get("Monospace");
    }
    /**
     * The label font is used for labels with controls such as sliders and icon
     * bevel buttons. You should rarely need to use this font in dialogs, but
     * may find it useful in utility windows when space is at a premium.
     */
    public static Font getLabelFont() {
        init();
        return (Font) fonts.get("Label");
    }
    /**
     * If necessary, the mini dialog font can be used for utility window labels
     * and text.
     */
    public static Font getMiniDialogFont() {
        init();
        return (Font) fonts.get("MiniDialog");
    }
    
    /**
     * Puts an HTML font tag for the Dialog Font around the specified text.
     */
    public static String dialogFontTag(String text) {
        init();
        return fonts.get("DialogTag") + text + fonts.get("/DialogTag");
    }
    /**
     * Puts an HTML font tag for the Small Dialog Font around the specified text.
     */
    public static String smallDialogFontTag(String text) {
        init();
        return fonts.get("SmallDialogTag") + text + fonts.get("/SmallDialogTag");
    }
    /**
     * Puts an HTML font tag for the Emphasized Dialog Font around the specified text.
     */
    public static String emphasizedDialogFontTag(String text) {
        init();
        return fonts.get("EmphasizedDialogTag") + text + fonts.get("/EmphasizedDialogTag");
    }
}
