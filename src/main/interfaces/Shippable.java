package interfaces;

import service.ShippingService;

/**
 * Interface for books that can be shipped
 */
public interface Shippable {
    /**
     * Ships the book to the specified address
     * @param address the shipping address
     * @param shippingService the shipping service to use
     */
    void ship(String address, ShippingService shippingService);
}