package dto;

/**
 * Data Transfer Object for purchase operation results
 */
public class PurchaseResult {
    private final double paidAmount;
    private final String message;
    private final boolean successful;

    public PurchaseResult(double paidAmount, String message, boolean successful) {
        this.paidAmount = paidAmount;
        this.message = message;
        this.successful = successful;
    }

    public static PurchaseResult success(double amount, String message) {
        return new PurchaseResult(amount, message, true);
    }

    public static PurchaseResult failure(String message) {
        return new PurchaseResult(0.0, message, false);
    }

    public double getPaidAmount() { return paidAmount; }
    public String getMessage() { return message; }
    public boolean isSuccessful() { return successful; }
}