package cz.muni.fi.pb162.project;

import cz.muni.fi.pb162.project.exceptions.EmptySquareException;
import cz.muni.fi.pb162.project.exceptions.NotAllowedMoveException;
import cz.muni.fi.pb162.project.exceptions.InvalidFormatOfInputException;
import cz.muni.fi.pb162.project.strategies.MoveStrategy;

/**
 * Interface providing methods for playable game
 *
 * @author Adam Dzadon
 */
public interface Playable extends Caretaker {

    /**
     * Sets board to initial state ( e.g. in chess it puts all the pieces on it's starting positions )
     */
    void setInitialSet();

    /**
     * Moves the piece from first coordinates to second coordinates.
     * Method also provides "promotion" ( e.g. swapping pawn with queen if pawn reached last square on board )
     * If coords1 is empty position or positions are not valid, method does nothing.
     *
     * @param coords1 - coordinates of piece to move
     * @param coords2 - coordinates where to move
     */
    void move(Coordinates coords1, Coordinates coords2);

    /**
     * Aim of this method is to demonstrate game. In each round, it finds out which player is next,
     * gets input from the player (from standard input), increases the round by one, and makes a move.
     * Also, the state of the game is updated (see the updateStatus below).
     *
     * @throws EmptySquareException          when trying to move empty square of out-of-bounds square
     * @throws NotAllowedMoveException       when trying to move to invalid square
     * @throws InvalidFormatOfInputException when invalid console format is provided
     */
    void playConsole(MoveStrategy s1, MoveStrategy s2) throws EmptySquareException, NotAllowedMoveException;
}
