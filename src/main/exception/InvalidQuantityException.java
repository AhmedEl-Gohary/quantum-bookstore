package exception;

/**
 * Exception thrown when a quantity is non-positive
 */
public class InvalidQuantityException extends Exception {
    public InvalidQuantityException(int quantity) {
        super("Invalid purchase quantity: " + quantity + ". Quantity must be positive.");
    }
}