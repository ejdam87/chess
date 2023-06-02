package cz.muni.fi.pb162.project.exceptions;

/**
 * An unchecked exception which is thrown when want to start a game without player
 *
 * @author Adam Dzadon
 */
public class MissingPlayerException extends RuntimeException {

    /**
     * Creates new instance of MissingPlayerException
     */
    public MissingPlayerException() {
        super();
    }

    /**
     * Creates new instance of MissingPlayerException
     *
     * @param msg message to be shown
     */
    public MissingPlayerException(String msg) {
        super(msg);
    }

    /**
     * Creates new instance of MissingPlayerException
     *
     * @param msg   message to be shown
     * @param cause causal exception which caused this exception
     */
    public MissingPlayerException(String msg, Exception cause) {
        super(msg, cause);
    }
}
