import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class InterestingPlacesGetter implements Runnable {
    private final LocationSet locationSet;
    private static final int RADIUS = 500;
    private static final String APIkey = "5ae2e3f221c38a28845f05b61fa85b73872df08065f37412956e8708";
    public InterestingPlacesGetter(LocationSet locationSet, int selectedIndex) {
        this.locationSet = locationSet;
        locationSet.setSelectedPlace(locationSet.getLocationsMap().get(selectedIndex));
    }

    @Override
    public void run() {
        try {
            makeRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done!");
    }
    public void makeRequest() throws IOException {
        getDescriptions(getPlaces());
    }

    private JSONArray getPlaces() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().
                uri(URI.create("http://api.opentripmap.com/0.1/ru/places/radius?radius=" + RADIUS +
                        "&lon=" + locationSet.getSelectedLocation().getCoords().getLongitude() +
                        "&lat=" + locationSet.getSelectedLocation().getCoords().getLatitude() +
                        "&apikey=" + APIkey)).build();
        String places = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();
        System.out.println(places);
        return new JSONObject(places).getJSONArray("features");
    }

    private void getDescriptions(JSONArray features) throws IOException {
        System.out.println("Interesting places near the " + locationSet.getSelectedLocation().getName() + ":");

        OkHttpClient client = new OkHttpClient();
        for (Object o : features) {
            JSONObject feature = (JSONObject) o;

            String place = feature.getJSONObject("properties").getString("name");
            if (!place.isEmpty()) {
                Request request = new Request.Builder()
                        .url("http://api.opentripmap.com/0.1/ru/places/xid/" +
                                feature.getJSONObject("properties").getString("xid")+ "?apikey=" + APIkey
                        )
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                parseResponse(response.body().string());
//                JSONObject jsonObject = new JSONObject(response.body().string());
//                if (jsonObject.has("kinds")) {
//                    String description = jsonObject.getString("kinds");
//                    System.out.println(placeName + " - " + description);
//                }
            }
        }

//        HttpClient client = HttpClient.newHttpClient();
//
//        for (Object o : features) {
//            JSONObject feature = (JSONObject) o;
//            //System.out.println(feature);
//            String featureName = feature.getJSONObject("properties").getString("name");
//            if (!featureName.isEmpty()) {
//                System.out.println("searching...");
//                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://api.opentripmap.com/0.1/ru/places/xid/" +
//                                feature.getJSONObject("properties").getString("xid")+ "?apikey=" + APIkey)).build();
//                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                        .thenApply(HttpResponse::body)
//                        .thenAccept(this::parseResponse)
//                        .join();
//            }
//        }
    }

    void parseResponse(String body) {
        System.out.println("parsing...");
        System.out.println(body);


        JSONObject jsonObject = new JSONObject(body);
        if (jsonObject.has("kinds")) {
            String description = jsonObject.getString("kinds");
            System.out.println(jsonObject.getString("name") + " - " + description);
        }

//        JSONObject place = new JSONObject(body);
//        System.out.println(place);
//        String name = place.getString("name");
//        String kinds = place.getString("place");
//        String road = place.getJSONObject("address").getString("road");
//        String house = place.getJSONObject("address").getString("house");
//        System.out.println("name: ");
//        StringBuilder description = new StringBuilder();
//        description.append(name);
//        description.append(": ");
//        if (kinds != null) description.append(kinds);
//        if (road != null) description.append(road);
//        if (house != null) description.append(house);
//
//        System.out.println(name + ", " + road + ", " + house);
    }
}
