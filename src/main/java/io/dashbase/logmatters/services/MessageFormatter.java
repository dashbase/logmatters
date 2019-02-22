package io.dashbase.logmatters.services;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = DefaultFormatter.class)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "default", value = DefaultFormatter.class),
        @JsonSubTypes.Type(name = "json", value = JsonFormatter.class),
        @JsonSubTypes.Type(name = "xml", value = XmlFormatter.class)
})

public interface MessageFormatter {
    String format(String message);
}
