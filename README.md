# logmatters
Log Management with Dashbase

## Parser

the logstash parser parses a logstash json into a dashbase event

## Run the log generator

1. Build -> mvn clean package
2. ./bin/testlog.sh {numlogs}, e.g. {numlogs} = 1000000, will generate 1M logs with 10% errors with exception, and 20% warnings and 70% infos

