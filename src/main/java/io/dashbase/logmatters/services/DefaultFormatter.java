package io.dashbase.logmatters.services;

import com.fasterxml.jackson.annotation.JsonCreator;

public class DefaultFormatter implements MessageFormatter {

    @JsonCreator
    public DefaultFormatter() {

    }

    public String format(String message) {
        return message;
    }
}
