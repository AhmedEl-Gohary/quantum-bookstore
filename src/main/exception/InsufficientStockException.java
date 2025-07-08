package exception;

/**
 * Exception thrown when there's insufficient stock for a purchase
 */
public class InsufficientStockException extends Exception {
    public InsufficientStockException(String title, int available, int requested) {
        super("Insufficient Stock:" + requested + " copies were requested from the book titled: " + title +
                " but only " + available + " copies are available.");
    }
}
