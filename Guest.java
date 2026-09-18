public class Guest extends Person {
    public Guest(int id, String name, String email) { super(id, name, email); }
    @Override public String getRole() { return "Guest"; }
}
