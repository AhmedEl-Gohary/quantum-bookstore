import service.QuantumBookStore;
import dto.PurchaseResult;
import model.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuantumBookstoreFullTest {

    private QuantumBookStore bookstore;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        bookstore = new QuantumBookStore();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        outputStream.reset();
    }

    @Nested
    @DisplayName("Book Addition Tests")
    class BookAdditionTests {
        @Test @DisplayName("Should successfully add a paper book")
        void testAddPaperBook() {
            bookstore.addBook("paper", "978-1234567890", "Clean Code", 2008, 45.99, 10);

            Book book = bookstore.getBook("978-1234567890");
            assertNotNull(book);
            assertInstanceOf(PaperBook.class, book);
            assertEquals("978-1234567890", book.getIsbn());
            assertEquals("Clean Code", book.getTitle());
            assertEquals(2008, book.getYear());
            assertEquals(45.99, book.getPrice(), 0.01);
            assertEquals(10, ((PaperBook) book).getStock());
        }

        @Test @DisplayName("Should successfully add an ebook")
        void testAddEBook() {
            bookstore.addBook("ebook", "978-0987654321", "Design Patterns", 1994, 29.99, "epub");

            Book book = bookstore.getBook("978-0987654321");
            assertNotNull(book);
            assertInstanceOf(EBook.class, book);
            assertEquals("978-0987654321", book.getIsbn());
            assertEquals("Design Patterns", book.getTitle());
            assertEquals(1994, book.getYear());
            assertEquals(29.99, book.getPrice(), 0.01);
            assertEquals("epub", ((EBook) book).getFiletype());
        }

        @Test @DisplayName("Should successfully add a showcase book")
        void testAddShowcaseBook() {
            bookstore.addBook("showcase", "978-1111111111", "New Release Preview", 2024, 0.0);

            Book book = bookstore.getBook("978-1111111111");
            assertNotNull(book);
            assertInstanceOf(ShowcaseBook.class, book);
            assertEquals("978-1111111111", book.getIsbn());
            assertEquals("New Release Preview", book.getTitle());
            assertEquals(2024, book.getYear());
            assertEquals(0.0, book.getPrice(), 0.01);
        }

        @Test @DisplayName("Should handle invalid book type gracefully")
        void testAddInvalidBookType() {
            bookstore.addBook("invalid", "978-3333333333", "Invalid Book", 2023, 19.99);

            assertNull(bookstore.getBook("978-3333333333"));

            String output = outputStream.toString();
            assertTrue(output.contains("Failed to add book"));
            assertTrue(output.toLowerCase().contains("invalid book type"));
        }

        @Test @DisplayName("Should add ebook with default filetype when not specified")
        void testAddEBookWithDefaultFiletype() {
            bookstore.addBook("ebook", "978-4444444444", "Java Programming", 2020, 25.99);

            EBook book = (EBook) bookstore.getBook("978-4444444444");
            assertNotNull(book);
            assertEquals("pdf", book.getFiletype());
        }

        @Test @DisplayName("Should reject duplicate ISBNs")
        void testAddDuplicateISBN() {
            bookstore.addBook("paper", "123-dup", "First Title", 2020, 10.0, 5);
            bookstore.addBook("paper", "123-dup", "Second Title", 2021, 12.0, 3);

            // Inventory should still only hold the first one
            Book b = bookstore.getBook("123-dup");
            assertNotNull(b);
            assertEquals("First Title", b.getTitle());

            String log = outputStream.toString();
            assertTrue(log.contains("Added book"));
            assertTrue(log.contains("Failed to add book"));
            assertTrue(log.toLowerCase().contains("duplicate isbn"));
        }
    }

    @Nested
    @DisplayName("Book Purchase Tests")
    class BookPurchaseTests {
        @BeforeEach
        void setUpBooks() {
            bookstore.addBook("paper",   "978-1234567890", "Clean Code",           2008, 45.99, 10);
            bookstore.addBook("ebook",   "978-0987654321", "Design Patterns",      1994, 29.99, "epub");
            bookstore.addBook("showcase","978-1111111111", "New Release Preview",  2024,  0.00);
        }

        @Test @DisplayName("Should successfully purchase paper book")
        void testSuccessfulPaperBookPurchase() {
            PurchaseResult result = bookstore.buyBook(
                    "978-1234567890", 2, "cust@domain.com", "123 Main St"
            );

            assertTrue(result.isSuccessful());
            assertEquals(45.99 * 2, result.getPaidAmount(), 0.01);
            assertTrue(result.getMessage().contains("Successfully purchased"));

            PaperBook book = (PaperBook) bookstore.getBook("978-1234567890");
            assertEquals(8, book.getStock());

            assertTrue(outputStream.toString().contains("Shipping Clean Code to 123 Main St"));
        }

        @Test @DisplayName("Should successfully purchase ebook")
        void testSuccessfulEBookPurchase() {
            PurchaseResult result = bookstore.buyBook(
                    "978-0987654321", 1, "cust@domain.com", "123 Main St"
            );

            assertTrue(result.isSuccessful());
            assertEquals(29.99, result.getPaidAmount(), 0.01);
            assertTrue(outputStream.toString().contains("Sending Design Patterns (epub) to cust@domain.com"));
        }

        @Test @DisplayName("Should fail to purchase showcase book")
        void testFailedShowcaseBookPurchase() {
            PurchaseResult result = bookstore.buyBook(
                    "978-1111111111", 1, "cust@domain.com", "123 Main St"
            );

            assertFalse(result.isSuccessful());
            assertTrue(result.getMessage().toLowerCase().contains("not for sale"));
        }

        @Test @DisplayName("Should fail when insufficient stock")
        void testInsufficientStockPurchase() {
            PurchaseResult result = bookstore.buyBook(
                    "978-1234567890", 20, "cust@domain.com", "123 Main St"
            );

            assertFalse(result.isSuccessful());
            assertTrue(result.getMessage().toLowerCase().contains("insufficient stock"));
        }

        @Test @DisplayName("Should fail when book not found")
        void testBookNotFoundPurchase() {
            PurchaseResult result = bookstore.buyBook(
                    "978-9999999999", 1, "cust@domain.com", "123 Main St"
            );

            assertFalse(result.isSuccessful());
            assertTrue(result.getMessage().toLowerCase().contains("book not found"));
        }

        @Test @DisplayName("Should handle purchasing exact stock amount")
        void testPurchaseExactStockAmount() {
            bookstore.addBook("paper", "978-5555555555", "Limited Edition", 2023, 50.0, 3);
            PurchaseResult result = bookstore.buyBook(
                    "978-5555555555", 3, "cust@domain.com", "123 Main St"
            );

            assertTrue(result.isSuccessful());
            assertEquals(150.0, result.getPaidAmount(), 0.01);
            assertEquals(0, ((PaperBook)bookstore.getBook("978-5555555555")).getStock());
        }

        @Test @DisplayName("Should handle multiple purchases from same book")
        void testMultiplePurchasesFromSameBook() {
            PurchaseResult r1 = bookstore.buyBook(
                    "978-1234567890", 3, "a@domain.com", "1 Elm St"
            );
            PurchaseResult r2 = bookstore.buyBook(
                    "978-1234567890", 2, "b@domain.com", "2 Oak Ave"
            );

            assertTrue(r1.isSuccessful() && r2.isSuccessful());
            assertEquals(5, ((PaperBook)bookstore.getBook("978-1234567890")).getStock());
        }
    }

    @Nested
    @DisplayName("Outdated Book Removal Tests")
    class OutdatedBookRemovalTests {
        @BeforeEach
        void initInventory() {
            bookstore.addBook("paper",   "978-1111111111", "Old Book 1",    2000, 15.99, 5);
            bookstore.addBook("paper",   "978-2222222222", "Old Book 2",    1995, 12.99, 3);
            bookstore.addBook("ebook",   "978-3333333333", "Recent Book",   2020, 25.99, "pdf");
            bookstore.addBook("showcase","978-4444444444", "Current Book",  2023, 30.00);
        }

        @Test @DisplayName("Should remove books older than 20 years")
        void testRemoveOutdatedBooks() {
            List<Book> removed = bookstore.removeOutdatedBooks(20);
            assertEquals(2, removed.size());
            assertNull(bookstore.getBook("978-1111111111"));
            assertNull(bookstore.getBook("978-2222222222"));
            assertNotNull(bookstore.getBook("978-3333333333"));
            assertNotNull(bookstore.getBook("978-4444444444"));
        }

        @Test @DisplayName("Should remove no books when threshold is very large")
        void testRemoveOutdatedBooksNoneFound() {
            List<Book> removed = bookstore.removeOutdatedBooks(100);
            assertEquals(0, removed.size());
            assertNotNull(bookstore.getBook("978-1111111111"));
            assertNotNull(bookstore.getBook("978-2222222222"));
            assertNotNull(bookstore.getBook("978-3333333333"));
            assertNotNull(bookstore.getBook("978-4444444444"));
        }

        @Test @DisplayName("Should remove all books when threshold is zero")
        void testRemoveAllOutdatedBooks() {
            List<Book> removed = bookstore.removeOutdatedBooks(0);
            assertEquals(4, removed.size());
            assertNull(bookstore.getBook("978-1111111111"));
            assertNull(bookstore.getBook("978-2222222222"));
            assertNull(bookstore.getBook("978-3333333333"));
            assertNull(bookstore.getBook("978-4444444444"));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesAndIntegrationTests {
        @Test
        @DisplayName("Should reject zero quantity purchase")
        void testZeroQuantityPurchase() {
            bookstore.addBook("paper", "978-1234567890", "Clean Code", 2008, 45.99, 10);
            PurchaseResult r = bookstore.buyBook("978-1234567890", 0, "x@d.com", "Addr");

            assertFalse(r.isSuccessful());
            assertEquals(0.0, r.getPaidAmount(), 0.01);
            assertTrue(r.getMessage().toLowerCase().contains("invalid purchase quantity"));
            assertEquals(10, ((PaperBook)bookstore.getBook("978-1234567890")).getStock());
        }

        @Test
        @DisplayName("Should reject negative quantity purchase")
        void testNegativeQuantityPurchase() {
            bookstore.addBook("paper", "978-1234567890", "Clean Code", 2008, 45.99, 10);
            PurchaseResult r = bookstore.buyBook("978-1234567890", -5, "x@d.com", "Addr");

            assertFalse(r.isSuccessful());
            assertEquals(0.0, r.getPaidAmount(), 0.01);
            assertTrue(r.getMessage().toLowerCase().contains("invalid purchase quantity"));
            assertEquals(10, ((PaperBook)bookstore.getBook("978-1234567890")).getStock());
        }
    }

    @Nested
    @DisplayName("Additional Edge Case Tests")
    class AdditionalEdgeCaseTests {
        @Test
        @DisplayName("Purchase from empty inventory fails")
        void testPurchaseOnEmptyInventory() {
            PurchaseResult r = bookstore.buyBook("no-isbn", 1, "x@d.com", "Addr");
            assertFalse(r.isSuccessful());
            assertTrue(r.getMessage().toLowerCase().contains("book not found"));
        }

        @Test
        @DisplayName("Removing outdated from empty inventory yields empty list")
        void testRemoveOutdatedEmptyInventory() {
            List<Book> removed = bookstore.removeOutdatedBooks(5);
            assertTrue(removed.isEmpty());
        }

        @Test
        @DisplayName("Should handle large quantity ebook purchase")
        void testLargeQuantityEbookPurchase() {
            bookstore.addBook("ebook", "978-9999999998", "Big Data", 2021, 9.99, "pdf");
            PurchaseResult r = bookstore.buyBook("978-9999999998", 10000, "y@d.com", "Addr");
            assertTrue(r.isSuccessful());
            assertEquals(9.99 * 10000, r.getPaidAmount(), 0.01);
        }

        @Test
        @DisplayName("Inventory stays correct after mixed operations")
        void testInventoryIntegrity() {
            bookstore.addBook("paper", "A", "T1", 2010, 5.0, 2);
            bookstore.addBook("paper", "B", "T2", 2000, 5.0, 1);
            bookstore.buyBook("A", 1, "a", "a");
            List<Book> removed = bookstore.removeOutdatedBooks(24);
            assertTrue(removed.stream().anyMatch(b -> b.getIsbn().equals("B")));
            assertNull(bookstore.getBook("B"));
            assertNotNull(bookstore.getBook("A"));
            assertEquals(1, ((PaperBook) bookstore.getBook("A")).getStock());
        }
    }

    @Test @DisplayName("Should display proper console output format")
    void testConsoleOutputFormat() {
        bookstore.addBook("paper", "978-1234567890", "Clean Code", 2008, 45.99, 10);
        bookstore.buyBook("978-1234567890", 1, "a@d.com", "Addr");
        String[] lines = outputStream.toString().split("\\r?\\n");
        for (String line : lines) {
            if (!line.isBlank()) {
                assertTrue(line.contains("Quantum book store"),
                        "Expected prefix on: " + line);
            }
        }
    }
}
