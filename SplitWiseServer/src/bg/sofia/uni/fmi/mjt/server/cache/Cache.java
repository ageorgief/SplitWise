package bg.sofia.uni.fmi.mjt.server.cache;

import java.io.IOException;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.server.exception.CacheInitializationException;
import bg.sofia.uni.fmi.mjt.server.exception.CacheSaveException;
import bg.sofia.uni.fmi.mjt.server.model.Friendship;
import bg.sofia.uni.fmi.mjt.server.model.Group;
import bg.sofia.uni.fmi.mjt.server.model.Payment;
import bg.sofia.uni.fmi.mjt.server.model.User;
import bg.sofia.uni.fmi.mjt.server.storage.FriendshipStorage;
import bg.sofia.uni.fmi.mjt.server.storage.GroupStorage;
import bg.sofia.uni.fmi.mjt.server.storage.UserStorage;

public class Cache {
    private UserStorage userStorage;
    private GroupStorage groupStorage;
    private FriendshipStorage friendshipStorage;
    private Map<String, User> users;
    private Map<String, Group> groups;
    private Map<String, Friendship> friendships;

    public Cache(UserStorage userStorage, GroupStorage groupStorage, FriendshipStorage friendshipStorage)
            throws CacheInitializationException {
        this.userStorage = userStorage;
        this.groupStorage = groupStorage;
        this.friendshipStorage = friendshipStorage;
        try {
            users = userStorage.getUsers();
            groups = groupStorage.getGroups();
            friendships = friendshipStorage.getFriendships();
        } catch (IOException e) {
            throw new CacheInitializationException("Failed to initialize server cache", e);
        }
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public boolean existsUser(String username) {
        return users.containsKey(username);
    }

    public void saveUser(User user) throws CacheSaveException {
        users.put(user.getUsername(), user);
        try {
            userStorage.saveUser(user);
        } catch (IOException e) {
            throw new CacheSaveException("Could not save user", e);
        }
    }

    public Group getGroup(String groupName) {
        return groups.get(groupName);
    }

    public boolean existsGroup(String groupName) {
        return groups.containsKey(groupName);
    }

    public void saveGroup(Group group) throws CacheSaveException {
        groups.put(group.getName(), group);
        try {
            groupStorage.saveGroup(group);
        } catch (IOException e) {
            throw new CacheSaveException("Could not save group", e);
        }
    }

    public Friendship getFriendship(String friendshipName) {
        return friendships.get(friendshipName);
    }

    public boolean existsFriendship(String friendshipName) {
        return friendships.containsKey(friendshipName);
    }

    public void saveFriendship(Friendship friendship) throws CacheSaveException {
        friendships.put(friendship.getName(), friendship);
        try {
            friendshipStorage.saveFriendship(friendship);
        } catch (IOException e) {
            throw new CacheSaveException("Could not save friendship", e);
        }
    }

    public void saveUserPayment(User user, Payment payment) throws CacheSaveException {
        try {
            userStorage.saveUserPayment(user, payment);
        } catch (IOException e) {
            throw new CacheSaveException("Could not save payment", e);
        }
    }

}
