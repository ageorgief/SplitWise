package bg.sofia.uni.fmi.mjt.server.commands;

import java.util.List;

import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.exception.CacheSaveException;
import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.exception.ServerInternalProblemException;
import bg.sofia.uni.fmi.mjt.server.model.Friendship;
import bg.sofia.uni.fmi.mjt.server.model.User;

public class AddFriend implements Command {
    private Cache serverCache;

    public AddFriend(Cache serverCache) {
        this.serverCache = serverCache;
    }

    @Override
    public String execute(List<String> arguments)
            throws CommandInvalidArgumentsException, ServerInternalProblemException {
        validateArguments(arguments);

        String username = arguments.get(0);
        String commandExecutorUsername = arguments.get(1);

        if (!serverCache.existsUser(username)) {
            throw new CommandInvalidArgumentsException(username.concat(" does not exists"));
        }

        User commandExecutor = serverCache.getUser(commandExecutorUsername);
        User user = serverCache.getUser(username);

        String groupName = createFriendshipName(username, commandExecutorUsername);

        if (commandExecutor.getGroupNames().contains(groupName)) {
            throw new CommandInvalidArgumentsException("You are already friends with ".concat(username));
        }

        Friendship friendship = new Friendship(groupName, commandExecutorUsername, username);

        try {
            serverCache.saveFriendship(friendship);

            commandExecutor.addGroupName(groupName);
            user.addGroupName(groupName);

            serverCache.saveUser(user);
            serverCache.saveUser(commandExecutor);
        } catch (CacheSaveException e) {
            throw new ServerInternalProblemException(e.getMessage(), e);
        }

        return username.concat(" has been added as a friend!");
    }

    private String createFriendshipName(String username1, String username2) {
        String friendshipName;
        if (username1.compareTo(username2) < 0) {
            friendshipName = username1.concat("-").concat(username2);

        } else {
            friendshipName = username2.concat("-").concat(username1);
        }
        return friendshipName;
    }

    private void validateArguments(List<String> arguments) throws CommandInvalidArgumentsException {
        int commandArgumentsCount = 2;

        if (arguments.size() != commandArgumentsCount) {
            throw new CommandInvalidArgumentsException("Command accepts only 2 arguments");
        }
    }

}