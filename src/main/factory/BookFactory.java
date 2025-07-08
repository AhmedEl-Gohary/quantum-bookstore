package factory;

import exception.InvalidBookTypeException;
import model.*;

/**
 * Factory class for creating different types of books
 */
public class BookFactory {

    /**
     * Creates a book of the specified type
     * @param type the type of book to create
     * @param isbn the book's ISBN
     * @param title the book's title
     * @param year the publication year
     * @param price the book's price
     * @param additionalParams additional parameters specific to book type
     * @return a Book instance of the specified type
     * @throws InvalidBookTypeException if the book type is not recognized
     */
    public static Book createBook(String type, String isbn, String title,
                                  int year, double price, Object... additionalParams)
            throws InvalidBookTypeException {
        switch (type.toLowerCase()) {
            case "paper":
                int stock = additionalParams.length > 0 ? (Integer) additionalParams[0] : 0;
                return new PaperBook(isbn, title, year, price, stock);
            case "ebook":
                String filetype = additionalParams.length > 0 ? (String) additionalParams[0] : "pdf";
                return new EBook(isbn, title, year, price, filetype);
            case "showcase":
                return new ShowcaseBook(isbn, title, year, price);
            default:
                throw new InvalidBookTypeException(type);
        }
    }
}