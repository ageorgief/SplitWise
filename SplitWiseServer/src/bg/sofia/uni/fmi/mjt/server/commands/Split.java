package bg.sofia.uni.fmi.mjt.server.commands;

import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.exception.CacheSaveException;
import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.exception.ServerInternalProblemException;
import bg.sofia.uni.fmi.mjt.server.model.Friendship;
import bg.sofia.uni.fmi.mjt.server.model.Payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Split implements Command {
    private Cache serverCache;

    public Split(Cache serverCache) {
        this.serverCache = serverCache;
    }

    @Override
    public String execute(List<String> arguments) throws CommandInvalidArgumentsException, ServerInternalProblemException {
        validateArguments(arguments);

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(arguments.get(0)));
        String friendName = arguments.get(1);
        String username = arguments.get(2);
        String friendshipName = getFriendshipName(username, friendName);

        Friendship friendship = serverCache.getFriendship(friendshipName);
        Payment payment = new Payment(friendName, username, amount.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP));
        friendship.addPayment(payment);

        try {
            serverCache.saveFriendship(friendship);
        } catch (CacheSaveException e) {
            throw new ServerInternalProblemException(e.getMessage(), e);
        }

        return "Split created successfully";
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

        String friendName = arguments.get(1);
        if (!serverCache.existsUser(friendName)) {
            throw new CommandInvalidArgumentsException(friendName.concat(" does not exists"));
        }

        String username = arguments.get(2);
        String friendshipName = getFriendshipName(username, friendName);
        if (!serverCache.existsFriendship(friendshipName)) {
            throw new CommandInvalidArgumentsException(friendshipName.concat(" does not exists"));
        }
    }

    private String getFriendshipName(String username, String friendName) {
        if (username.compareTo(friendName) < 0) {
            return username.concat("-").concat(friendName);
        }

        return friendName.concat("-").concat(username);
    }

}