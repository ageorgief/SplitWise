package bg.sofia.uni.fmi.mjt.server.commands;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import bg.sofia.uni.fmi.mjt.server.cache.Cache;
import bg.sofia.uni.fmi.mjt.server.exception.CacheSaveException;
import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.exception.ServerInternalProblemException;
import bg.sofia.uni.fmi.mjt.server.model.Friendship;
import bg.sofia.uni.fmi.mjt.server.model.Group;
import bg.sofia.uni.fmi.mjt.server.model.Payment;
import bg.sofia.uni.fmi.mjt.server.model.User;

public class Payed implements Command {
    private Cache serverCache;

    public Payed(Cache serverCache) {
        this.serverCache = serverCache;
    }

    @Override
    public String execute(List<String> arguments)
            throws CommandInvalidArgumentsException, ServerInternalProblemException {
        boolean payingUserNotFound = true;

        BigDecimal paidAmount = BigDecimal.valueOf(Double.parseDouble(arguments.get(0)));
        String payingUser = arguments.get(1);
        String paidUserName = arguments.get(2);

        Payment paidPayment = new Payment(payingUser, paidUserName, paidAmount);

        User paidUser = serverCache.getUser(paidUserName);

        Set<String> friendships = paidUser.getGroupNames().stream()
                .filter(friendshipName -> serverCache.existsFriendship(friendshipName)).collect(Collectors.toSet());
        Set<String> groups = paidUser.getGroupNames().stream().filter(groupName -> serverCache.existsGroup(groupName))
                .collect(Collectors.toSet());

        boolean isAmountPaid = false;
        for (String friendshipName : friendships) {
            Friendship friendship = serverCache.getFriendship(friendshipName);
            Payment payment = friendship.getPayment();
            try {
                if (payment.getSender().equals(payingUser)) {
                    payingUserNotFound = false;
                    if (payment.getAmount().compareTo(paidAmount) >= 0) {
                        payment.subtractAmount(paidAmount);
                        isAmountPaid = true;
                        serverCache.saveFriendship(friendship);
                        break;
                    }
                    paidAmount = paidAmount.subtract(payment.getAmount());
                    payment.subtractAmount(payment.getAmount());
                    serverCache.saveFriendship(friendship);
                    break;
                }
            } catch (CacheSaveException e) {
                throw new ServerInternalProblemException(e.getMessage(), e);
            }
        }

        if (!isAmountPaid) {
            for (String groupName : groups) {
                Group group = serverCache.getGroup(groupName);
                if (group.getMembers().contains(payingUser)) {
                    for (Payment payment : group.getPayments()) {
                        if (payment.getSender().equals(payingUser)) {
                            try {
                                payingUserNotFound = false;
                                if (payment.getAmount().compareTo(paidAmount) >= 0) {
                                    payment.subtractAmount(paidAmount);
                                    serverCache.saveGroup(group);
                                    break;
                                }
                                paidAmount = paidAmount.subtract(payment.getAmount());
                                payment.subtractAmount(payment.getAmount());
                                serverCache.saveGroup(group);
                                break;
                            } catch (CacheSaveException e) {
                                throw new ServerInternalProblemException(e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }

        if (payingUserNotFound) {
            throw new CommandInvalidArgumentsException(payingUser.concat(" not found in your payments!"));
        }

        try {
            serverCache.saveUserPayment(paidUser, paidPayment);
        } catch (CacheSaveException e) {
            throw new ServerInternalProblemException(e.getMessage(), e);
        }

        return payingUser.concat(" payed you successfully!");
    }

    private void validateArguments(List<String> arguments) throws CommandInvalidArgumentsException {
        int minCommandArgumentsCount = 3;

        if (arguments.size() < minCommandArgumentsCount) {
            throw new CommandInvalidArgumentsException(
                    "Command requires at least " + minCommandArgumentsCount + " arguments");
        }

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(arguments.get(0)));

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CommandInvalidArgumentsException("Amount should be positive");
        }

        String userName = arguments.get(1);
        if (serverCache.existsUser(userName)) {
            throw new CommandInvalidArgumentsException(userName.concat(" does not exists"));
        }
    }

}