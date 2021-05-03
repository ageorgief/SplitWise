package bg.sofia.uni.fmi.mjt.server.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.exception.CacheSaveException;
import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.exception.ServerInternalProblemException;
import bg.sofia.uni.fmi.mjt.server.model.Group;
import bg.sofia.uni.fmi.mjt.server.model.User;

public class CreateGroupTest {
    private Command createGroup;
    private Cache serverCache;

    @Before
    public void initialize() {
        serverCache = mock(Cache.class);
        createGroup = new CreateGroup(serverCache);
    }

    @Test(expected = CommandInvalidArgumentsException.class)
    public void testExecuteWithEmptyList() throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of();
        createGroup.execute(arguments);
    }

    @Test(expected = CommandInvalidArgumentsException.class)
    public void testExecuteWithAlreadyExistingGroup()
            throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of("groupname", "user1", "user2", "user3", "user4");
        Set<String> members = new HashSet<>();

        Group group = new Group(arguments.get(0), members);

        when(serverCache.existsGroup(group.getName())).thenReturn(true);
        when(serverCache.existsUser(arguments.get(1))).thenReturn(true);
        when(serverCache.existsUser(arguments.get(2))).thenReturn(true);
        when(serverCache.existsUser(arguments.get(3))).thenReturn(true);

        createGroup.execute(arguments);
    }

    @Test(expected = CommandInvalidArgumentsException.class)
    public void testExecuteWithMemebersOfGroupThatAreNotExistingUsers()
            throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of("groupname", "user1", "user2", "user3", "user4");
        Set<String> members = new HashSet<>();

        Group group = new Group(arguments.get(0), members);

        when(serverCache.existsGroup(group.getName())).thenReturn(false);
        when(serverCache.existsUser(any(String.class))).thenReturn(false);

        createGroup.execute(arguments);
    }

    @Test
    public void testExecuteWithCorrectArguments()
            throws ServerInternalProblemException, CommandInvalidArgumentsException, CacheSaveException {
        List<String> arguments = List.of("groupname", "user1", "user2", "user3", "user4");
        Set<String> members = new HashSet<>();

        User user1 = new User(arguments.get(1), "password");
        User user2 = new User(arguments.get(2), "password");
        User user3 = new User(arguments.get(3), "password");
        User user4 = new User(arguments.get(4), "password");

        when(serverCache.getUser(user1.getUsername())).thenReturn(user1);
        when(serverCache.getUser(user2.getUsername())).thenReturn(user2);
        when(serverCache.getUser(user3.getUsername())).thenReturn(user3);
        when(serverCache.getUser(user4.getUsername())).thenReturn(user4);

        Group group = new Group(arguments.get(0), members);

        when(serverCache.existsGroup(group.getName())).thenReturn(false);
        when(serverCache.existsUser(any(String.class))).thenReturn(true);

        String expectedResult = group.getName().concat(" created successfully");
        String result = createGroup.execute(arguments);

        verify(serverCache).saveGroup(any(Group.class));
        assertEquals(expectedResult, result);
    }

}