/*
 * @(#)Files.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.util;

import org.jhotdraw.annotation.Nonnull;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * Files.
 *
 * @author  Werner Randelshofer
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
     * @throws IOException when an I/O error occurs.
     */
    public static void copyDirectoryTree(@Nonnull File srcdir, @Nonnull File tgtdir)
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
     *               decide whether it should be copied or not.
     * @throws IOException when an I/O error occurs.
     */
    public static void copyDirectoryTree(@Nonnull File srcdir, @Nonnull File tgtdir, @Nonnull FileFilter filter)
            throws IOException {
        if (!tgtdir.exists()) {
            tgtdir.mkdirs();
        }

        File[] list = srcdir.listFiles();
        if (list == null) {
            throw new IOException("Source directory does not exist: " + srcdir);
        }

        for (int i = 0; i < list.length; i++) {
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
    public static void copyFile(@Nonnull File source, @Nonnull File target)
            throws IOException {
        FileOutputStream out = null;
        FileInputStream in = null;

        if (!target.getParentFile().exists()) {
            target.getParentFile().mkdirs();
        }

        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            copyStream(in, out);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }

    /**
     * Copies to contents of the input stream to the output stream.
     */
    public static void copyStream(@Nonnull InputStream in, @Nonnull OutputStream out)
            throws IOException {
        byte[] buf = new byte[512];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }
}
