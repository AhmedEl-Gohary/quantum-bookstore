package model;

import exception.InsufficientStockException;
import interfaces.Purchasable;
import interfaces.Shippable;
import service.ShippingService;

/**
 * Physical book that can be purchased and shipped
 */
public class PaperBook extends Book implements Purchasable, Shippable {
    private int stock;

    public PaperBook(String isbn, String title, int year, double price, int stock) {
        super(isbn, title, year, price);
        this.stock = stock;
    }

    public int getStock() { return stock; }

    @Override
    public boolean canPurchase(int quantity) {
        return stock >= quantity;
    }

    @Override
    public void processPurchase(int quantity) throws InsufficientStockException {
        if (!canPurchase(quantity)) {
            throw new InsufficientStockException(getTitle(), quantity, stock);
        }
        stock -= quantity;
    }

    @Override
    public void ship(String address, ShippingService shippingService) {
        shippingService.ship(this, address);
    }

    @Override
    public String toString() {
        return super.toString() + ", Stock: " + stock;
    }
}