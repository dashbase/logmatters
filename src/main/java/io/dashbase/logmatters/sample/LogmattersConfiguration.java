package io.dashbase.logmatters.sample;

import io.dropwizard.Configuration;

public class LogmattersConfiguration extends Configuration {
    public int throttleNPerSec = 1;
    public String name = "logmatters";
}
