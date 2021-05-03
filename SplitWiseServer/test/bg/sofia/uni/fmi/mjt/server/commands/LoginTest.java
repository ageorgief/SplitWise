package bg.sofia.uni.fmi.mjt.server.commands;

import static org.junit.Assert.assertEquals;
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
import bg.sofia.uni.fmi.mjt.server.model.User;

public class LoginTest {
    private Command login;
    private Cache cache;

    @Before
    public void initialize() {
        cache = mock(Cache.class);
        login = new Login(cache);
    }

    @Test(expected = CommandInvalidArgumentsException.class)
    public void testExecuteWithEmptyList()
            throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of();
        login.execute(arguments);
    }

    @Test
    public void testExecuteWithCorrectUsernameAndPassword()
            throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of("username", "password");

        User user = new User(arguments.get(0), arguments.get(1));

        when(cache.existsUser(user.getUsername())).thenReturn(true);
        when(cache.getUser(user.getUsername())).thenReturn(user);

        String expectedResult = "Login successful!";
        String result = login.execute(arguments);

        verify(cache).getUser(user.getUsername());

        assertEquals(expectedResult, result);
    }

    @Test(expected = CommandInvalidArgumentsException.class)
    public void testExecuteWithNotExistingUser()
            throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of("username", "password");

        User user = new User(arguments.get(0), arguments.get(1));

        when(cache.existsUser(user.getUsername())).thenReturn(false);

        login.execute(arguments);
    }

    @Test(expected = CommandInvalidArgumentsException.class)
    public void testExecuteWithNotValidUsernameAndWrongPassword()
            throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of("username", "password");
        String wrongPassword = "Wrong password";

        User user = new User(arguments.get(0), wrongPassword);

        when(cache.existsUser(user.getUsername())).thenReturn(true);
        when(cache.getUser(user.getUsername())).thenReturn(user);

        when(cache.existsUser(user.getUsername())).thenReturn(true);

        login.execute(arguments);
    }

}
