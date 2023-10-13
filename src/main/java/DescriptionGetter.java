import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class DescriptionGetter {
    private static final String APIkey = "5ae2e3f221c38a28845f05b61fa85b73872df08065f37412956e8708";

    public static CompletableFuture<String> getDescription(JSONObject feature) {
        CompletableFuture<String> future = new CompletableFuture<>();
        OkHttpClient client = new OkHttpClient();

//        String place = feature.getJSONObject("properties").getString("name");
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
                //System.out.println(body);
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
        //System.out.println(body);

        JSONObject place = new JSONObject(body);
        //System.out.println(place);
        StringBuilder description = new StringBuilder();
        if (place.has("name")) {
            description.append(place.getString("name"));
        } else {
            return "";
        }
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

        //System.out.println(description);
        return description.toString();
    }
}
