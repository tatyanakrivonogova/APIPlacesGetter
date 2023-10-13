import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class LocationGetter {
    private final LocationSet locationSet;
    private static final String APIkey = "b4ffdb7c-df80-4dff-8266-725cc3a06c2a";
    public LocationGetter() {
        this.locationSet = new LocationSet(new HashMap<>());
    }

    public CompletableFuture<LocationSet> run() {
        CompletableFuture<LocationSet> future = new CompletableFuture<>();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter location: ");

        String location = scanner.nextLine();
        try {
            makeRequest(location, future);
        } catch (IOException e) {
            e.printStackTrace();
            future.completeExceptionally(e);
        }
        System.out.println("Completed");
        return future;
    }
    public void makeRequest(String location,CompletableFuture<LocationSet> future) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://graphhopper.com/api/1/geocode?locale=ru&q=" + URLEncoder.encode("Цветной проезд") + "&key=" + APIkey)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                parseResponse(response.body().string());
                future.complete(locationSet);
            }
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
    }

    void parseResponse(String body) {
        JSONArray jsonArray = new JSONObject(body).getJSONArray("hits");
        int index = 1;
        for (Object o : jsonArray) {
            JSONObject object = (JSONObject) o;

            double latitude = object.getJSONObject("point").getDouble("lat");
            double longitude = object.getJSONObject("point").getDouble("lng");
            String name = object.getString("name");
            String country = object.getString("country");
            String state = object.getString("state");
            String city = object.getString("city");

            locationSet.getLocationsMap().put(index, new LocationInfo(name, new Coordinates(latitude, longitude)));
            //System.out.println(index + ": " + "name=" + name + " country=" + country + " state=" + state + " city=" + city + " latitude=" + latitude + " longitude=" + longitude);
            ++index;
        }
    }

    LocationInfo chooseLocation(LocationSet locationSet) {
        System.out.println("Список локаций:");
        for (Map.Entry<Integer, LocationInfo> location : locationSet.getLocationsMap().entrySet()) {
            System.out.println(location.getKey() + " " + location.getValue());
        }
        System.out.println("*************************");
        System.out.print("Choose one location and enter its index: ");
        Scanner scanner = new Scanner(System.in);
        int selectedIndex = scanner.nextInt();
        //locationSet.setSelectedPlace(locationSet.getLocationsMap().get(selectedIndex));
        //future.complete(locationSet.getLocationsMap().get(selectedIndex));
        return locationSet.getLocationsMap().get(selectedIndex);
    }
}
