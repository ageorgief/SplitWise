package bg.sofia.uni.fmi.mjt.server.storage;

import bg.sofia.uni.fmi.mjt.server.model.Friendship;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FriendshipStorage {
    private static final Gson GSON = new Gson();
    private static final String FRIENDSHIPS_DIRECTORY = "friendships";

    public FriendshipStorage() throws IOException {
        Path friendshipDirectory = Paths.get(FRIENDSHIPS_DIRECTORY);

        if (Files.notExists(friendshipDirectory)) {
            Files.createDirectory(friendshipDirectory);
        }
    }

    public Map<String, Friendship> getFriendships() throws IOException {
        Map<String, Friendship> friendships = new HashMap<>();
        Path friendshipsDirectory = Path.of(FRIENDSHIPS_DIRECTORY);

        try (DirectoryStream<Path> friendshipsFiles = Files.newDirectoryStream(friendshipsDirectory)){
            for (Path friendshipFile : friendshipsFiles) {
                String groupJson = Files.readString(friendshipFile);
                Friendship friendship = GSON.fromJson(groupJson, Friendship.class);
                friendships.put(friendship.getName(), friendship);
            }
        }

        return friendships;
    }

    public void saveFriendship(Friendship friendship) throws IOException {
        Path friendshipFile = Paths.get(FRIENDSHIPS_DIRECTORY, friendship.getName());
        String friendshipJson = GSON.toJson(friendship);
        Files.writeString(friendshipFile, friendshipJson);
    }
}
