package bg.sofia.uni.fmi.mjt.server.storage;

import bg.sofia.uni.fmi.mjt.server.model.Group;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GroupStorage {
    private static final Gson GSON = new Gson();
    private static final String GROUPS_DIRECTORY = "groups";

    public GroupStorage() throws IOException {
        Path groupsDirectory = Paths.get(GROUPS_DIRECTORY);

        if (Files.notExists(groupsDirectory)) {
            Files.createDirectory(groupsDirectory);
        }
    }

    public Map<String, Group> getGroups() throws IOException {
        Map<String, Group> groups = new HashMap<>();
        Path groupsDirectory = Path.of(GROUPS_DIRECTORY);

        try (DirectoryStream<Path> groupsFiles = Files.newDirectoryStream(groupsDirectory)) {
            for (Path groupFile : groupsFiles) {
                String groupJson = Files.readString(groupFile);
                Group group = GSON.fromJson(groupJson, Group.class);
                groups.put(group.getName(), group);
            }
        }

        return groups;
    }

    public void saveGroup(Group group) throws IOException {
        Path groupFile = Paths.get(GROUPS_DIRECTORY, group.getName());
        String groupJson = GSON.toJson(group);
        Files.writeString(groupFile, groupJson);
    }

}
