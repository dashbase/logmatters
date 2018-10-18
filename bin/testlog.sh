#!/usr/bin/env bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
basedir=${bin}/..
conf=${basedir}/conf
dist=${basedir}/target
lib=${dist}/lib
cd ${basedir}

#JAVA_OPTS=""

#JAVA_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=1044,server=y,suspend=y"
#GC_OPTS="-XX:+UseConcMarkSweepGC -XX:+UseParNewGC"

  exec java \
    ${JAVA_OPTS} \
    ${HEAP_OPTS} \
    ${GC_OPTS} \
    ${JMX_OPTS} \
    ${JAVA_DEBUG} \
    -jar ${dist}/logmatters-*.jar server ${conf}/config.yml
