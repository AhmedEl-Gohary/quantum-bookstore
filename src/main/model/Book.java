package model;

/**
 * Base class for all books in the system
 */
public abstract class Book {
    private final String isbn;
    private final String title;
    private final int year;
    private final double price;

    public Book(String isbn, String title, int year, double price) {
        this.isbn = isbn;
        this.title = title;
        this.year = year;
        this.price = price;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public int getYear() { return year; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return String.format("ISBN: %s, Title: %s, Year: %d, Price: $%.2f",
                isbn, title, year, price);
    }
}
