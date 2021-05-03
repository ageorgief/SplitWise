package bg.sofia.uni.fmi.mjt.server.cache;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import bg.sofia.uni.fmi.mjt.server.exception.CacheInitializationException;
import bg.sofia.uni.fmi.mjt.server.exception.CacheSaveException;
import bg.sofia.uni.fmi.mjt.server.model.Friendship;
import bg.sofia.uni.fmi.mjt.server.model.Group;
import bg.sofia.uni.fmi.mjt.server.model.Payment;
import bg.sofia.uni.fmi.mjt.server.model.User;
import bg.sofia.uni.fmi.mjt.server.storage.FriendshipStorage;
import bg.sofia.uni.fmi.mjt.server.storage.GroupStorage;
import bg.sofia.uni.fmi.mjt.server.storage.UserStorage;

public class CacheTest {
    private Cache cache;
    private UserStorage userStorage;
    private GroupStorage groupStorage;
    private FriendshipStorage friendshipStorage;

    @Before
    public void initialize() throws CacheInitializationException, IOException {
        userStorage = mock(UserStorage.class);
        when(userStorage.getUsers()).thenReturn(new HashMap<String, User>());
        groupStorage = mock(GroupStorage.class);
        when(groupStorage.getGroups()).thenReturn(new HashMap<String, Group>());
        friendshipStorage = mock(FriendshipStorage.class);
        when(friendshipStorage.getFriendships()).thenReturn(new HashMap<String, Friendship>());
        cache = new Cache(userStorage, groupStorage, friendshipStorage);

    }

    @Test()
    public void testSaveUserInternalMethodIsCalledAndUserIsReturned() throws CacheSaveException, IOException {
        User user = new User("Username", "Password");

        cache.saveUser(user);

        verify(userStorage).saveUser(user);

        assertEquals(user, cache.getUser(user.getUsername()));
    }

    @Test()
    public void testSaveGroupMethodIsCalledAndGroupIsReturned() throws CacheSaveException, IOException {
        String user1 = "User1";
        String user2 = "User2";
        String user3 = "User3";

        Set<Payment> payments = new HashSet<>();
        Set<String> groupMembers = new HashSet<>();

        groupMembers.add(user1);
        groupMembers.add(user2);
        groupMembers.add(user3);

        Group group = new Group("Group", groupMembers, payments);

        cache.saveGroup(group);

        verify(groupStorage).saveGroup(group);

        assertEquals(group, cache.getGroup(group.getName()));
    }

    @Test()
    public void testSaveFriendshipMethodIsCalledAndGroupIsReturned() throws CacheSaveException, IOException {
        String user1 = "User1";
        String user2 = "User2";
        String friendshipName = "Friendship";

        Friendship friendship = new Friendship(friendshipName, user1, user2);

        cache.saveFriendship(friendship);

        verify(friendshipStorage).saveFriendship(friendship);

        assertEquals(friendship, cache.getFriendship(friendship.getName()));
    }

}
