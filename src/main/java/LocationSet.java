import java.util.HashMap;

public class LocationSet {
    private final HashMap<Integer, LocationInfo> locationsMap;
    public LocationSet(HashMap<Integer, LocationInfo> locationsMap) {
        this.locationsMap = locationsMap;
    }
    public HashMap<Integer, LocationInfo> getLocationsMap() { return locationsMap; }
}
