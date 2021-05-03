package bg.sofia.uni.fmi.mjt.server.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.exception.CacheSaveException;
import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.exception.ServerInternalProblemException;
import bg.sofia.uni.fmi.mjt.server.model.Friendship;
import bg.sofia.uni.fmi.mjt.server.model.User;

public class AddFriendTest {
    private Command addFriend;
    private Cache serverCache;

    @Before
    public void initialize() {
        serverCache = mock(Cache.class);
        addFriend = new AddFriend(serverCache);
    }

    @Test(expected = CommandInvalidArgumentsException.class)
    public void testExecuteWithEmptyList() throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of();
        addFriend.execute(arguments);
    }

    @Test(expected = CommandInvalidArgumentsException.class)
    public void testExecuteWithNotExistingUser()
            throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of("username", "username1");

        User user = new User(arguments.get(0), "password");

        addFriend.execute(arguments);
    }

    @Test(expected = CommandInvalidArgumentsException.class)
    public void testExecuteWithUserThatIsAlreadyFriend()
            throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of("username", "username1");
        String username = arguments.get(0);
        String username1 = arguments.get(1);

        User user = new User(username, "password");
        User user1 = new User(username1, "password");

        String groupName = "username-username1";
        user1.addGroupName(groupName);

        when(serverCache.existsUser(user.getUsername())).thenReturn(true);
        when(serverCache.getUser(username1)).thenReturn(user1);
        when(serverCache.getUser(username)).thenReturn(user);

        addFriend.execute(arguments);
    }

    @Test
    public void testExecuteWithCorrectArguments()
            throws ServerInternalProblemException, CommandInvalidArgumentsException, CacheSaveException {
        List<String> arguments = List.of("username", "username1");
        String username = arguments.get(0);
        String username1 = arguments.get(1);

        User user = new User(username, "password");
        User user1 = new User(username1, "password");

        when(serverCache.existsUser(user.getUsername())).thenReturn(true);
        when(serverCache.getUser(username1)).thenReturn(user1);
        when(serverCache.getUser(username)).thenReturn(user);

        String expectedResult = arguments.get(0).concat(" has been added as a friend!");
        String result = addFriend.execute(arguments);

        verify(serverCache).saveFriendship(any(Friendship.class));
        assertEquals(expectedResult, result);
    }
}
