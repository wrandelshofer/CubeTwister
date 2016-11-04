/*
 * @(#)Files.java  1.1  2007-09-13
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.util;

import java.io.*;
/**
 * Files.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2007-09-13 Added method stream. 
 * <br>1.0 13. Januar 2004  Created.
 */
public class Files {
    
    /** Prevent instance creation. */
    private Files() {
    }
    
    /**
     * Recursively copies all files from the specified source directory
     * to the target directory.
     *
     * @param srcdir The source directory.
     * @param tgtdir The target directory.
     * @exception IOException when an I/O error occurs.
     */
    public static void copyDirectoryTree(File srcdir, File tgtdir)
    throws IOException {
        copyDirectoryTree(srcdir, tgtdir, new FileFilter() {
            public boolean accept(File filename) {
                return true;
            }
        }
        );
    }
    /**
     * Recursively copies all files from the specified source directory
     * to the target directory.
     *
     * @param srcdir The source directory.
     * @param tgtdir The target directory.
     * @param filter A filter which is used on the files of the srcdir to
     * decide whether it should be copied or not.
     * @exception IOException when an I/O error occurs.
     */
    public static void copyDirectoryTree(File srcdir, File tgtdir, FileFilter filter)
    throws IOException {
        if (! tgtdir.exists()) tgtdir.mkdirs();
        
        File[] list = srcdir.listFiles();
        if (list == null) throw new IOException("Source directory does not exist: "+srcdir);
        
        for (int i=0; i < list.length; i++) {
            File source = list[i];
            
            if (filter.accept(source)) {
                File target = new File(tgtdir, source.getName());
                
                if (source.isDirectory()) {
                    copyDirectoryTree(source, target, filter);
                } else {
                    copyFile(source, target);
                }
            }
        }
    }
    /**
     * Copies to contents of the source file to the target file.
     * This method does not honor file attributes.
     */
    public static void copyFile(File source, File target)
    throws IOException {
        FileOutputStream out = null;
        FileInputStream in = null;
        
        if (! target.getParentFile().exists()) {
            target.getParentFile().mkdirs();
        }
        
        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            copyStream(in, out);
        } finally {
            try {
                if (in != null) in.close();
            } finally {
                if (out != null) out.close();
            }
        }
    }
    /**
     * Copies to contents of the input stream to the output stream.
     */
    public static void copyStream(InputStream in, OutputStream out)
    throws IOException {
        byte[] buf = new byte[512];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }
}
