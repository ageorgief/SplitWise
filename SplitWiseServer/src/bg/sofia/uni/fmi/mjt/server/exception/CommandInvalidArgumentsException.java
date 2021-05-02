package bg.sofia.uni.fmi.mjt.server.exception;

public class CommandInvalidArgumentsException extends Exception {

    public CommandInvalidArgumentsException(String message) {
        super(message);
    }

    public CommandInvalidArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }

}
