package io.dashbase.logmatters.services;

import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {
    private ServiceRegistry() {

    }

    private static final Map<String, Service> REGISTRY = new HashMap();

    public static final ServiceRegistry INSTANCE = new ServiceRegistry();

    public void load(Map<String, ServiceConfiguration> conf) {
        conf.entrySet().stream().forEach(
                e -> {
                    REGISTRY.put(e.getKey(), new Service(e.getKey(), e.getValue()));
                }
        );
    }

    public Service find(String name) {
        return REGISTRY.get(name);
    }
}
