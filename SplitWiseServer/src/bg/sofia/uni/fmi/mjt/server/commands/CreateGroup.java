package bg.sofia.uni.fmi.mjt.server.commands;

import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.exception.CacheSaveException;
import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.exception.ServerInternalProblemException;
import bg.sofia.uni.fmi.mjt.server.model.Group;
import bg.sofia.uni.fmi.mjt.server.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateGroup implements Command {
    private Cache serverCache;

    public CreateGroup(Cache serverCache) {
        this.serverCache = serverCache;
    }

    @Override
    public String execute(List<String> arguments)
            throws CommandInvalidArgumentsException, ServerInternalProblemException {
        validateArguments(arguments);

        String groupName = arguments.get(0);
        Set<String> members = arguments.stream().skip(1).collect(Collectors.toSet());
        Group group = new Group(groupName, members);

        try {
            serverCache.saveGroup(group);

            for (String member : members) {
                User user = serverCache.getUser(member);
                user.addGroupName(groupName);
                serverCache.saveUser(user);
            }
        } catch (CacheSaveException e) {
            throw new ServerInternalProblemException(e.getMessage(), e);
        }

        return groupName.concat(" created successfully");
    }

    private void validateArguments(List<String> arguments) throws CommandInvalidArgumentsException {
        int minCommandArgumentsCount = 4;

        if (arguments.size() < minCommandArgumentsCount) {
            throw new CommandInvalidArgumentsException(
                    "Command requires at least " + minCommandArgumentsCount + " arguments");
        }

        String groupName = arguments.get(0);

        if (serverCache.existsGroup(groupName)) {
            throw new CommandInvalidArgumentsException(groupName.concat(" already exists!"));
        }

        for (int index = 1; index < arguments.size(); index++) {
            String username = arguments.get(index);
            if (!serverCache.existsUser(username)) {
                throw new CommandInvalidArgumentsException(username.concat(" does not exists!"));
            }
        }
    }

}
