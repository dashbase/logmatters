# logmatters
Java log generator

## Build

Pre-requisites:

* [java 10+](https://www.oracle.com/technetwork/java/javase/downloads/jdk10-downloads-4416644.html)
* [maven](https://maven.apache.org/)

Build:
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

### Run multi processed
#### Run 3 producer
```./bin/run.sh 3```
process-1 write logs to `/logs/dashbase-1-xxx.log`
...
process-3 write logs to `/logs/dashbase-3-xxx.log`

#### Run 3 producer each with throttle limit 30
```./bin/run.sh 3 30```

#### Kill all producer
```./bin/kill.sh```
