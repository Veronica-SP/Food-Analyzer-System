package bg.sofia.uni.fmi.mjt.food.analyzer.server;

public interface Server extends Runnable {

    /**
     * Starts a multi-client server which listens for new client connections and
     * processes connected clients' requests simultaneously.
     * Runs until the server's stop method is called.
     */
    void start();

    /**
     * Stops the server.
     */
    void stop();
}
