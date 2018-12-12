#!/usr/bin/env bash
logs=${LOG_PATH:-/logs}

java  -Dprocessname=dashbase-log -Didentifier=$epoch \
      -Dtotal=$1 ${JAVA_OPTS} ${HEAP_OPTS} ${GC_OPTS} ${JMX_OPTS} ${JAVA_DEBUG} \
      -Ddw.logging.appenders[0].currentLogFilename=${LOG_PA}/dashbase-$epoch-$i.log \
      -Ddw.logging.appenders[0].archivedLogFilenamePattern=${logs}/dashbase-$epoch-$i.log.%i \
      -jar ${dist}/logmatters-*.jar server ${conf}/config.yml
