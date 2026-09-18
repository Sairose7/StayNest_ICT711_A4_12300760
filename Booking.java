public class Booking {
    private final int id;
    private final int guestId;
    private final int propertyId;
    private String date;
    private String status;

    public Booking(int id, int guestId, int propertyId, String date, String status) {
        this.id = id;
        this.guestId = guestId;
        this.propertyId = propertyId;
        this.date = date;
        this.status = status;
    }
    public int getId() { return id; }
    public int getGuestId() { return guestId; }
    public int getPropertyId() { return propertyId; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public void setDate(String date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }
    @Override public String toString() {
        return id + " | Guest: " + guestId + " | Property: " + propertyId +
               " | Date: " + date + " | Status: " + status;
    }
}
