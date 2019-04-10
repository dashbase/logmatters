package io.dashbase.logmatters.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dashbase.logmatters.services.ServiceRegistry;
import io.dropwizard.cli.Command;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationFactoryFactory;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class LogmattersCommand extends Command {

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final ExecutorService executorService = Executors.newCachedThreadPool();

  public LogmattersCommand() {
    // The name of our command is "hello" and the description printed is
    // "Prints a greeting"
    super("generate", "Generates event logs to the specified output file");
  }

  @Override
  public void configure(Subparser subparser) {
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
  @SuppressWarnings("unchecked")
  public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {

    ConfigurationFactoryFactory configurationFactoryFactory = new DefaultConfigurationFactoryFactory<LogmattersConfiguration>();
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.getValidator();
    ObjectMapper objectMapper = Jackson.newObjectMapper();
    final ConfigurationFactory<LogmattersConfiguration> configurationFactory = configurationFactoryFactory.create(LogmattersConfiguration.class, validator, objectMapper, "dw");
    File confFile = new File("conf/config.yml");
    LogmattersConfiguration config = configurationFactory.build(new File(confFile.toURI()));


    ServiceRegistry.INSTANCE.load(config.services);

    scheduler.execute(() -> {
      int totalEvents = 0;

      while(totalEvents < 2) {
        totalEvents++;
        for (int i = 0; i < config.deployment.concurrentRequest; ++i) {
          config.deployment.callPath.execute(null, executorService);
        }
      }
    });

    System.out.println(String.format("Generating %d events to output file %s", namespace.getInt("events"), namespace.getString("output")));
  }

}