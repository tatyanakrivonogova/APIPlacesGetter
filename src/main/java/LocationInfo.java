public class LocationInfo {
    private final String name;
    private final String country;
    private final String state;
    private final String city;
    private final Coordinates coords;
    public LocationInfo(String name, String country, String state, String city, Coordinates coords) {
        this.name = name;
        this.country = country;
        this.state = state;
        this.city = city;
        this.coords = coords;
    }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getState() { return state; }
    public String getCity() { return city; }
    public Coordinates getCoords() { return coords; }
}
