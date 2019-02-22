package io.dashbase.logmatters.services;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonFormatter implements MessageFormatter {
    private final ObjectMapper mapper = new ObjectMapper();

    @JsonCreator
    public JsonFormatter() {}

    @Override
    public String format(String message) {
        ObjectNode node = mapper.createObjectNode();
        node.put("message", message);
        return node.toString();
    }
}
