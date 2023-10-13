import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class WeatherGetter {
    private final LocationInfo locationInfo;
    private static final String APIkey = "25ff6748da47a7d903abd3e5e6f6b7cb";
    public WeatherGetter(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }
    
    public CompletableFuture<String> run() {
        //locationSet.setSelectedPlace(locationSet.getLocationsMap().get(selectedIndex));
        CompletableFuture<String> future = new CompletableFuture<>();
        makeRequest(future);
        System.out.println("Done!");
        return future;
    }
    public void makeRequest(CompletableFuture<String> future) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?units=metric&lat=" +
                        locationInfo.getCoords().getLatitude() + "&lon=" +
                        locationInfo.getCoords().getLongitude() + "&appid=" + APIkey)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                future.complete(parseResponse(response.body().string()));
            }
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
    }

    String parseResponse(String body) {
        //System.out.println(body);
        JSONObject jsonObject = new JSONObject(body).getJSONObject("main");

        double temp = jsonObject.getDouble("temp");
        double feelsLike = jsonObject.getDouble("feels_like");
        double tempMin = jsonObject.getDouble("temp_min");
        double tempMax = jsonObject.getDouble("temp_max");

        return "Weather for " + locationInfo.getName() + ": " + "temp:" + temp +
                " feels like:" + feelsLike + " temp_min:" + tempMin + " temp_max:" + tempMax;
    }
}
