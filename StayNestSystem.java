import java.io.*;
import java.util.*;

/** Core application logic shared by the GUI and TBI. */
public class StayNestSystem implements Evaluable {
    private final ArrayList<Person> users = new ArrayList<>();
    private final ArrayList<Property> properties = new ArrayList<>();
    private final ArrayList<Booking> bookings = new ArrayList<>();
    private final ArrayList<Feedback> feedbackList = new ArrayList<>();

    public ArrayList<Person> getUsers() { return users; }
    public ArrayList<Property> getProperties() { return properties; }
    public ArrayList<Booking> getBookings() { return bookings; }
    public ArrayList<Feedback> getFeedbackList() { return feedbackList; }

    public void loadUsers(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) throw new IllegalArgumentException("User file not found: " + fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.toLowerCase().startsWith("id,")) continue;
                String[] p = line.split(",", -1);
                if (p.length != 4) continue;
                try {
                    int id = Integer.parseInt(p[0].trim());
                    String role = p[1].trim();
                    String name = p[2].trim();
                    String email = p[3].trim();
                    if (findUserLinear(id) != null) continue;
                    users.add(role.equalsIgnoreCase("Host") ? new Host(id, name, email) : new Guest(id, name, email));
                } catch (NumberFormatException ignored) { /* skip malformed row */ }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not load users: " + e.getMessage(), e);
        }
    }

    public void saveUsers(String fileName) {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            out.println("id,role,name,email");
            for (Person p : users) out.println(p.getId() + "," + p.getRole() + "," + p.getName() + "," + p.getEmail());
        } catch (IOException e) {
            throw new IllegalStateException("Could not save users: " + e.getMessage(), e);
        }
    }

    // Linear search: O(n), works on unsorted data.
    public Person findUserLinear(int id) {
        for (Person p : users) if (p.getId() == id) return p;
        return null;
    }

    // Binary search: O(log n), requires users sorted by ID.
    public Person findUserBinary(int id) {
        users.sort(Comparator.comparingInt(Person::getId));
        int low = 0, high = users.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int value = users.get(mid).getId();
            if (value == id) return users.get(mid);
            if (value < id) low = mid + 1; else high = mid - 1;
        }
        return null;
    }

    public Person findUser(int id) { return findUserLinear(id); }

    public List<Person> searchUsersByName(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        ArrayList<Person> result = new ArrayList<>();
        for (Person p : users) if (p.getName().toLowerCase().contains(q)) result.add(p);
        return result;
    }

    public void sortUsersById() { users.sort(Comparator.comparingInt(Person::getId)); }
    public void sortUsersByName() { users.sort(Comparator.comparing(Person::getName, String.CASE_INSENSITIVE_ORDER)); }
    public void sortUsersByEmail() { users.sort(Comparator.comparing(Person::getEmail, String.CASE_INSENSITIVE_ORDER)); }

    public void addUser(Person person) {
        if (person == null) throw new IllegalArgumentException("User cannot be null.");
        if (findUserLinear(person.getId()) != null) throw new IllegalArgumentException("User ID already exists.");
        if (person.getName().isBlank() || person.getEmail().isBlank()) throw new IllegalArgumentException("Name and email are required.");
        users.add(person);
    }
    public boolean deleteUser(int id) { Person p = findUserLinear(id); return p != null && users.remove(p); }
    public boolean updateUser(int id, String name, String email) {
        Person p = findUserLinear(id); if (p == null) return false;
        if (name == null || name.isBlank() || email == null || email.isBlank()) throw new IllegalArgumentException("Name and email are required.");
        p.setName(name.trim()); p.setEmail(email.trim()); return true;
    }

    public Property findProperty(int id) { for (Property p : properties) if (p.getId() == id) return p; return null; }
    public void addProperty(Property p) {
        if (findProperty(p.getId()) != null) throw new IllegalArgumentException("Property ID already exists.");
        if (p.getPricePerNight() < 0) throw new IllegalArgumentException("Price cannot be negative.");
        properties.add(p);
    }
    public boolean deleteProperty(int id) { Property p = findProperty(id); return p != null && properties.remove(p); }
    public List<Property> searchProperties(String query) {
        String q = query == null ? "" : query.toLowerCase().trim(); ArrayList<Property> r = new ArrayList<>();
        for (Property p : properties) if (p.getName().toLowerCase().contains(q) || p.getLocation().toLowerCase().contains(q)) r.add(p);
        return r;
    }
    public void sortPropertiesByPrice() { properties.sort(Comparator.comparingDouble(Property::getPricePerNight)); }
    public void sortPropertiesByName() { properties.sort(Comparator.comparing(Property::getName, String.CASE_INSENSITIVE_ORDER)); }

    public Booking findBooking(int id) { for (Booking b : bookings) if (b.getId() == id) return b; return null; }
    public void addBooking(Booking b) {
        if (findBooking(b.getId()) != null) throw new IllegalArgumentException("Booking ID already exists.");
        Person guest = findUserLinear(b.getGuestId());
        if (!(guest instanceof Guest)) throw new IllegalArgumentException("A valid guest is required.");
        if (findProperty(b.getPropertyId()) == null) throw new IllegalArgumentException("Property not found.");
        bookings.add(b);
    }
    public boolean deleteBooking(int id) { Booking b = findBooking(id); return b != null && bookings.remove(b); }
    public void sortBookingsByDate() { bookings.sort(Comparator.comparing(Booking::getDate)); }
    public void sortBookingsById() { bookings.sort(Comparator.comparingInt(Booking::getId)); }
    public List<Booking> searchBookings(String query) {
        String q = query == null ? "" : query.toLowerCase().trim(); ArrayList<Booking> r = new ArrayList<>();
        for (Booking b : bookings) if (String.valueOf(b.getId()).contains(q) || b.getDate().toLowerCase().contains(q) || b.getStatus().toLowerCase().contains(q)) r.add(b);
        return r;
    }

    @Override
    public String evaluate(int bookingId, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be between 1 and 5.");
        if (findBooking(bookingId) == null) throw new IllegalArgumentException("Booking not found.");
        feedbackList.add(new Feedback(bookingId, rating, comment == null ? "" : comment));
        if (rating >= 4) return "Positive feedback recorded. Reward: +10 points for the host.";
        if (rating <= 2) return "Negative feedback recorded. Penalty: -10 points for the host.";
        return "Neutral feedback recorded. No reward or penalty.";
    }

    public void seedDemoData() {
        if (properties.isEmpty()) {
            properties.add(new Property(101, "City View Apartment", "Sydney", 150.00, 2));
            properties.add(new Property(102, "Coastal Cottage", "Newcastle", 120.00, 3));
            properties.add(new Property(103, "Modern Studio", "Melbourne", 100.00, 4));
        }
        if (bookings.isEmpty()) {
            if (findUserLinear(1) != null && findProperty(101) != null) bookings.add(new Booking(1001, 1, 101, "2026-09-10", "Confirmed"));
            if (findUserLinear(5) != null && findProperty(102) != null) bookings.add(new Booking(1002, 5, 102, "2026-09-15", "Confirmed"));
        }
    }
}
