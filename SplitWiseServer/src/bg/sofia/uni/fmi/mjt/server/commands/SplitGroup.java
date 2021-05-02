package bg.sofia.uni.fmi.mjt.server.commands;

import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.exception.CacheSaveException;
import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.exception.ServerInternalProblemException;
import bg.sofia.uni.fmi.mjt.server.model.Group;
import bg.sofia.uni.fmi.mjt.server.model.User;

import java.math.BigDecimal;
import java.util.List;

public class SplitGroup implements Command {
    private Cache serverCache;

    public SplitGroup(Cache serverCache) {
        this.serverCache = serverCache;
    }

    @Override
    public String execute(List<String> arguments) throws CommandInvalidArgumentsException, ServerInternalProblemException {
        validateArguments(arguments);

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(arguments.get(0)));
        String groupName = arguments.get(1);
        String username = arguments.get(2);

        Group group = serverCache.getGroup(groupName);
        User user = serverCache.getUser(username);
        group.splitAmount(user, amount);

        try {
            serverCache.saveGroup(group);
        } catch (CacheSaveException e) {
            throw new ServerInternalProblemException(e.getMessage(), e);
        }

        return "Split created successfully!";
    }

    private void validateArguments(List<String> arguments) throws CommandInvalidArgumentsException {
        int minCommandArgumentsCount = 3;

        if (arguments.size() < minCommandArgumentsCount) {
            throw new CommandInvalidArgumentsException("Command requires at least " + minCommandArgumentsCount + " arguments");
        }

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(arguments.get(0)));

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CommandInvalidArgumentsException("Amount should be positive");
        }

        String groupName = arguments.get(1);
        String username = arguments.get(2);

        if (!serverCache.existsGroup(groupName)) {
            throw new CommandInvalidArgumentsException(groupName.concat(" does not exists!"));
        }

        if (!serverCache.existsUser(username)) {
            throw new CommandInvalidArgumentsException(username.concat(" does not exists"));
        }

        Group group = serverCache.getGroup(groupName);
        if (!group.getMembers().contains(username)) {
            throw new CommandInvalidArgumentsException(username.concat(" is not in the group"));
        }
    }
}
