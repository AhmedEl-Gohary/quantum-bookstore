package model;

/**
 * Showcase book that is not for sale
 */
public class ShowcaseBook extends Book {
    public ShowcaseBook(String isbn, String title, int year, double price) {
        super(isbn, title, year, price);
    }

    @Override
    public String toString() {
        return super.toString() + " (Showcase - Not for sale)";
    }
}