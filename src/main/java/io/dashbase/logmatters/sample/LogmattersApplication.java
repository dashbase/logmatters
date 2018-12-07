package io.dashbase.logmatters.sample;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.dhatim.dropwizard.prometheus.PrometheusBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static io.dashbase.logmatters.sample.TestLog.logIt;

public class LogmattersApplication extends Application<LogmattersConfiguration> {

    private static Logger logger = LoggerFactory.getLogger(LogmattersApplication.class);

    private Random rand = new Random();
    private volatile boolean isStopped = false;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) throws Exception {
        new LogmattersApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<LogmattersConfiguration> bootstrap) {
        bootstrap.addBundle(new PrometheusBundle());
    }

    @Override
    public void run(LogmattersConfiguration conf, Environment environment) throws Exception {
        Meter eventMeter = environment.metrics().meter("log_event_count");
        long sleepDuration = (long)(1000.0/(double)conf.throttleNPerSec);
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {
                scheduler.execute(new Runnable() {
                    @Override
                    public void run() {
                        while(!isStopped) {
                            logIt(logger);
                            eventMeter.mark();
                            if (sleepDuration >=0) {
                                try {
                                    Thread.sleep(sleepDuration);
                                } catch (InterruptedException ie) {
                                    continue;
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void stop() throws Exception {
                isStopped = true;
                scheduler.shutdown();
            }
        });
        System.out.println("logmatter: " + conf.name + " started");
    }
}
