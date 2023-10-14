import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class InterestingPlacesGetter {
    private final LocationInfo locationInfo;
    private static final OkHttpClient client = new OkHttpClient();
    private static final int RADIUS = 500;
    private static final String APIkey = "5ae2e3f221c38a28845f05b61fa85b73872df08065f37412956e8708";
    public InterestingPlacesGetter(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }

    public CompletableFuture<JSONArray> getPlaces() {
        CompletableFuture<JSONArray> future = new CompletableFuture<>();
        Request request = new Request.Builder()
                .url("http://api.opentripmap.com/0.1/ru/places/radius?radius=" + RADIUS +
                        "&lon=" + locationInfo.getCoords().getLongitude() +
                        "&lat=" + locationInfo.getCoords().getLatitude() +
                        "&apikey=" + APIkey)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String body = response.body().string();
                future.complete(new JSONObject(body).getJSONArray("features"));
            }
            public void onFailure(Call call, IOException e) {
                System.out.println("Не удалось получить список интересных мест");
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
