#!/bin/sh
export JAVA_HOME=/usr/java/default
export PATH=$JAVA_HOME/bin:$PATH
nohup java -jar ./blackhole-1.5.jar --spring.config.location=./application.properties >./log.log 2>&1 &
echo "start blackhole server start success..."
