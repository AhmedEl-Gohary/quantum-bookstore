# Quantum Bookstore

An extensible, interface‑driven Java application modeling an online bookstore. Supports multiple book types (paper, e‑book, showcase), inventory management, purchasing, shipping/email delivery, and comprehensive testing.

---

## Assumptions

- **ISBN is unique** per book. Attempting to add a book with a duplicate ISBN throws a `DuplicateBookException`.
- **PaperBooks are the only books with stock.** EBooks are treated as having infinite copies.
- **Book quantities cannot be edited once added.** Restocking isn't supported in this version.
- **All purchases must be of positive quantity.** Zero or negative quantities are rejected with `InvalidQuantityException`.
---


## Table of Contents

1. [Overview](#overview)
2. [Package & Class Hierarchy](#package--class-hierarchy)
3. [Design Patterns & Principles](#design-patterns--principles)
4. [Key Components & Logic](#key-components--logic)
5. [Usage Examples](#usage-examples)
6. [Testing](#testing)

---

## Overview

Quantum Bookstore maintains an inventory of three book types:

- **PaperBook**: has `stock`, can be shipped
- **EBook**: has `filetype`, can be emailed
- **ShowcaseBook**: demo only, not for sale

Common book fields:
- `isbn` (String)
- `title` (String)
- `year` (int)
- `price` (double)

All console output is prefixed with `Quantum book store:`.

Features:
- Add books by ISBN, title, year, price, plus type‑specific params
- Remove & return outdated books older than N years
- Purchase by ISBN, quantity, email, address →
    - Validates quantity > 0 & availability
    - Updates stock for paper books
    - Calculates total paid amount
    - Ships or emails via injected services
    - Throws domain‑specific exceptions on errors

---

## Package & Class Hierarchy

```text
dto/
└── PurchaseResult         • DTO encapsulating `paidAmount`, `message`, `successful`

exception/
├── BookNotFoundException
├── BookNotPurchasableException
├── InsufficientStockException
├── InvalidBookTypeException
├── InvalidQuantityException
└── DuplicateBookException

factory/
└── BookFactory            • Factory method to create `PaperBook`, `EBook`, `ShowcaseBook`

interfaces/
├── Purchasable            • `canPurchase(qty)`, `processPurchase(qty)`
├── Shippable              • `ship(address, ShippingService)`
└── Emailable              • `email(email, MailService)`

model/
├── Book (abstract)        • base class with common fields & `toString()`
├── PaperBook              • implements `Purchasable` & `Shippable`
├── EBook                  • implements `Purchasable` & `Emailable`
└── ShowcaseBook           • read-only demo type

service/
├── QuantumBookStore       • main service: `addBook`, `removeOutdatedBooks`, `buyBook`, `displayInventory`
├── ShippingService        • stub for physical shipping
└── MailService            • stub for ebook emailing

test/
└── QuantumBookstoreFullTest • JUnit suite covering all flows & edge cases
```
## Design Patterns & Principles

- **Factory Pattern** (`BookFactory`)
    - Centralizes creation of all `Book` subtypes.
    - New book types plug into factory without touching client code.

- **Strategy / Interface Segregation** (`Purchasable`, `Shippable`, `Emailable`)
    - Clients depend only on the behaviors they use.
    - Adding new delivery channels (e.g. SMSable) requires new interface + implementation.

- **DTO** (`PurchaseResult`)
    - Encapsulates outcome of purchase operations (amount, message, success flag).

- **Checked Exceptions for Domain Errors**
    - `BookNotFoundException`, `InsufficientStockException`, etc., force handling of every error case.

- **Single Responsibility & Open/Closed**
    - Each class has one clear responsibility.
    - System open for extension (new book/delivery types), closed for modification.

---

## Key Components & Logic

### `addBook(...)`

1. **Duplicate Check**: Rejects existing ISBN → `DuplicateBookException`.
2. **Type Dispatch**: Delegates creation to `BookFactory`.
3. **Inventory Insert**: Stores new `Book` in `Map<String,Book>` and logs success.

### `removeOutdatedBooks(int years)`

- Computes `cutoffYear = currentYear − years`.
- Streams inventory, filters `book.getYear() < cutoffYear`, removes and returns list.

### `buyBook(String isbn, int quantity, String email, String address)`

1. **Quantity Validation**: `quantity > 0` or `InvalidQuantityException`.
2. **Existence Check**: `inventory.get(isbn) != null` or `BookNotFoundException`.
3. **Purchasable Check**: `instanceof Purchasable` or `BookNotPurchasableException`.
4. **Process Purchase**: `processPurchase(quantity)` may throw `InsufficientStockException`.
5. **Delivery**
    - If `instanceof Shippable`, calls `ship(address, shippingService)`.
    - If `instanceof Emailable`, calls `email(email, mailService)`.
6. **Result**: Returns `PurchaseResult.success(totalAmount, message)` or `failure(...)` on any caught exception.

---

## Usage Examples

```java
QuantumBookStore store = new QuantumBookStore();

// Add diverse books
store.addBook("paper",    "978-111", "Clean Code",       "Robert C. Martin", 2008, 45.99, 10);
store.addBook("ebook",    "978-222", "Design Patterns",  "Gamma et al.",     1994, 29.99, "epub");
store.addBook("showcase", "978-333", "New Release Demo", "Jane Doe",         2024,  0.00);

// Remove outdated (>20 years old)
List<Book> old = store.removeOutdatedBooks(20);

// Purchase flow
PurchaseResult result = store.buyBook(
  "978-111", 2,
  "customer@example.com",
  "123 Main St, Anytown"
);
if (result.isSuccessful()) {
  System.out.println("Paid: " + result.getPaidAmount());
}
```

## Testing

All core functionality is covered by the **QuantumBookstoreFullTest** JUnit 5 suite located under `src/test/java`. Tests include:

- **Book Addition**
    - Adding paper, e‑book, and showcase books
    - Default filetype for e‑books
    - Handling invalid and duplicate ISBNs

- **Purchase Flows**
    - Successful paper‑book and e‑book purchases (shipping/email)
    - Failure modes: showcase not for sale, out‑of‑stock, book not found
    - Quantity guardrails: zero or negative quantities → `InvalidQuantityException`
    - Large‑quantity e‑book purchases

- **Inventory Removal**
    - Outdated removal at boundary and extreme thresholds
    - Removing from empty inventory

- **Edge Cases & Integration**
    - Empty‑inventory operations
    - Mixed add/purchase/remove sequence integrity
    - Console output prefix formatting  