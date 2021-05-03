package bg.sofia.uni.fmi.mjt.client.request;

import java.io.*;

public class ClientRequestHandler implements Runnable {
    private InputStream input;
    private Writer output;

    public ClientRequestHandler(InputStream input, Writer output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        try (var reader = new BufferedReader(new InputStreamReader(input));
             var writer = new PrintWriter(output, true)) {
            //TODO: add break point for loop
            while (true) {
                String clientRequest = reader.readLine();
                writer.println(clientRequest);
            }
        } catch (IOException e) {
            System.out.println("Problem occurred while sending request!");
            System.out.println("Please refresh your connection");
        }
    }

}
