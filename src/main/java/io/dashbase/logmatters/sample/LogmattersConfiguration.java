package io.dashbase.logmatters.sample;

import io.dashbase.logmatters.services.Deployment;
import io.dashbase.logmatters.services.ServiceConfiguration;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class LogmattersConfiguration extends Configuration {
    @NotNull
    public Map<String, ServiceConfiguration> services = new HashMap<>();
    @NotNull
    public Deployment deployment = null;
}
