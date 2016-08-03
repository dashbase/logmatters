#!/usr/bin/env bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
basedir=${bin}/..
dist=${basedir}/target
lib=${dist}/lib
cd ${basedir}

LOG_DIR=log

mkdir -p ${LOG_DIR}
echo "Log directory: ${LOG_DIR}"

JAVA_OPTS="-server \
   -d64 \
   -Dlog4j.configuration=file://$(pwd)/conf/logmatters_log4j.xml"

#JAVA_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=1044,server=y,suspend=y"
#GC_OPTS="-XX:+UseConcMarkSweepGC -XX:+UseParNewGC"

MAIN_CLASS="io.dashbase.logmatters.sample.TestLog"
CLASSPATH=${dist}/*:${lib}/*

  exec java \
    ${JAVA_OPTS} \
    ${HEAP_OPTS} \
    ${GC_OPTS} \
    ${JMX_OPTS} \
    ${JAVA_DEBUG} \
    -classpath ${CLASSPATH} \
    -Dlog.home=${LOG_DIR} \
    ${MAIN_CLASS} $@
