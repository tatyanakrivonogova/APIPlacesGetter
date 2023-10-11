import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class WeatherGetter implements Runnable {
    private final LocationSet locationSet;
    private static final String APIkey = "25ff6748da47a7d903abd3e5e6f6b7cb";
    public WeatherGetter(LocationSet locationSet) {
        this.locationSet = locationSet;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose one location and enter its index: ");

        int selectedIndex = scanner.nextInt();
        locationSet.setSelectedPlace(locationSet.getLocationsMap().get(selectedIndex));
        try {
            makeRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done!");
    }
    public void makeRequest() throws IOException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().
                uri(URI.create("https://api.openweathermap.org/data/2.5/weather?units=metric&lat=" +
                        locationSet.getSelectedLocation().getCoords().getLatitude() + "&lon=" +
                        locationSet.getSelectedLocation().getCoords().getLongitude() + "&appid=" + APIkey)).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::parseResponse)
                .join();
    }

    void parseResponse(String body) {
        //System.out.println(body);
        JSONObject jsonObject = new JSONObject(body).getJSONObject("main");

        double temp = jsonObject.getDouble("temp");
        double feelsLike = jsonObject.getDouble("feels_like");
        double tempMin = jsonObject.getDouble("temp_min");
        double tempMax = jsonObject.getDouble("temp_max");

        System.out.println("Weather for " + locationSet.getSelectedLocation().getName() + ": " + "temp:" + temp +
                " feels like:" + feelsLike + " temp_min:" + tempMin + " temp_max:" + tempMax);
    }
}
