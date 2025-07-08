package service;

import model.*;
import interfaces.*;
import factory.BookFactory;
import dto.PurchaseResult;
import exception.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main service class for the Quantum Bookstore
 */
public class QuantumBookstore {
    private final Map<String, Book> inventory;
    private final ShippingService shippingService;
    private final MailService mailService;

    public QuantumBookstore() {
        this.inventory = new HashMap<>();
        this.shippingService = new ShippingService();
        this.mailService = new MailService();
    }

    /**
     * Adds a book to the inventory
     * @param type the type of book to add
     * @param isbn the book's ISBN
     * @param title the book's title
     * @param author the book's author
     * @param year the publication year
     * @param price the book's price
     * @param additionalParams additional parameters specific to book type
     */
    public void addBook(String type, String isbn, String title, String author,
                        int year, double price, Object... additionalParams) {
        try {
            Book book = BookFactory.createBook(type, isbn, title, author, year, price, additionalParams);
            inventory.put(isbn, book);
            System.out.println("Quantum book store: Added book - " + book);
        } catch (InvalidBookTypeException e) {
            System.out.println("Quantum book store: Failed to add book - " + e.getMessage());
        }
    }

    /**
     * Removes and returns books older than the specified number of years
     * @param years the age threshold in years
     * @return list of removed books
     */
    public List<Book> removeOutdatedBooks(int years) {
        int currentYear = java.time.LocalDate.now().getYear();
        int cutoffYear = currentYear - years;

        List<Book> outdatedBooks = inventory.values().stream()
                .filter(book -> book.getYear() < cutoffYear)
                .collect(Collectors.toList());

        outdatedBooks.forEach(book -> {
            inventory.remove(book.getIsbn());
            System.out.println("Quantum book store: Removed outdated book - " + book);
        });

        return outdatedBooks;
    }

    /**
     * Purchases a book from the inventory
     * @param isbn the ISBN of the book to purchase
     * @param quantity the quantity to purchase
     * @param email the customer's email address
     * @param address the shipping address
     * @return PurchaseResult containing the outcome of the purchase
     */
    public PurchaseResult buyBook(String isbn, int quantity, String email, String address) {
        try {
            Book book = inventory.get(isbn);
            if (book == null) {
                throw new BookNotFoundException(isbn);
            }

            if (!(book instanceof Purchasable)) {
                throw new BookNotPurchasableException(book.getTitle());
            }

            Purchasable purchasableBook = (Purchasable) book;

            // Process purchase
            purchasableBook.processPurchase(quantity);
            double totalAmount = book.getPrice() * quantity;

            // Handle delivery based on book type
            if (book instanceof Shippable) {
                ((Shippable) book).ship(address, shippingService);
            }

            if (book instanceof Emailable) {
                ((Emailable) book).email(email, mailService);
            }

            String message = String.format("Quantum book store: Successfully purchased %d copies of %s",
                    quantity, book.getTitle());
            System.out.println(message);

            return PurchaseResult.success(totalAmount, message);

        } catch (BookNotFoundException | InsufficientStockException | BookNotPurchasableException e) {
            String errorMessage = "Quantum book store: Purchase failed - " + e.getMessage();
            System.out.println(errorMessage);
            return PurchaseResult.failure(errorMessage);
        }
    }

    /**
     * Displays the current inventory
     */
    public void displayInventory() {
        System.out.println("Quantum book store: Current Inventory:");
        inventory.values().forEach(book -> System.out.println("  " + book));
    }

    /**
     * Retrieves a book by its ISBN
     * @param isbn the ISBN to search for
     * @return the book if found, null otherwise
     */
    public Book getBook(String isbn) {
        return inventory.get(isbn);
    }
}