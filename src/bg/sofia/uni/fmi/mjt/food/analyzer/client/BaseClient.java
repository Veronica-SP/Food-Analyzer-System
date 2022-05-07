package bg.sofia.uni.fmi.mjt.food.analyzer.client;

import bg.sofia.uni.fmi.mjt.food.analyzer.formatter.ServerResponseFormatter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseClient implements Client {
    private static final int BUFFER_SIZE = 65_536;
    private static final String LOGS_FILE_NAME = "client-logs.txt";

    private static final String SERVER_CONNECTION_ERROR_MESSAGE =
            "Unable to connect to the server.";
    private static final String SERVER_CONNECTION_ERROR_MESSAGE_USER =
            "For more information please refer to the logs in %s.";

    private final InetSocketAddress socketAddress;
    private final ServerResponseFormatter formatter;
    private final Logger logger;

    private final String welcomeCommand;
    private final String prompt;
    private final String disconnectCommand;

    private ByteBuffer buffer;

    protected BaseClient(InetSocketAddress socketAddress, ServerResponseFormatter formatter,
                         String welcomeCommand, String prompt, String disconnectCommand) {
        this.socketAddress = socketAddress;
        this.formatter = formatter;
        this.logger = Logger.getLogger(BaseClient.class.getCanonicalName());

        this.welcomeCommand = welcomeCommand;
        this.prompt = prompt;
        this.disconnectCommand = disconnectCommand;

    }

    @Override
    public void run() {
        start();
    }

    @Override
    public void start() {
        try (SocketChannel channel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            logger.addHandler(new FileHandler(LOGS_FILE_NAME));
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            channel.connect(socketAddress);

            System.out.println(welcomeCommand);

            while (true) {
                System.out.print(prompt);
                String request = scanner.nextLine();

                if (request.isBlank()) {
                    continue;
                }

                sendRequest(request, channel);

                String response = getResponse(channel);
                String formattedResponse = formatter.format(request, response);
                System.out.println(formattedResponse);

                if (disconnectCommand.equalsIgnoreCase(request)) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, SERVER_CONNECTION_ERROR_MESSAGE, e);
            System.out.println(SERVER_CONNECTION_ERROR_MESSAGE + " " +
                    SERVER_CONNECTION_ERROR_MESSAGE_USER.formatted(LOGS_FILE_NAME));
        }
    }

    private void sendRequest(String request, SocketChannel channel) throws IOException {
        buffer.clear();
        buffer.put(request.getBytes());

        buffer.flip();
        channel.write(buffer);
    }

    private String getResponse(SocketChannel channel) throws IOException {
        buffer.clear();
        channel.read(buffer);

        buffer.flip();
        return new String(buffer.array(), 0, buffer.limit(), StandardCharsets.UTF_8);
    }
}
