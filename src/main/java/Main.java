import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String ... args) {
        while (true) {
            LocationGetter locationGetter = new LocationGetter();

            CompletableFuture<LocationSet> locationsFuture = locationGetter.getLocations();
            CompletableFuture<LocationInfo> selectedLocationFuture = locationsFuture.thenApply(locationGetter::chooseLocation);

            CompletableFuture<String> weatherFuture = selectedLocationFuture.thenCompose(selectedLocation ->
                    new WeatherGetter(selectedLocation).getWeather());

            CompletableFuture<JSONArray> placesFuture = selectedLocationFuture.thenCompose(selectedLocation ->
                    new InterestingPlacesGetter(selectedLocation).getPlaces());

            CompletableFuture<String> descriptionsFuture = placesFuture.thenCompose(places -> {
                List<CompletableFuture<String>> descriptionsFutureList = new ArrayList<>();
                for (Object o : places) {
                    descriptionsFutureList.add(DescriptionGetter.getDescription((JSONObject) o));
                }
                StringBuilder stringDescriptions = new StringBuilder();
                CompletableFuture<String> descriptions = new CompletableFuture<>();
                CompletableFuture<Void> allFutures = CompletableFuture.allOf(descriptionsFutureList.toArray(new CompletableFuture[0]));
                allFutures.thenRun(() -> {
                    for (CompletableFuture<String> future : descriptionsFutureList) {
                        try {
                            String description = future.get();
                            if (!description.isEmpty()) {
                                stringDescriptions.append(description).append('\n');
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    descriptions.complete(stringDescriptions.toString());
                });
                return descriptions;
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
            try {
                combinedFuture.get();
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("Не удалось получить информацию о месте");
            }
        }

    }
}
