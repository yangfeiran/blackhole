#!/bin/bash
#export JAVA_HOME=/usr/java/default
#export JRE_HOME=/usr/java/default/jre
#export CLASSPATH=${JAVA_HOME}/lib:${JRE_HOME}/lib:$CLASSPATH
#export PATH=${JAVA_HOME}/bin:${PATH}
nohup java -jar /opt/server/tools/blackhole/blackhole-0.0.1-SNAPSHOT.jar --spring.config.location=/opt/server/tools/blackhole/application.properties >./log.log 2>&1 &

