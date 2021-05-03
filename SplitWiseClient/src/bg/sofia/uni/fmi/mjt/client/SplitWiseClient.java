package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.client.request.ClientRequestHandler;
import bg.sofia.uni.fmi.mjt.client.response.ServerResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

public class SplitWiseClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final String ENCODING = "UTF-8";

    private final Runnable clientRequestHandler;
    private final Runnable serverResponseHandler;

    public SplitWiseClient(SocketChannel socketChannel, InputStream input, OutputStream output) {
        clientRequestHandler = new ClientRequestHandler(input, Channels.newWriter(socketChannel, ENCODING));
        serverResponseHandler = new ServerResponseHandler(Channels.newReader(socketChannel, ENCODING), output);
    }

    public void run() {
        new Thread(serverResponseHandler).start();
        clientRequestHandler.run();
    }

    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            SplitWiseClient client = new SplitWiseClient(socketChannel, System.in, System.out);

            client.run();
        } catch (IOException e) {
            System.out.println("Could not connect to server.");
            System.out.println("Please try again later!");
        }
    }

}