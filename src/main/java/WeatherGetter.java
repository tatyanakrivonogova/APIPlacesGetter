import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class WeatherGetter {
    private final LocationInfo locationInfo;
    private static final OkHttpClient client = new OkHttpClient();
    private static final String APIkey = "25ff6748da47a7d903abd3e5e6f6b7cb";
    public WeatherGetter(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }
    
    public CompletableFuture<String> getWeather() {
        CompletableFuture<String> future = new CompletableFuture<>();
        makeRequest(future);
        return future;
    }
    public void makeRequest(CompletableFuture<String> future) {
        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?units=metric&lat=" +
                        locationInfo.getCoords().getLatitude() + "&lon=" +
                        locationInfo.getCoords().getLongitude() + "&appid=" + APIkey)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                future.complete(parseResponse(response.body().string()));
            }
            public void onFailure(Call call, IOException e) {
                System.out.println("Не удалось получить прогноз погоды");
                future.completeExceptionally(e);
            }
        });
    }

    String parseResponse(String body) {
        JSONObject jsonObject = new JSONObject(body).getJSONObject("main");

        double temp = jsonObject.getDouble("temp");
        double feelsLike = jsonObject.getDouble("feels_like");
        double tempMin = jsonObject.getDouble("temp_min");
        double tempMax = jsonObject.getDouble("temp_max");

        return locationInfo.getName() + ": " + "температура:" + temp +
                " ощущается как:" + feelsLike + " минимум:" + tempMin + " максимум:" + tempMax;
    }
}
