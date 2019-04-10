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
```
$ ./bin/run.sh 3
Started the process 1 with Log name: /logs/dashbase-1544179256-1.log...
Started the process 2 with Log name: /logs/dashbase-1544179256-2.log...
Started the process 3 with Log name: /logs/dashbase-1544179256-3.log...
Got Identifier(group name) of these process: 1544179256
You can kill them by `./bin/kill.sh 1544179256`
appending output to nohup.out                                                                                                                                             
appending output to nohup.out
appending output to nohup.out

```
#### Kill these producers
```
$ ./bin/kill.sh 1544179256`
```

#### Run 3 producer each with throttle limit 30
```./bin/run.sh 3 30```

#### Show producer info(use regular expressions to match, maybe too slow)
```
$ python ./bin/show.py
Producer group 1544184708 have 3 producers with throttle 30.
Producer group 1544185211 have 2 producers with throttle 20.
Producer group 1544185223 have 3 producers with throttle -1.
Producer group 1544185281 have 4 producers with throttle -1.
Producer group 1544185307 have 50 producers with throttle 1.
You can kill group by `./bin/kill.sh <group-name>`, or use `./bin/kill.sh` to kill them all.
Use `ps -ef | grep java` to get all running processed.
```

#### Kill all producer
```./bin/kill.sh```

## Run as a script to generate logs

#### Using Docker image
```
$ docker run -it dashbase/log-generator conf/generate.yml -c 10 > samples.log
```
The above generates 10 service calls (which depending on your generate.yml in turn might generate more log lines) and pipes the results to the `samples.log` file.
Logs can be piped to a file, but can also be found under `logs/logmatters.log` (settings defined in generate.yml).

#### To build the docker image for `dashbase/log-generator` simply do:

`mvn package`

`docker build -f DockerfileGenerator -t dashbase/logs-generator .`

#### Manually
```
$ mvn package
```

```
java -jar target/logmatters-0.0.1-SNAPSHOT.jar generate conf/generate.yml -c 1
```
Generates one call and uses `generate.yml` to figure out the calls.