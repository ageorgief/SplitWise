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
import bg.sofia.uni.fmi.mjt.server.model.User;

public class RegisterTest {
    private Cache serverCache;
    private Command register;

    @Before
    public void initialize() {
        serverCache = mock(Cache.class);
        register = new Register(serverCache);
    }

    @Test(expected = CommandInvalidArgumentsException.class)
    public void testExecuteWithEmptyList()
            throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of();
        register.execute(arguments);
    }

    @Test
    public void testExecuteWithCorrectUsernameAndPassword()
            throws ServerInternalProblemException, CommandInvalidArgumentsException, CacheSaveException {
        List<String> arguments = List.of("username", "password");

        String expectedResult = "Registration successful!";
        String result = register.execute(arguments);

        verify(serverCache).saveUser(any(User.class));
        assertEquals(expectedResult, result);
    }

    @Test(expected = CommandInvalidArgumentsException.class)
    public void testExecuteWithAlreadyRegisteredUsername()
            throws ServerInternalProblemException, CommandInvalidArgumentsException {
        List<String> arguments = List.of("username", "password");
        when(serverCache.existsUser("username")).thenReturn(true);
        register.execute(arguments);
    }

}
