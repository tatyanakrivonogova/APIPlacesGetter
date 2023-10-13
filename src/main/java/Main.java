import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String ... args) {
        LocationGetter locationGetter = new LocationGetter();

        CompletableFuture<LocationSet> locationsFuture = locationGetter.run();
        CompletableFuture<LocationInfo> selectedLocationFuture = locationsFuture.thenApply(locationGetter::chooseLocation);

        CompletableFuture<String> weatherFuture = selectedLocationFuture.thenCompose(selectedLocation ->
                new WeatherGetter(selectedLocation).run());


        CompletableFuture<JSONArray> placesFuture = selectedLocationFuture.thenCompose(selectedLocation ->
                new InterestingPlacesGetter(selectedLocation).run());

        CompletableFuture<String> descriptionsFuture = placesFuture.thenCompose(places -> {
            List<CompletableFuture<String>> futureList = new ArrayList<>();
            for (Object o: places) {
                futureList.add(DescriptionGetter.getDescription((JSONObject) o));
            }
            StringBuilder descriptions = new StringBuilder();
            CompletableFuture<String> descr = new CompletableFuture<>();
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
            allFutures.thenRun(() -> {
                for (CompletableFuture<String> future : futureList) {
                    try {
                        String description = future.get();
                        if (!description.isEmpty()) {
                            //System.out.println("+++" + description);
                            descriptions.append(description).append('\n');
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                descr.complete(descriptions.toString());
            });
            return descr;
        });

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(weatherFuture, descriptionsFuture);
        combinedFuture.thenRun(() -> {
            try {
                String weather = weatherFuture.get();
                String descriptions = descriptionsFuture.get();
                System.out.println('\t' + "Погода: " + '\n' + weather);
                System.out.println('\t' + "Интересные места: " + '\n' + descriptions);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        //System.out.println("Finish");
    }
}
