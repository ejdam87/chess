package cz.muni.fi.pb162.project.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Utility class to load images
 *
 * @author Adam Dzadon
 */

public class IconLoader {

    /**
     * Static method to load icon from given path
     *
     * @param path location of image
     * @return resulting icon
     */
    public static ImageIcon loadImage(String path) {

        ImageIcon res = null;
        try {
            Image im = ImageIO.read(new File(path));
            im = im.getScaledInstance(50, 70, java.awt.Image.SCALE_SMOOTH);
            res = new ImageIcon(im);

        } catch (IOException ex) {
            System.out.println("Error");
        }

        return res;

    }
}
