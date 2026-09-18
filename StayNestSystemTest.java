import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StayNestSystemTest {
    private StayNestSystem system;

    @BeforeEach
    void setUp() {
        system = new StayNestSystem();
        system.addUser(new Guest(1, "Alice", "alice@example.com"));
        system.addUser(new Host(2, "Bob", "bob@example.com"));
        system.addUser(new Guest(3, "Charlie", "charlie@example.com"));
        system.addProperty(new Property(101, "City View", "Sydney", 150, 2));
        system.addBooking(new Booking(1001, 1, 101, "2026-09-10", "Confirmed"));
    }

    @Test void addUserAndFindUserLinearWorks() { assertEquals("Alice", system.findUserLinear(1).getName()); }
    @Test void binarySearchFindsExistingUser() { assertEquals("Bob", system.findUserBinary(2).getName()); }
    @Test void binarySearchReturnsNullForMissingUser() { assertNull(system.findUserBinary(999)); }
    @Test void sortUsersByNameOrdersAscending() { system.sortUsersByName(); assertEquals("Alice", system.getUsers().get(0).getName()); }
    @Test void searchPropertiesFindsByLocation() { assertEquals(1, system.searchProperties("sydney").size()); }
    @Test void addBookingRejectsNonGuest() { assertThrows(IllegalArgumentException.class, () -> system.addBooking(new Booking(1002, 2, 101, "2026-09-20", "Confirmed"))); }
    @Test void evaluateCreatesFeedback() { system.evaluate(1001, 5, "Great stay"); assertEquals(1, system.getFeedbackList().size()); }
    @Test void invalidRatingIsRejected() { assertThrows(IllegalArgumentException.class, () -> system.evaluate(1001, 6, "Invalid")); }
}
