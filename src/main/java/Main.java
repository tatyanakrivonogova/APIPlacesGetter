import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String ... args) throws ExecutionException, InterruptedException {
        LocationSet locationSet = new LocationSet(new HashMap<>());
        //CompletableFuture<Void> completableFuture = CompletableFuture.runAsync()
        //locGetter.makeRequest("Цветной проезд");

        CompletableFuture<Void> completableFuture = CompletableFuture
                .runAsync(new LocationGetter(locationSet))
                        .thenRun(new WeatherGetter(locationSet));

        completableFuture.get();
    }
}
