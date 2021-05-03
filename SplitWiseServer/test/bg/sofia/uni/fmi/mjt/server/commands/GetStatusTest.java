package bg.sofia.uni.fmi.mjt.server.commands;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.exception.ServerInternalProblemException;
import bg.sofia.uni.fmi.mjt.server.model.User;

public class GetStatusTest {
    private Command getStatus;
    private Cache serverCache;

    @Before
    public void initialize() {
        serverCache = mock(Cache.class);
        getStatus = new GetStatus(serverCache);
    }

    @Test(expected = CommandInvalidArgumentsException.class)
    public void testExecuteWithEmptyList() throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of();
        getStatus.execute(arguments);
    }

    @Test
    public void testExecuteWithEmptyStatus() throws CommandInvalidArgumentsException, ServerInternalProblemException {
        List<String> arguments = List.of("user");
        User user = new User(arguments.get(0), "password");
        when(serverCache.getUser(user.getUsername())).thenReturn(user);
        String expectedResult = "";
        String result = getStatus.execute(arguments);
    }

}
