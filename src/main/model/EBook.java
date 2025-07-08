package model;

import exception.InsufficientStockException;
import interfaces.Emailable;
import interfaces.Purchasable;
import service.MailService;

/**
 * Digital book that can be purchased and emailed
 */
public class EBook extends Book implements Purchasable, Emailable {
    private final String filetype;

    public EBook(String isbn, String title, int year, double price, String filetype) {
        super(isbn, title, year, price);
        this.filetype = filetype;
    }

    public String getFiletype() { return filetype; }

    @Override
    public boolean canPurchase(int quantity) {
        return true;
    }

    @Override
    public void processPurchase(int quantity) throws InsufficientStockException {

    }

    @Override
    public void email(String emailAddress, MailService mailService) {
        mailService.sendEBook(this, emailAddress);
    }

    @Override
    public String toString() {
        return super.toString() + ", Filetype: " + filetype;
    }
}