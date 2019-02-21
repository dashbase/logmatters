package io.dashbase.logmatters.sample;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dashbase.logmatters.services.ServiceRegistry;
import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.dhatim.dropwizard.prometheus.PrometheusBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static io.dashbase.logmatters.sample.TestLog.logIt;

public class LogmattersApplication extends Application<LogmattersConfiguration> {

    private static Logger logger = LoggerFactory.getLogger(LogmattersApplication.class);

    private volatile boolean isStopped = false;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        new LogmattersApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<LogmattersConfiguration> bootstrap) {
        bootstrap.addBundle(new PrometheusBundle());
    }

    @Override
    public void run(LogmattersConfiguration conf, Environment environment) throws Exception {
        ServiceRegistry.INSTANCE.load(conf.services);
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {
                scheduler.execute(new Runnable() {
                    @Override
                    public void run() {
                        while(!isStopped) {
                            for (int i = 0; i < conf.deployment.concurrentRequest; ++i) {
                                conf.deployment.callPath.execute(null, executorService);
                            }
                        }
                    }
                });
            }

            @Override
            public void stop() throws Exception {
                logger.info("shutting down logmatters");
                isStopped = true;
                logger.info("shutting down executor service");
                executorService.shutdown();
                logger.info("shutting down scheduler");
                scheduler.shutdown();
            }
        });
        logger.info("logmatters: started");
    }
}
