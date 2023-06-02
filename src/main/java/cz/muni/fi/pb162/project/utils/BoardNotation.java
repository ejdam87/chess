package cz.muni.fi.pb162.project.utils;

import cz.muni.fi.pb162.project.Coordinates;

/**
 * Utility class which represents board notation of coordinates.
 *
 * @author Adam Dzadon
 */
public final class BoardNotation {
    private static final char ASCII_OF_A = 97;

    private BoardNotation() {
    }

    /**
     * Static method to convert coordinates (x, y) to letter notation
     *
     * @param x index of row
     * @param y index of column
     * @return converted notation
     */
    public static String getNotationOfCoordinates(int x, int y) {
        int newX = ASCII_OF_A + x;
        int newY = y + 1;
        return (char) newX + String.valueOf(newY);
    }

    /**
     * Static method to convert letter notation back to numerical coordinates
     * (Inverse to previous method)
     *
     * @param x letter notation of row
     * @param y index of column
     * @return coordinates object consisting of numerical representation of position
     */
    public static Coordinates getCoordinatesOfNotation(char x, int y) {
        int newX = x - ASCII_OF_A;
        int newY = y - 1;
        return new Coordinates(newX, newY);
    }
}
