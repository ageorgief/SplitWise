package bg.sofia.uni.fmi.mjt.server.storage;

import bg.sofia.uni.fmi.mjt.server.model.Payment;
import bg.sofia.uni.fmi.mjt.server.model.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class UserStorage {
    private static final Gson GSON = new Gson();
    private static final String USERS_DIRECTORY = "users";
    private static final String USER_PAYMENTS_FILE = "payments.txt";

    public UserStorage() throws IOException {
        Path usersDirectory = Paths.get(USERS_DIRECTORY);

        if (Files.notExists(usersDirectory)) {
            Files.createDirectory(usersDirectory);
        }
    }

    public Map<String, User> getUsers() throws IOException {
        Path usersDirectory = Paths.get(USERS_DIRECTORY);
        Map<String, User> users = new HashMap<>();

        try (DirectoryStream<Path> usersDirectories = Files.newDirectoryStream(usersDirectory)) {
            for (Path userDirectory : usersDirectories) {
                Path userFile = Paths.get(userDirectory.toString(), userDirectory.getFileName().toString());
                String userJson = Files.readString(userFile);
                User user = GSON.fromJson(userJson, User.class);
                users.put(user.getUsername(), user);
            }
        }

        return users;
    }

    public void saveUser(User user) throws IOException {
        Path userDirectory = getUserDirectory(user);
        Path userFile = Paths.get(userDirectory.toString(), user.getUsername());
        String userInfo = GSON.toJson(user);
        Files.writeString(userFile, userInfo);
    }

    private Path getUserDirectory(User user) throws IOException {
        String username = user.getUsername();
        Path userDirectory = Paths.get(USERS_DIRECTORY, username);

        if (Files.notExists(userDirectory)) {
            Files.createDirectory(userDirectory);
        }

        return userDirectory;
    }

    public void saveUserPayment(User user, Payment payment) throws IOException {
        Path userDirectory = getUserDirectory(user);
        Path userPaymentsFile = Paths.get(userDirectory.toString(), USER_PAYMENTS_FILE);
        String paymentJson = GSON.toJson(payment).concat(System.lineSeparator());
        OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE};
        Files.writeString(userPaymentsFile, paymentJson, options);
    }

}
