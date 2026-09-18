public class Host extends Person {
    public Host(int id, String name, String email) { super(id, name, email); }
    @Override public String getRole() { return "Host"; }
}
