import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class LocationGetter {
    private final LocationSet locationSet;
    private static final String APIkey = "b4ffdb7c-df80-4dff-8266-725cc3a06c2a";
    public LocationGetter(LocationSet locationSet) {
        this.locationSet = locationSet;
    }

    public CompletableFuture<Integer> run() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter location: ");

        String location = scanner.nextLine();
        try {
            makeRequest(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Done!");
        System.out.print("Choose one location and enter its index: ");

        int selectedIndex = scanner.nextInt();
//        locationSet.setSelectedPlace(locationSet.getLocationsMap().get(selectedIndex));
        future.complete(selectedIndex);
        System.out.println("Completed");
        return future;
    }
    public void makeRequest(String location) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://graphhopper.com/api/1/geocode?locale=ru&q=" + URLEncoder.encode("Цветной проезд") + "&key=" + APIkey)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                parseResponse(response.body().string());
            }
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
        //client.dispatcher().executorService().shutdown();

        //String body = response.body().string();
        //System.out.println(body);

//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://graphhopper.com/api/1/geocode?locale=ru&q=" + URLEncoder.encode("Цветной проезд") + "&key=" + APIkey)).build();
//        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                        .thenApply(HttpResponse::body)
//                                .thenAccept(this::parseResponse)
//                                        .join();
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
            System.out.println(index + ": " + "name=" + name + " country=" + country + " state=" + state + " city=" + city + " latitude=" + latitude + " longitude=" + longitude);
            ++index;
        }
    }
}
