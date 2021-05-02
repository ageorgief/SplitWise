package bg.sofia.uni.fmi.mjt.server.commands;

import java.util.List;

import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.model.User;

public class Login implements Command {
    private Cache serverCache;

    public Login(Cache serverCache) {
        this.serverCache = serverCache;
    }

    @Override
    public String execute(List<String> arguments) throws CommandInvalidArgumentsException {
        validateArguments(arguments);

        String username = arguments.get(0);
        String password = arguments.get(1);

        if (!serverCache.existsUser(username)) {
            throw new CommandInvalidArgumentsException(username.concat(" does not exists"));
        }

        User user = serverCache.getUser(username);

        if (!user.getPassword().equals(password)) {
            throw new CommandInvalidArgumentsException("wrong password");
        }

        return "Login successful!";
    }

    private void validateArguments(List<String> arguments) throws CommandInvalidArgumentsException {
        int commandArgumentsCount = 2;

        if (arguments.size() != commandArgumentsCount) {
            throw new CommandInvalidArgumentsException("Command accepts only 2 arguments");
        }
    }

}