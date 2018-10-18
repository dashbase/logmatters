package io.dashbase.logmatters.sample;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static io.dashbase.logmatters.sample.TestLog.logIt;

public class LogmattersApplication extends Application<LogmattersConfiguration> {

    private static Logger logger = LoggerFactory.getLogger(LogmattersApplication.class);

    private Random rand = new Random();
    private volatile boolean isStopped = false;

    public static void main(String[] args) throws Exception {
        new LogmattersApplication().run(args);
    }

    @Override
    public void run(LogmattersConfiguration conf, Environment environment) throws Exception {
        long sleepDuration = (long)(1000.0/(double)conf.throttleNPerSec);
        while(!isStopped) {
            logIt(logger);
            if (sleepDuration >=0) {
                Thread.sleep(sleepDuration);
            }
        }
    }
}
