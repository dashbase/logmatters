package io.dashbase.logmatters.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dashbase.logmatters.services.ServiceRegistry;
import io.dropwizard.Application;
import io.dropwizard.cli.Command;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class LogmattersCommand extends EnvironmentCommand<io.dashbase.logmatters.sample.LogmattersConfiguration> {

  private static final Logger logger = LoggerFactory.getLogger(LogmattersCommand.class);
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final ExecutorService executorService = Executors.newCachedThreadPool();

  private final LogmattersApplication application;

  protected LogmattersCommand(LogmattersApplication application) {
    super(application,"generate", "Generates event logs to the specified output file");
    this.application = application;
  }

  @Override
  protected Class<LogmattersConfiguration> getConfigurationClass() {
    return LogmattersConfiguration.class;
  }

  @Override
  public void configure(Subparser subparser) {
    super.configure(subparser);
    // Add command line options
    subparser.addArgument("-e", "--events")
            .dest("events")
            .type(Integer.class)
            .required(true)
            .help("The number of log events to output");

    subparser.addArgument("-o", "--output")
            .dest("output")
            .type(String.class)
            .setDefault("logs/output.log")
            .required(false)
            .help("The file to output the logs");
  }

  @Override
  protected void run(Environment environment, Namespace namespace, LogmattersConfiguration logmattersConfiguration) throws Exception {
    for (LifeCycle lifeCycle : environment.lifecycle().getManagedObjects()) {
      try {
        lifeCycle.start();
      } catch(Exception e) {
        logger.error("start failed: " + lifeCycle, e);
      }
    }

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        for (LifeCycle lifeCycle : environment.lifecycle().getManagedObjects()) {
          if (lifeCycle.isRunning()) {
            try {
              lifeCycle.stop();
            } catch(Exception e) {
              logger.error("stop failed: " + lifeCycle, e);
            }
          }
        }
        System.out.println(String.format("Generating %d events to output file %s", namespace.getInt("events"), namespace.getString("output")));
      }
    });

    while(true) {
      for (LifeCycle lifeCycle : environment.lifecycle().getManagedObjects()) {
        if (lifeCycle.isRunning()) {
          logger.info("lifecycle is still running");
          Thread.sleep(100);
          continue;
        }
      }
      break;
    }
  }
}