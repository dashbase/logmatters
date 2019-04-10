package io.dashbase.logmatters.sample;

import io.dashbase.logmatters.services.ServiceRegistry;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class LogmattersCommand extends EnvironmentCommand<io.dashbase.logmatters.sample.LogmattersConfiguration> {

  private static final Logger logger = LoggerFactory.getLogger(LogmattersCommand.class);
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final ExecutorService executorService = Executors.newCachedThreadPool();
  private volatile int numberOfEventsGenerated = 0;
  private int MaxCallsToGenerate = 10;

  protected LogmattersCommand(LogmattersApplication application) {
    super(application,"generate", "Generates event logs to the specified output file");
  }

  @Override
  protected Class<LogmattersConfiguration> getConfigurationClass() {
    return LogmattersConfiguration.class;
  }

  @Override
  public void configure(Subparser subparser) {
    super.configure(subparser);
    // Add command line options
    subparser.addArgument("-c", "--calls")
            .dest("calls")
            .type(Integer.class)
            .required(true)
            .help("The number of service calls to output. Check your config to see how a single call might map to multiple log events.");
  }

  @Override
  protected void run(Environment environment, Namespace namespace, LogmattersConfiguration conf) throws Exception {

    MaxCallsToGenerate = namespace.getInt("calls");
    logger.debug(String.format("Generating %d events to output file %s", MaxCallsToGenerate, namespace.getString("output")));

    ServiceRegistry.INSTANCE.load(conf.services);

    final Managed life = new Managed() {
      @Override
      public void start() {
        scheduler.execute(() -> {
          while(numberOfEventsGenerated < MaxCallsToGenerate) {
            numberOfEventsGenerated++;
            for (int i = 0; i < conf.deployment.concurrentRequest; ++i) {
              conf.deployment.callPath.execute(null, executorService);
            }
          }
          logger.debug(String.format("Stopping after generating %d events.", numberOfEventsGenerated));
          this.stop();
        });
      }

      @Override
      public void stop() {
        logger.debug("shutting down logmatters");
        logger.debug("shutting down executor service");
        executorService.shutdown();
        logger.debug("shutting down scheduler");
        scheduler.shutdown();
      }
    };

    life.start();

    environment.lifecycle().manage(life);
  }
}