package exception;

/**
 * Exception thrown when an invalid book type is provided
 */
public class InvalidBookTypeException extends Exception {
    public InvalidBookTypeException(String type) {
        super("Invalid Book Type: " + type);
    }
}
