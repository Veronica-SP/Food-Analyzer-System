package bg.sofia.uni.fmi.mjt.food.analyzer.client;

public interface Client extends Runnable {

    /**
     * Establishes a connection between the client and the server.
     * Receives user requests, sends them to the server and provides the server's response.
     * Runs until a disconnect command is received.
     */
    void start();
}
