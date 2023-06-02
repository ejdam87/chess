package cz.muni.fi.pb162.project;

import cz.muni.fi.pb162.project.utils.BoardNotation;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Class which represents board of game
 *
 * @author Adam Dzadon
 */
public class Board implements Originator<Memento> {

    public static final int SIZE = 8;
    private Piece[][] squares = new Piece[SIZE][SIZE];
    private int round;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    /**
     * Method to decide whether given coordinate is inside the square
     *
     * @param coord - given coordinate
     * @return if given coordinate is inbound
     */
    private static boolean coordInRange(int coord) {
        return 0 <= coord && coord < SIZE;
    }

    /**
     * Method to decide whether given coordinates (given as two integers) are inside the board
     *
     * @param row - index of row
     * @param col - index of column
     * @return if given coordinates are inbound
     */
    public static boolean inRange(int row, int col) {
        return coordInRange(row) && coordInRange(col);
    }

    /**
     * Method to decide whether given coordinates (given as Coordinates object) are inside the board
     *
     * @param coords - given coordinates
     * @return if given coordinates are inbound
     */
    public static boolean inRange(Coordinates coords) {
        int row = coords.letterNumber();
        int col = coords.number();
        return inRange(row, col);
    }

    /**
     * Method to decide whether there is any piece on given coordinates
     *
     * @param row - index of row
     * @param col - index of column
     * @return if there is no piece on given coordinates
     */
    public boolean isEmpty(int row, int col) {
        return !inRange(row, col) || squares[row][col] == null;
    }

    /**
     * Method to get piece on given coordinates
     *
     * @param row - index of row
     * @param col - index of column
     * @return piece on given coordinates or null if coordinates are not in range or empty position
     */
    public Piece getPiece(int row, int col) {
        if (inRange(row, col)) {
            return squares[row][col];
        }
        return null;
    }

    /**
     * Method to get piece on given coordinates
     *
     * @param coords - Coordinates object representing coordinates of possible piece
     * @return piece on given coordinates or null if coordinates are not in range or empty position
     */
    public Piece getPiece(Coordinates coords) {
        return getPiece(coords.letterNumber(), coords.number());
    }

    /**
     * Method to put given piece on given coordinates
     *
     * @param row   - index of row
     * @param col   - index of column
     * @param piece - given piece
     */
    public void putPieceOnBoard(int row, int col, Piece piece) {
        if (!inRange(row, col)) {
            return;
        }
        squares[row][col] = piece;
    }

    /**
     * Method to find Coordinates of piece by given id
     *
     * @param id - id of desired piece
     * @return Coordinates of piece with given id or null if there is no such piece
     */
    public Coordinates findCoordinatesOfPieceById(long id) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Piece piece = this.getPiece(i, j);
                if (piece != null && piece.getId() == (id)) {
                    return new Coordinates(i, j);
                }
            }
        }

        return null;

    }

    /**
     * Method to get all pieces currently on board
     *
     * @return all pieces currently on board
     */
    public Piece[] getAllPiecesFromBoard() {
        return Arrays.stream(squares)
                .flatMap(Stream::of)
                .filter(Objects::nonNull)
                .toArray(Piece[]::new);
    }

    /**
     * Method to append initial numbering to given builder
     *
     * @param builder - StringBuilder to be modified
     */
    private void numbering(StringBuilder builder) {
        for (int i = 1; i <= SIZE; i++) {
            builder.append(' ');
            builder.append(' ');
            builder.append(i);
            builder.append(' ');
        }
        builder.append(System.lineSeparator());
    }

    /**
     * Method to append string representation of row with given index to given builder
     *
     * @param rowIndex - index of row to be stringified
     * @param builder  - StringBuilder to be modified
     */
    private void rowToString(int rowIndex, StringBuilder builder) {
        char letter = BoardNotation.getNotationOfCoordinates(rowIndex, 0).charAt(0);
        letter = Character.toUpperCase(letter);
        builder.append(letter);
        builder.append(' ');
        builder.append('|');

        for (int colIndex = 0; colIndex < SIZE; colIndex++) {
            builder.append(' ');

            Piece piece = squares[rowIndex][colIndex];
            if (piece == null) {
                builder.append(' ');
            } else {
                builder.append(piece);
            }

            builder.append(' ');
            builder.append('|');
        }
        builder.append(System.lineSeparator());
    }

    /**
     * Method to append horizontal line to given StringBuilder
     *
     * @param builder - StringBuilder to be modified
     */
    private void horizontalSeparator(StringBuilder builder) {
        builder.append("  ");
        builder.append("----".repeat(SIZE));
        builder.append(System.lineSeparator());
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("  ");
        numbering(res);
        horizontalSeparator(res);

        for (int row = 0; row < SIZE; row++) {
            rowToString(row, res);
            horizontalSeparator(res);
        }

        return res.toString().stripTrailing();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Board other)) {
            return false;
        }
        return this.round == other.round && Arrays.deepEquals(this.squares, other.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares) * 13 + round * 23;
    }

    /**
     * Method to create a deep copy of array of the board
     *
     * @return a deep copy of array of the board
     */
    private Piece[][] boardDeepCopy() {
        Piece[][] res = new Piece[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {

                Piece current = squares[i][j];
                if (current != null) {
                    res[i][j] = new Piece(current);
                } else {
                    res[i][j] = null;
                }

            }
        }
        return res;
    }

    @Override
    public Memento save() {
        return new Memento(round, boardDeepCopy());
    }

    @Override
    public void restore(Memento save) {
        this.squares = save.board();
        this.round = save.round();
    }
}
