package cz.muni.fi.pb162.project.exceptions;

/**
 * An unchecked exception which is thrown when user provides invalid input
 *
 * @author Adam Dzadon
 */
public class InvalidFormatOfInputException extends RuntimeException {

    /**
     * Creates new instance of InvalidFormatOfInputException
     */
    public InvalidFormatOfInputException() {
        super();
    }

    /**
     * Creates new instance of InvalidFormatOfInputException
     *
     * @param msg message to be shown
     */
    public InvalidFormatOfInputException(String msg) {
        super(msg);
    }

    /**
     * Creates new instance of InvalidFormatOfInputException
     *
     * @param msg   message to be shown
     * @param cause causal exception which caused this exception
     */
    public InvalidFormatOfInputException(String msg, Exception cause) {
        super(msg, cause);
    }
}
