#!/usr/bin/env bash
if [ x"$1" = x ]; then
    pkill -f "java -Dprocessname=dashbase-log"
else
    pkill -f "java -Dprocessname=dashbase-log -Didentifier=$1"
fi