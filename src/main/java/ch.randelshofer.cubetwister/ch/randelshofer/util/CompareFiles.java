/*
 * @(#)CompareFiles.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
/**
 * Compares two files of equal size and content.
 *
 * @author Werner Randelshofer
 */
public class CompareFiles extends Object {

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        File file1;
        File file2;

        FileDialog fd = new FileDialog(new Frame(), "CompareFiles: Select file 1");

        fd.setVisible(true);
        if (fd.getFile() == null) {
            System.exit(0);
        }
        file1 = new File(fd.getDirectory()+fd.getFile());

        fd.setTitle("CompareFiles: Select file 2");
        fd.setVisible(true);
        if (fd.getFile() == null) {
            System.exit(0);
        }
        file2 = new File(fd.getDirectory()+fd.getFile());

        long length1, length2;

        length1 = file1.length();
        length2 = file2.length();

        System.out.println(file1+"\n  length: "+length1);
        System.out.println(file2+"\n  length: "+length2);

        if (length1 != length2) {
            System.out.println("Different lengths.");
            return;
        }

        byte[] bytes1 = new byte[(int) length1];
        byte[] bytes2 = new byte[(int) length2];

        try {
            System.out.println("reading "+file1+"...");
            DataInputStream in = new DataInputStream(new FileInputStream(file1));
            in.readFully(bytes1);
            in.close();
            System.out.println("done.");
            System.out.println("reading "+file2+"...");
            in = new DataInputStream(new FileInputStream(file2));
            in.readFully(bytes2);
            in.close();
            System.out.println("done.");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (Arrays.equals(bytes1, bytes2)) {
            System.out.println("The files are identical.");
        } else {
            int i;
            for (i=0; bytes1[i] == bytes2[i]; i++) {}
            System.out.println("The files are different @ "+i);
        }

    }
}
