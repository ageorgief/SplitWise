package bg.sofia.uni.fmi.mjt.server.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class User implements Serializable {
    private static final long serialVersionUID = -9008404894659955717L;

    private final String username;
    private final String password;
    private final Set<String> groupNames;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        groupNames = new HashSet<>();
    }

    public User(String username, String password, Set<String> groupNames) {
        this.username = username;
        this.password = password;
        this.groupNames = groupNames;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getGroupNames() {
        return new HashSet<>(groupNames);
    }

    public void addGroupName(String groupName) {
        groupNames.add(groupName);
    }
}
