package bg.sofia.uni.fmi.mjt.food.analyzer.server;

import bg.sofia.uni.fmi.mjt.food.analyzer.cache.FoodCache;
import bg.sofia.uni.fmi.mjt.food.analyzer.request.FoodRequestHandler;

import java.net.InetSocketAddress;

public class FoodDataServer extends BaseServer {

    public FoodDataServer(InetSocketAddress address, FoodCache foodCache) {
        super(address, new FoodRequestHandler(foodCache));
    }

    @Override
    public void start() {
        super.start();
        ((FoodRequestHandler)requestHandler).storeCache();
    }
}
