#!/usr/bin/env bash
logs=${LOG_PATH:-/logs}
conf=${CONFIG_PATH:-./conf/config.yml}
loop=$1

for i in `seq 1 $loop`
do
epoch=`date +%s`
nohup java  -Dprocessname=dashbase-log -Didentifier=$epoch \
      -Dtotal=$loop ${JAVA_OPTS} ${HEAP_OPTS} ${GC_OPTS} ${JMX_OPTS} ${JAVA_DEBUG} \
      -Ddw.logging.appenders[0].currentLogFilename=${logs}/dashbase-$epoch.log \
      -Ddw.logging.appenders[0].archivedLogFilenamePattern=${logs}/dashbase-$epoch.log.%i \
      -jar ./target/logmatters-*.jar server ${conf} > /tmp/logmatters.log &

sleep 1

done

# sleep
while true; do sleep 100; done
