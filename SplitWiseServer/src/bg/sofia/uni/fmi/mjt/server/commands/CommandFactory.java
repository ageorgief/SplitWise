package bg.sofia.uni.fmi.mjt.server.commands;


import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.exception.InvalidCommandException;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private Map<String, Command> commands;

    public CommandFactory(Cache serverCache) {
        commands = new HashMap<>();
        commands.put("register", new Register(serverCache));
        commands.put("login", new Login(serverCache));
        commands.put("add-friend", new AddFriend(serverCache));
        commands.put("create-group", new CreateGroup(serverCache));
        commands.put("split", new Split(serverCache));
        commands.put("split-group", new SplitGroup(serverCache));
        commands.put("get-status", new GetStatus(serverCache));
        commands.put("payed", new Payed(serverCache));
    }

    public Command make(String commandName) throws InvalidCommandException {
        Command command = commands.get(commandName);

        if (command == null) {
            String message = "The entered command is invalid.";

            throw new InvalidCommandException(message);
        }

        return command;
    }
}