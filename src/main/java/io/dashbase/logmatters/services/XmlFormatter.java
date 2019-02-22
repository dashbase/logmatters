package io.dashbase.logmatters.services;

import com.fasterxml.jackson.annotation.JsonCreator;

public class XmlFormatter implements MessageFormatter {

    @JsonCreator
    public XmlFormatter() {}

    @Override
    public String format(String message) {
        return "<xml><message>"+message+"</message></xml>";
    }
}
