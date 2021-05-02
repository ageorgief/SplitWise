package bg.sofia.uni.fmi.mjt.server.commands;

import bg.sofia.uni.fmi.mjt.server.exception.CommandInvalidArgumentsException;
import bg.sofia.uni.fmi.mjt.server.exception.ServerInternalProblemException;

import java.util.List;

public interface Command {
    String execute(List<String> arguments)
            throws CommandInvalidArgumentsException, ServerInternalProblemException;
}
