public class Property {
    private final int id;
    private String name;
    private String location;
    private double pricePerNight;
    private int hostId;

    public Property(int id, String name, String location, double pricePerNight, int hostId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.pricePerNight = pricePerNight;
        this.hostId = hostId;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public double getPricePerNight() { return pricePerNight; }
    public int getHostId() { return hostId; }
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public void setHostId(int hostId) { this.hostId = hostId; }
    @Override public String toString() {
        return id + " | " + name + " | " + location + " | $" + String.format("%.2f", pricePerNight) + " | Host: " + hostId;
    }
}
