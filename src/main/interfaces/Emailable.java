package interfaces;

import service.MailService;

/**
 * Interface for books that can be emailed
 */
public interface Emailable {
    /**
     * Emails the book to the specified email address
     * @param emailAddress the recipient's email address
     * @param mailService the mail service to use
     */
    void email(String emailAddress, MailService mailService);
}