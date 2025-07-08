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
public class QuantumBookStore {
    private final Map<String, Book> inventory;
    private final ShippingService shippingService;
    private final MailService mailService;

    public QuantumBookStore() {
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
    public void addBook(String type, String isbn, String title,
                        int year, double price, Object... additionalParams) {
        try {
            if (inventory.containsKey(isbn)) {
                throw new DuplicateBookException(isbn);
            }
            Book book = BookFactory.createBook(type, isbn, title, year, price, additionalParams);
            inventory.put(isbn, book);
            System.out.println("Quantum book store: Added book - " + book);
        } catch (InvalidBookTypeException | DuplicateBookException e) {
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
            if (quantity <= 0) {
                throw new InvalidQuantityException(quantity);
            }

            Book book = inventory.get(isbn);
            if (book == null) {
                throw new BookNotFoundException(isbn);
            }

            if (!(book instanceof Purchasable)) {
                throw new BookNotPurchasableException(book.getTitle());
            }
            Purchasable purchasable = (Purchasable) book;

            purchasable.processPurchase(quantity);
            double total = book.getPrice() * quantity;

            if (book instanceof Shippable) {
                ((Shippable) book).ship(address, shippingService);
            }
            if (book instanceof Emailable) {
                ((Emailable) book).email(email, mailService);
            }

            String successMsg = String.format(
                    "Quantum book store: Successfully purchased %d copies of %s",
                    quantity, book.getTitle()
            );
            System.out.println(successMsg);
            return PurchaseResult.success(total, successMsg);

        } catch (InvalidQuantityException | BookNotFoundException | InsufficientStockException |
                 BookNotPurchasableException ex) {
            String err = "Quantum book store: Purchase failed - " + ex.getMessage();
            System.out.println(err);
            return PurchaseResult.failure(err);

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