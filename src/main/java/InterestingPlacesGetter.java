import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class InterestingPlacesGetter {
    private final LocationInfo locationInfo;
    private static final int RADIUS = 500;
    private static final String APIkey = "5ae2e3f221c38a28845f05b61fa85b73872df08065f37412956e8708";
    public InterestingPlacesGetter(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }
    public CompletableFuture<JSONArray> run() {
        CompletableFuture<JSONArray> future = new CompletableFuture<>();
        getPlaces(future);
        return future;
    }

    private void getPlaces(CompletableFuture<JSONArray> future) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.opentripmap.com/0.1/ru/places/radius?radius=" + RADIUS +
                        "&lon=" + locationInfo.getCoords().getLongitude() +
                        "&lat=" + locationInfo.getCoords().getLatitude() +
                        "&apikey=" + APIkey)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                future.complete(new JSONObject(body).getJSONArray("features"));
            }
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
    }
}
