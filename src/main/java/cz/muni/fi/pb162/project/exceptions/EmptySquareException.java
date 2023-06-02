package cz.muni.fi.pb162.project.exceptions;

/**
 * A checked exception which is thrown when move from empty square is wanted
 *
 * @author Adam Dzadon
 */
public class EmptySquareException extends Exception {

    /**
     * Creates new instance of EmptySquareException
     */
    public EmptySquareException() {
        super();
    }

    /**
     * Creates new instance of EmptySquareException
     *
     * @param msg message to be shown
     */
    public EmptySquareException(String msg) {
        super(msg);
    }

    /**
     * Creates new instance of EmptySquareException
     *
     * @param msg   message to be shown
     * @param cause causal exception which caused this exception
     */
    public EmptySquareException(String msg, Exception cause) {
        super(msg, cause);
    }
}
