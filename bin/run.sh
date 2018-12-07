#!/usr/bin/env bash

#java -jar target/logmatters-0.0.1-SNAPSHOT.jar server conf/config.yml

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
basedir=${bin}/..
conf=${basedir}/conf
dist=${basedir}/target
lib=${dist}/lib
logs=/logs
cd ${basedir}

for((i = 1; i <= $1; i++))
do
    if [ x"$2" = x ]; then
        nohup java  -Dprocessname=dashbase-log ${JAVA_OPTS} ${HEAP_OPTS} ${GC_OPTS} ${JMX_OPTS} ${JAVA_DEBUG} -Ddw.logging.appenders[0].currentLogFilename=${logs}/dashbase-$i.log -Ddw.logging.appenders[0].archivedLogFilenamePattern=${logs}/dashbase-$i-%d.log.%i -jar ${dist}/logmatters-0.0.1-SNAPSHOT.jar server ${conf}/config.yml &
    else
        nohup java -Dprocessname=dashbase-log ${JAVA_OPTS} ${HEAP_OPTS} ${GC_OPTS} ${JMX_OPTS} ${JAVA_DEBUG} -Ddw.throttleNPerSec=$2 -Ddw.logging.appenders[0].currentLogFilename=${logs}/dashbase-$i.log -Ddw.logging.appenders[0].archivedLogFilenamePattern=${logs}/dashbase-$i-%d.log.%i -jar ${dist}/logmatters-0.0.1-SNAPSHOT.jar server ${conf}/config.yml &
    fi
    echo "Started the process $i with Log name: dashbase-$i.log..."
done
