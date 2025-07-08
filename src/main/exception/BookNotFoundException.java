package exception;

/**
 * Exception thrown when a book is not found in the inventory
 */
public class BookNotFoundException extends Exception {
    public BookNotFoundException(String isbn) {
        super("Book Not Found: Book with ISBN: " + isbn + " is not found.");
    }
}
