import bg.sofia.uni.fmi.mjt.food.analyzer.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.food.analyzer.client.Client;
import bg.sofia.uni.fmi.mjt.food.analyzer.client.FoodDataClient;
import bg.sofia.uni.fmi.mjt.food.analyzer.finder.FoodDataCentralAPI;
import bg.sofia.uni.fmi.mjt.food.analyzer.finder.FoodDataFinder;
import bg.sofia.uni.fmi.mjt.food.analyzer.formatter.FoodResponseFormatter;
import bg.sofia.uni.fmi.mjt.food.analyzer.server.FoodDataServer;
import bg.sofia.uni.fmi.mjt.food.analyzer.server.Server;

import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.file.Path;

public class Main {
    private static final String TEST_FILE_NAME = "test.txt";
    private static final int TEST_PORT = 4444;

    public static void main(String[] args) throws InterruptedException {
        FoodDataFinder foodDataFinder = new FoodDataCentralAPI(HttpClient.newHttpClient(),
                "YpQpF0A4Se7bwD7p80rWxR4kOppcdTD0gklT39RC");

        FoodCache foodCache = new FoodCache(foodDataFinder, Path.of(TEST_FILE_NAME));

        Server foodDataServer = new FoodDataServer(new InetSocketAddress(TEST_PORT), foodCache);
        Thread serverThread = new Thread(foodDataServer);
        serverThread.start();

        Client foodDataClient = new FoodDataClient(new InetSocketAddress(TEST_PORT), new FoodResponseFormatter());
        Thread clientThread = new Thread(foodDataClient);
        clientThread.start();

        clientThread.join();

        foodDataServer.stop();
    }
}
