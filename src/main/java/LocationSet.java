import java.util.HashMap;

public class LocationSet {
    private final HashMap<Integer, LocationInfo> locationsMap;
    private LocationInfo selectedLocation;

    public LocationSet(HashMap<Integer, LocationInfo> locationsMap) {
        this.locationsMap = locationsMap;
    }
    public HashMap<Integer, LocationInfo> getLocationsMap() { return locationsMap; }
    public void setSelectedPlace(LocationInfo selectedLocation) {
        this.selectedLocation = selectedLocation;
    }
    public LocationInfo getSelectedLocation() { return selectedLocation; }
}
