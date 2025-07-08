package service;

import model.EBook;

/**
 * Service for handling email delivery of ebooks
 */
public class MailService {

    /**
     * Sends an ebook to the specified email address
     * @param book the ebook to send
     * @param email the recipient's email address
     */
    public void sendEBook(EBook book, String email) {
        System.out.println("Quantum book store: Sending " + book.getTitle() +
                " (" + book.getFiletype() + ") to " + email);
    }
}