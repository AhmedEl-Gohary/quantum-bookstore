package exception;

/**
 * Exception thrown when trying to purchase a non-purchasable book
 */
public class BookNotPurchasableException extends Exception {
    public BookNotPurchasableException(String title) {
        super("Not For Sale: The book titled: " + title + " is not for sale.");
    }
}
