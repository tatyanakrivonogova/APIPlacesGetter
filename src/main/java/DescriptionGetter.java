import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class DescriptionGetter {
    private static final String APIkey = "5ae2e3f221c38a28845f05b61fa85b73872df08065f37412956e8708";
    private static final OkHttpClient client = new OkHttpClient();

    public static CompletableFuture<String> getDescription(JSONObject feature) {
        CompletableFuture<String> future = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url("http://api.opentripmap.com/0.1/ru/places/xid/" +
                        feature.getJSONObject("properties").getString("xid")+ "?apikey=" + APIkey
                )
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                if (body == null) return;
                future.complete(parseResponse(body));
            }
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    static String parseResponse(String body) {
        JSONObject place = new JSONObject(body);
        StringBuilder description = new StringBuilder();
        if (place.has("name") && !place.getString("name").isEmpty()) {
            description.append(place.getString("name"));
        } else {
            return "";
        }
        description.append(" (");
        if (place.has("kinds")) {
            description.append(place.getString("kinds"));
        }
        if (place.has("address")) {
            if (place.getJSONObject("address").has("road")) {
                description.append(", ");
                description.append(place.getJSONObject("address").getString("road"));
            }
            if (place.getJSONObject("address").has("house")) {
                description.append(", ");
                description.append(place.getJSONObject("address").getString("house"));
            }
        }
        description.append(")");

        return description.toString();
    }
}
