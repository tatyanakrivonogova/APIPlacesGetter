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

    public CompletableFuture<LocationSet> getLocations() {
        CompletableFuture<LocationSet> future = new CompletableFuture<>();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите название: ");

        String location = scanner.nextLine();
        try {
            makeRequest(location, future);
        } catch (IOException e) {
            e.printStackTrace();
            future.completeExceptionally(e);
        }
        return future;
    }
    public void makeRequest(String location,CompletableFuture<LocationSet> future) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://graphhopper.com/api/1/geocode?locale=ru&q=" + URLEncoder.encode(location) + "&key=" + APIkey)
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
            String country = object.has("country") ? object.getString("country") : "";
            String state = object.has("state") ? object.getString("state") : "";
            String city = object.has("city") ? object.getString("city") : "";

            locationSet.getLocationsMap().put(index, new LocationInfo(name, country, state, city, new Coordinates(latitude, longitude)));
            ++index;
        }
    }

    LocationInfo chooseLocation(LocationSet locationSet) {
        System.out.println("Список локаций:");
        for (Map.Entry<Integer, LocationInfo> location : locationSet.getLocationsMap().entrySet()) {
            System.out.println(location.getKey() + " " + location.getValue().getName()
                    + ", " + location.getValue().getCountry()
                    + ", " + location.getValue().getState()
                    + ", " + location.getValue().getCity()
                    + ", долгота: " + location.getValue().getCoords().getLongitude()
                    + ", широта: " + location.getValue().getCoords().getLatitude());
        }
        System.out.print("Выберите нужную локацию и введите ее номер: ");
        Scanner scanner = new Scanner(System.in);
        int selectedIndex = scanner.nextInt();
        return locationSet.getLocationsMap().get(selectedIndex);
    }
}
