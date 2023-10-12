import okhttp3.*;
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
        getPlaces();
    }

    private void getPlaces() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.opentripmap.com/0.1/ru/places/radius?radius=" + RADIUS +
                        "&lon=" + locationSet.getSelectedLocation().getCoords().getLongitude() +
                        "&lat=" + locationSet.getSelectedLocation().getCoords().getLatitude() +
                        "&apikey=" + APIkey)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response);
//                return new JSONObject(response).getJSONArray("features");
                getDescriptions(new JSONObject(response).getJSONArray("features"));
            }
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });

//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder().
//                uri(URI.create("http://api.opentripmap.com/0.1/ru/places/radius?radius=" + RADIUS +
//                        "&lon=" + locationSet.getSelectedLocation().getCoords().getLongitude() +
//                        "&lat=" + locationSet.getSelectedLocation().getCoords().getLatitude() +
//                        "&apikey=" + APIkey)).build();
//        String places = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                .thenApply(HttpResponse::body)
//                .join();

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

                client.newCall(request).enqueue(new Callback() {
                    public void onResponse(Call call, Response response) throws IOException {
                        parseResponse(response.body().string());
                    }
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                });
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
        //System.out.println("parsing...");
        //System.out.println(body);


//        JSONObject jsonObject = new JSONObject(body);
//        if (jsonObject.has("kinds")) {
//            String description = jsonObject.getString("kinds");
//            if (jsonObject.has("address") && jsonObject.getJSONObject("address").has("road")) {
//                System.out.println(jsonObject.getJSONObject("address").getString("road"));
//            }
//            System.out.println(jsonObject.getString("name") + " - " + description);
//        }

        JSONObject place = new JSONObject(body);
        //System.out.println(place);
        StringBuilder description = new StringBuilder();
        description.append(place.getString("name"));
        description.append(" (");
        if (place.has("kinds")) {
            description.append(place.getString("kinds"));
        }
        if (place.has("road")) {
            description.append(", ");
            description.append(place.getString("road"));
        }
        if (place.has("house")) {
            description.append(", ");
            description.append(place.getString("house"));
        }
        description.append(")");

        System.out.println(description);
    }
}
