public class Feedback {
    private final int bookingId;
    private final int rating;
    private final String comment;
    public Feedback(int bookingId, int rating, String comment) {
        this.bookingId = bookingId; this.rating = rating; this.comment = comment;
    }
    public int getBookingId() { return bookingId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    @Override public String toString() { return "Booking " + bookingId + " | Rating: " + rating + "/5 | " + comment; }
}
