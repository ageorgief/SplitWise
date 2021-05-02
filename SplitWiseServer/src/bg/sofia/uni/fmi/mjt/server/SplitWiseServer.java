package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.commands.Command;
import bg.sofia.uni.fmi.mjt.server.commands.CommandFactory;
import bg.sofia.uni.fmi.mjt.server.exception.CacheInitializationException;
import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.exception.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.server.exception.ServerInternalProblemException;
import bg.sofia.uni.fmi.mjt.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.server.parser.RequestParser;
import bg.sofia.uni.fmi.mjt.server.storage.FriendshipStorage;
import bg.sofia.uni.fmi.mjt.server.storage.GroupStorage;
import bg.sofia.uni.fmi.mjt.server.storage.UserStorage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class SplitWiseServer {
    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8080;

    private static final Logger LOGGER = new Logger(System.out);

    private CommandFactory commandFactory;
    private ByteBuffer inputBuffer;
    private RequestParser parser;
    private Map<SocketChannel, String> loggedUsers;

    public SplitWiseServer() throws CacheInitializationException, IOException {
        UserStorage userStorage = new UserStorage();
        GroupStorage groupStorage = new GroupStorage();
        FriendshipStorage friendshipStorage = new FriendshipStorage();
        Cache serverCache = new Cache(userStorage, groupStorage, friendshipStorage);
        commandFactory = new CommandFactory(serverCache);
        parser = new RequestParser();
        inputBuffer = ByteBuffer.allocate(1024);
        loggedUsers = new HashMap<>();
    }

    public static void main(String[] args) {
        try {
            SplitWiseServer server = new SplitWiseServer();
            server.start();
        } catch (CacheInitializationException | IOException | InterruptedException e) {
            LOGGER.log(e);
        }
    }

    public void start() throws IOException, InterruptedException {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int readyChannels = selector.select();

                if (readyChannels == 0) {
                    int sleepMillis = 500;
                    Thread.sleep(sleepMillis);
                }

                handleReadyChannels(selector);
            }
        }
    }

    private void handleReadyChannels(Selector selector) {
        Set<SelectionKey> selectionKey = selector.selectedKeys();
        Iterator<SelectionKey> selectionKeyIterator = selectionKey.iterator();

        while (selectionKeyIterator.hasNext()) {
            SelectionKey key = selectionKeyIterator.next();

            if (key.isReadable()) {
                SocketChannel client = (SocketChannel) key.channel();
                String clientRequest = getRequestFromClient(client);
                String response = handleClientRequest(client, clientRequest);
                sendResponse(client, response);
            } else if (key.isAcceptable()) {
                ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
                try {
                    SocketChannel client = socketChannel.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                } catch (IOException e) {
                    LOGGER.log(e);
                }
            }

            selectionKeyIterator.remove();
        }
    }

    private String getRequestFromClient(SocketChannel client) {
        StringBuilder command = new StringBuilder();
        int readBytes = 1;

        try {
            while (readBytes > 0) {
                inputBuffer.clear();
                readBytes = client.read(inputBuffer);
                inputBuffer.flip();
                if (readBytes > 0) {
                    byte[] cmd = new byte[readBytes];
                    inputBuffer.get(cmd);
                    command.append(new String(cmd));
                }
            }
        } catch (IOException e) {
            String username = loggedUsers.get(client);
            LOGGER.log(username, e);
        }

        return command.toString();
    }

    private String handleClientRequest(SocketChannel client, String clientRequest) {
        try {
            String commandName = parser.getCommand(clientRequest);
            List<String> commandArguments = parser.getArguments(clientRequest);
            Command command = commandFactory.make(commandName);

            if (commandName.equals("register")) {
                if (loggedUsers.get(client) != null) {
                    return "You can't register while you are logged in!";
                }

                return command.execute(commandArguments);
            }

            if (commandName.equals("login")) {
                if (loggedUsers.get(client) != null) {
                    return "You are already logged in!";
                }

                String result = command.execute(commandArguments);
                String username = commandArguments.get(0);
                loggedUsers.put(client, username);
                return result;
            }

            String user = loggedUsers.get(client);

            if (user == null) {
                return "Please login to your account";
            }

            commandArguments.add(user);
            return command.execute(commandArguments);
        } catch (ServerInternalProblemException | CommandInvalidArgumentsException | InvalidCommandException e) {
            String username = loggedUsers.get(client);
            LOGGER.log(username, e);
            return e.getMessage();
        }
    }

    private void sendResponse(SocketChannel client, String message) {
        message = message + System.lineSeparator();
        byte[] messageInBytes = message.getBytes();

        ByteBuffer response = ByteBuffer.allocate(messageInBytes.length);
        response.put(messageInBytes);
        response.flip();
        try {
            client.write(response);
        } catch (IOException e) {
            String username = loggedUsers.get(client);
            LOGGER.log(username, e);
            try {
                client.close();
            } catch (IOException e1) {
                LOGGER.log(username, e1);
            }
        }
    }

}
