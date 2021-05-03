package bg.sofia.uni.fmi.mjt.response;

import java.io.*;

public class ServerResponseHandler implements Runnable {
    private Reader input;
    private OutputStream output;

    public ServerResponseHandler(Reader input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        try (var reader = new BufferedReader(input);
             var writer = new PrintStream(output)) {
            while (true) {
                String serverResponse = reader.readLine();

                if (serverResponse == null) {
                    break;
                }

                writer.println(serverResponse);
            }
        } catch (IOException e) {
            System.out.println("Problem occurred while reading server response!");
            System.out.println("Please refresh your connection!");
        }
    }
}
