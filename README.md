# logmatters
Java log generator

## Build

```
mvn clean package
```
an executable jar will be built in `target/`

## Configure

Edit the configuration file [here](https://github.com/dashbase/logmatters/blob/master/conf/config.yml)

`throttleNPerSec: -1` defines how fast the logs are generated, -1 indicates no throttling, 1000 indicates 1k events are generated per second.

## Run the log generator

```./bin/testlog.sh```

Logs are generated under `logs` directory
