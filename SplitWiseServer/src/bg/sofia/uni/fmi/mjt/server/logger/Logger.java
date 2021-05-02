package bg.sofia.uni.fmi.mjt.server.logger;

import java.io.*;

public class Logger {
    private static final String LOG_FILE = "log.txt";

    private final PrintStream output;

    public Logger(PrintStream output) {
        this.output = output;
    }

    public void log(Exception exception) {
        String message = exception.getMessage();
        log(exception, message);
    }

    private void log(Exception exception, String message) {
        output.println(message);

        try (var writer = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            exception.printStackTrace(writer);
        } catch (IOException e) {
            output.println(e.getMessage());
        }
    }

    public void log(String username, Exception exception) {
        if (username != null) {
            String message = "[" + username + "] " + exception.getMessage();
            log(exception, message);
        } else {
            log(exception);
        }
    }

}
