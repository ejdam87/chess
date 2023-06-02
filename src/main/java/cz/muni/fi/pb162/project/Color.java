package cz.muni.fi.pb162.project;

/**
 * Enum representing color of individual pieces on chess board
 *
 * @author Adam Dzadon
 */
public enum Color {
    WHITE,
    BLACK;

    /**
     * Method to get opposite color to actual color
     *
     * @return an opposite color
     */
    public Color getOppositeColor() {
        if (this == WHITE) {
            return BLACK;
        }
        return WHITE;
    }
}
