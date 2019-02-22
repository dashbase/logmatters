package io.dashbase.logmatters.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public final class Service {
    private static final Logger logger = LoggerFactory.getLogger(Service.class);
    private static final Random RANDOM = new Random();

    public final String name;
    public final ServiceConfiguration config;

    static boolean isFail(double sla) {
        int roll = Math.abs(RANDOM.nextInt(100000)) + 1;
        return (roll > (int) (sla * 1000));
    }

    static long latency(LatencySpec latencySpec) {
        int roll = Math.abs(RANDOM.nextInt(100));
        if (roll < 50) {
            return latencySpec.p50;
        } else if (roll < 90) {
            return latencySpec.p90;
        } else {
            return latencySpec.p99;
        }
    }

    public Service(String name, ServiceConfiguration config) {
        this.name = name;
        this.config = config;
    }

    public final void execute(String ctxId, CallPath subcalls, ExecutorService executorService) throws Exception {
        final String ctx;
        if (ctxId == null) {
            ctx = UUID.randomUUID().toString();
        } else {
            ctx = ctxId;
        }
        logger.info(config.format.format("ctx: [" + ctx +"] executing service: " + name));
        if (isFail(config.sla)) {
            throw new Exception("ctx: [" + ctx + "] service: " + name + " internal error");
        }
        long duration = 0;
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(latency(config.latencyMs));
        } catch (InterruptedException ie) {
            logger.error("ctx: [" + ctx + "] thread interrupted: ", ie);
        } finally {
            duration = System.currentTimeMillis() - start;
        }
        subcalls.execute(ctx, executorService);
        logger.info(config.format.format("ctx: [" + ctx +"] done executing service: " + name + ", took: " + duration + " ms"));
    }
}
