package exception;

/**
 * Exception thrown when trying to add an already existing book
 */
public class DuplicateBookException extends Exception {
    public DuplicateBookException(String isbn) {
        super("Duplicate ISBN: A book with ISBN " + isbn + " already exists.");
    }
}