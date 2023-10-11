public class LocationInfo {
    private final String name;
    private final Coordinates coords;
    public LocationInfo(String name, Coordinates coords) {
        this.name = name;
        this.coords = coords;
    }
    public String getName() { return name; }
    public Coordinates getCoords() { return coords; }
}
