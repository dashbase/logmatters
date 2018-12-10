#!/usr/bin/env bash

#java -jar target/logmatters-0.0.1-SNAPSHOT.jar server conf/config.yml

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
basedir=${bin}/..
conf=${basedir}/conf
dist=${basedir}/target
lib=${dist}/lib
logs=${LOG_PATH:-log}
cd ${basedir}

epoch=`date +%s`

for i in `seq 1 $1`
do
    if [ x"$2" = x ]; then
        nohup java  -Dprocessname=dashbase-log -Didentifier=$epoch -Dtotal=$1 ${JAVA_OPTS} ${HEAP_OPTS} ${GC_OPTS} ${JMX_OPTS} ${JAVA_DEBUG} -Ddw.logging.appenders[0].currentLogFilename=${logs}/dashbase-$epoch-$i.log -Ddw.logging.appenders[0].archivedLogFilenamePattern=${logs}/dashbase-$epoch-$i.log.%i -jar ${dist}/logmatters-0.0.1-SNAPSHOT.jar server ${conf}/config.yml > /tmp/logmatters.log &
    else
        nohup java -Dprocessname=dashbase-log -Didentifier=$epoch -Ddw.throttleNPerSec=$2 -Dtotal=$1 ${JAVA_OPTS} ${HEAP_OPTS} ${GC_OPTS} ${JMX_OPTS} ${JAVA_DEBUG} -Ddw.logging.appenders[0].currentLogFilename=${logs}/dashbase-$epoch-$i.log -Ddw.logging.appenders[0].archivedLogFilenamePattern=${logs}/dashbase-$epoch-$i.log.%i -jar ${dist}/logmatters-0.0.1-SNAPSHOT.jar server ${conf}/config.yml > /tmp/logmatters.log &
    fi
    echo "Started the process $i with Log name: ${logs}/dashbase-$epoch-$i.log..."
done
echo "Got Identifier of these process: $epoch"
echo "You can kill them by \`./bin/kill.sh $epoch\`"