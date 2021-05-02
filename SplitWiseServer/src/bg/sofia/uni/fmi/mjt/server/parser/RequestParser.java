package bg.sofia.uni.fmi.mjt.server.parser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestParser {

    public String getCommand(String clientMessage) {
        clientMessage = clientMessage.trim();
        String[] tokens = clientMessage.split(" ");
        int commandIndex = 0;
        return tokens[commandIndex];
    }

    public List<String> getArguments(String clientMessage) {
        String[] tokens = clientMessage.trim().split(" ");

        return Stream.of(tokens)
                .skip(1)
                .collect(Collectors.toList());
    }

}
