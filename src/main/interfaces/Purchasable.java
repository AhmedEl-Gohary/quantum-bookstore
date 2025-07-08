package interfaces;

import exception.InsufficientStockException;

/**
 * Interface for books that can be purchased
 */
public interface Purchasable {
    /**
     * Checks if the specified quantity can be purchased
     * @param quantity the quantity to check
     * @return true if purchase is possible, false otherwise
     */
    boolean canPurchase(int quantity);

    /**
     * Processes the purchase by updating internal state
     * @param quantity the quantity to purchase
     * @throws InsufficientStockException if not enough stock is available
     */
    void processPurchase(int quantity) throws InsufficientStockException;
}