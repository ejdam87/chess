package cz.muni.fi.pb162.project.exceptions;

/**
 * A checked exception which is thrown when illegal move is wanted
 *
 * @author Adam Dzadon
 */
public class NotAllowedMoveException extends Exception {

    /**
     * Creates new NotAllowedMoveException
     */
    public NotAllowedMoveException() {
        super();
    }

    /**
     * Creates new NotAllowedMoveException
     *
     * @param msg message to be shown
     */
    public NotAllowedMoveException(String msg) {
        super(msg);
    }

    /**
     * Creates new instance of NotAllowedMoveException
     *
     * @param msg   message to be shown
     * @param cause causal exception which caused this exception
     */
    public NotAllowedMoveException(String msg, Exception cause) {
        super(msg, cause);
    }
}
