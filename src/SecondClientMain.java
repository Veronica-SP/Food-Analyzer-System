import bg.sofia.uni.fmi.mjt.food.analyzer.client.Client;
import bg.sofia.uni.fmi.mjt.food.analyzer.client.FoodDataClient;
import bg.sofia.uni.fmi.mjt.food.analyzer.formatter.FoodResponseFormatter;

import java.net.InetSocketAddress;

public class SecondClientMain {
    public static void main(String[] args) throws InterruptedException {
        Client foodDataClient2 = new FoodDataClient(new InetSocketAddress(4444), new FoodResponseFormatter());
        Thread clientThread2 = new Thread(foodDataClient2);
        clientThread2.start();

        clientThread2.join();
    }
}
