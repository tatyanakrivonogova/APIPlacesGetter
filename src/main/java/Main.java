import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String ... args) throws ExecutionException, InterruptedException {
        LocationSet locationSet = new LocationSet(new HashMap<>());
        //CompletableFuture<Void> completableFuture = CompletableFuture.runAsync()
        //locGetter.makeRequest("Цветной проезд");

//        CompletableFuture<Void> completableFuture = CompletableFuture
//                .runAsync(new LocationGetter(locationSet))
//                        .thenRun(new WeatherGetter(locationSet));
        LocationGetter locationGetter = new LocationGetter(locationSet);
        CompletableFuture<Integer> completableFuture = locationGetter.run();
        completableFuture.thenAccept(selectedLocation -> new WeatherGetter(locationSet, selectedLocation).run());
        completableFuture.thenAccept(selectedLocation -> new InterestingPlacesGetter(locationSet, selectedLocation).run());

        completableFuture.get();
        System.out.println("Finish");
    }
}
