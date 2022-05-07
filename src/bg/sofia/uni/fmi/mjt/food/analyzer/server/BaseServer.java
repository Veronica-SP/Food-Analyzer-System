package bg.sofia.uni.fmi.mjt.food.analyzer.server;

import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.UnableToPerformOpException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.InvalidRequestException;
import bg.sofia.uni.fmi.mjt.food.analyzer.exceptions.NoMatchesException;
import bg.sofia.uni.fmi.mjt.food.analyzer.request.RequestHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseServer implements Server {
    private static final int BUFFER_SIZE = 65_536;
    private static final String LOGS_FILE_NAME = "server-logs.txt";

    private static final String SERVER_ERROR_MESSAGE =
            "An unexpected error occurred with the server.";
    private static final String SERVER_ERROR_MESSAGE_USER =
            "For more information please refer to the logs in %s.";
    private static final String MORE_INFORMATION =
            "For more information on the supported commands use the command \"help\".";

    private static final int OK_CODE = 200;
    private static final int BAD_REQUEST_CODE = 400;
    private static final int NOT_FOUND_CODE = 404;
    private static final int SERVICE_UNAVAILABLE = 503;

    private final InetSocketAddress socketAddress;
    protected final RequestHandler requestHandler;
    private final Logger logger;

    private boolean shouldRun;
    private Selector selector;
    private ByteBuffer buffer;

    protected BaseServer(InetSocketAddress socketAddress, RequestHandler requestHandler) {
        this.socketAddress = socketAddress;
        this.requestHandler = requestHandler;
        this.logger = Logger.getLogger(BaseServer.class.getCanonicalName());

        this.shouldRun = true;
    }

    @Override
    public void run() {
        start();
    }

    @Override
    public void stop() {
        shouldRun = false;

        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    @Override
    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            logger.addHandler(new FileHandler(LOGS_FILE_NAME));

            selector = Selector.open();
            buffer = ByteBuffer.allocate(BUFFER_SIZE);

            serverSocketChannel.bind(socketAddress);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (shouldRun) {
                int count = selector.select();
                if (count == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectedKey = iterator.next();

                    if (selectedKey.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) selectedKey.channel();
                        acceptClient(channel);
                    }

                    if (selectedKey.isReadable()) {
                        SocketChannel channel = (SocketChannel) selectedKey.channel();
                        processClientRequest(channel);
                    }

                    iterator.remove();
                }
            }

            selector.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, SERVER_ERROR_MESSAGE, e);
            System.out.println(SERVER_ERROR_MESSAGE + " " +
                    SERVER_ERROR_MESSAGE_USER.formatted(LOGS_FILE_NAME));
        }
    }

    private void acceptClient(ServerSocketChannel channel) throws IOException {
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void processClientRequest(SocketChannel channel) throws IOException {
        String request = readRequest(channel);
        if (request == null || request.isBlank()) {
            return;
        }

        int responseCode;
        String responseBody;

        try {
            responseBody = requestHandler.execute(request);
            responseCode = OK_CODE;
        } catch (InvalidRequestException e) {
            responseCode = BAD_REQUEST_CODE;
            responseBody = e.getMessage() + " " + MORE_INFORMATION;
        } catch (NoMatchesException e) {
            responseCode = NOT_FOUND_CODE;
            responseBody = e.getMessage();
        } catch (UnableToPerformOpException e) {
            responseCode = SERVICE_UNAVAILABLE;
            responseBody = e.getMessage();
        }

        sendResponse(new ServerResponse(responseCode, responseBody), channel);
    }

    private String readRequest(SocketChannel channel) throws IOException {
        buffer.clear();

        int r = channel.read(buffer);
        if (r < 0) {
            channel.close();
            return null;
        }

        buffer.flip();
        return new String(buffer.array(), 0, buffer.limit(), StandardCharsets.UTF_8);
    }

    private void sendResponse(ServerResponse response, SocketChannel channel) throws IOException {
        Gson gson = new Gson();

        buffer.clear();
        buffer.put(gson.toJson(response).getBytes(StandardCharsets.UTF_8));

        buffer.flip();
        channel.write(buffer);
    }
}
