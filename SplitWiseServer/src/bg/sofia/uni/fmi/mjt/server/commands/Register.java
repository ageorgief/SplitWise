package bg.sofia.uni.fmi.mjt.server.commands;

import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.exception.CacheSaveException;
import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.exception.ServerInternalProblemException;
import bg.sofia.uni.fmi.mjt.server.model.User;

import java.util.List;

public class Register implements Command {
    private final Cache serverCache;

    public Register(Cache serverCache) {
        this.serverCache = serverCache;
    }

    @Override
    public String execute(List<String> arguments) throws CommandInvalidArgumentsException, ServerInternalProblemException {
        validateArguments(arguments);

        String username = arguments.get(0);
        String password = arguments.get(1);
        User user = new User(username, password);

        try {
            serverCache.saveUser(user);
        } catch (CacheSaveException e) {
            throw new ServerInternalProblemException(e.getMessage(), e);
        }

        return "Registration successful!";
    }

    //TODO: validate username and password strings
    private void validateArguments(List<String> arguments) throws CommandInvalidArgumentsException {
        int commandArgumentsCount = 2;

        if (arguments.size() != commandArgumentsCount) {
            throw new CommandInvalidArgumentsException("Command accepts only 2 arguments");
        }

        String username = arguments.get(0);

        if (serverCache.existsUser(username)) {
            throw new CommandInvalidArgumentsException(username.concat(" already exists"));
        }
    }

}
