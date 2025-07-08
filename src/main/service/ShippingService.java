package service;

import model.PaperBook;

/**
 * Service for handling book shipping operations
 */
public class ShippingService {

    /**
     * Ships a paper book to the specified address
     * @param book the book to ship
     * @param address the shipping address
     */
    public void ship(PaperBook book, String address) {
        System.out.println("Quantum book store: Shipping " + book.getTitle() + " to " + address);
    }
}