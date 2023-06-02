package cz.muni.fi.pb162.project;


import cz.muni.fi.pb162.project.utils.BoardNotation;

/**
 * Record to represent coordinates on chess board
 *
 * @param letterNumber - index of row
 * @param number       - index of column
 * @author Adam Dzadon
 */
public record Coordinates(int letterNumber, int number) implements Comparable<Coordinates> {

    /**
     * Method to calculate average of two coordinates
     *
     * @return arithmetical average of the coordinates
     */
    public double averageOfCoordinates() {
        return ((double) (this.letterNumber() + this.number())) / 2;
    }

    /**
     * Method to add two sets of coordinates
     *
     * @param other - other set of coordinates
     * @return new coordinates consisting of sums of respectful coordinates
     */
    public Coordinates add(Coordinates other) {
        int newLn = this.letterNumber() + other.letterNumber();
        int newN = this.number() + other.number();
        return new Coordinates(newLn, newN);
    }

    /**
     * @return String representation of Coordinates in format <x><y>
     */
    @Override
    public String toString() {
        return BoardNotation.getNotationOfCoordinates(this.letterNumber(), this.number());
    }

    @Override
    public int compareTo(Coordinates o) {

        if (this.letterNumber == o.letterNumber) {
            return this.number - o.number;
        }
        return this.letterNumber - o.letterNumber;
    }
}
