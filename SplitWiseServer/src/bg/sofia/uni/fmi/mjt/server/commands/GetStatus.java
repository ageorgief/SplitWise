package bg.sofia.uni.fmi.mjt.server.commands;

import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.model.Friendship;
import bg.sofia.uni.fmi.mjt.server.model.Group;
import bg.sofia.uni.fmi.mjt.server.model.Payment;
import bg.sofia.uni.fmi.mjt.server.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GetStatus implements Command {
    private Cache serverCache;

    public GetStatus(Cache serverCache) {
        this.serverCache = serverCache;
    }

    @Override
    public String execute(List<String> arguments) throws CommandInvalidArgumentsException {
        validateArguments(arguments);

        String username = arguments.get(0);
        User user = serverCache.getUser(username);
        Set<String> friendships = user.getGroupNames().stream()
                .filter(friendshipName -> serverCache.existsFriendship(friendshipName))
                .collect(Collectors.toSet());
        Set<String> groups = user.getGroupNames().stream()
                .filter(groupName -> serverCache.existsGroup(groupName))
                .collect(Collectors.toSet());

        StringBuilder result = new StringBuilder("Friends:".concat(System.lineSeparator()));
        for (String friendship : friendships) {
            Payment payment = serverCache.getFriendship(friendship).getPayment();
            if (payment.getSender().equals(username)) {
                result.append("You owe ").append(payment.getReceiver()).append(" ")
                        .append(payment.getAmount()).append(" LV").append(System.lineSeparator());
            } else {
                result.append(payment.getSender()).append(" owes you ").append(payment.getAmount()).append(" LV")
                        .append(System.lineSeparator());
            }
        }

        result.append("Groups: ".concat(System.lineSeparator()));

        for (String groupName : groups) {
            Group group = serverCache.getGroup(groupName);
            result.append("* ").append(group.getName()).append(System.lineSeparator());

            for (Payment payment : group.getPayments()) {
                if (payment.getSender().equals(username)) {
                    result.append("You owe ").append(payment.getReceiver()).append(" ")
                            .append(payment.getAmount()).append(" LV").append(System.lineSeparator());
                } else if (payment.getReceiver().equals(username)) {
                    result.append(payment.getSender()).append(" owes you ").append(payment.getAmount()).append(" LV")
                            .append(System.lineSeparator());
                }
            }
        }

        return result.toString();
    }

    private void validateArguments(List<String> arguments) throws CommandInvalidArgumentsException {
        int commandArgumentsCount = 1;

        if (arguments.size() != commandArgumentsCount) {
            throw new CommandInvalidArgumentsException("Invalid arguments");
        }
    }
}
