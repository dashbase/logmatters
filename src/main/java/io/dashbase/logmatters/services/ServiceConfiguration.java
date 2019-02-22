package io.dashbase.logmatters.services;

import javax.validation.constraints.NotNull;

public class ServiceConfiguration {
    public double sla = 99.0;
    public LatencySpec latencyMs = new LatencySpec();

    @NotNull
    public MessageFormatter format = new DefaultFormatter();
}
