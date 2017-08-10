#/bin/bash

BLACKHOLE_HOME=/opt/server/stargate/blackhole
BLACKHOLE_LOG=/var/log/stargate/blackhole
MYDATE=`date +%Y%m%d-%H%M%S`

echo 'starting stargate blackhole...'
mkdir -p ${BLACKHOLE_LOG}
if [ -f ${BLACKHOLE_HOME}/conf/rs.jar ]; then
  rm -rf ${BLACKHOLE_HOME}/conf/rs.jar.bak
  mv ${BLACKHOLE_HOME}/conf/rs.jar ${BLACKHOLE_HOME}/conf/rs.jar.bak
fi
cd ${BLACKHOLE_HOME}/conf/rs
jar cf rs.jar .
mv rs.jar ..

cd ${BLACKHOLE_HOME}
nohup java -cp blackhole.jar:conf/rs.jar:lib/* com.chinacloud.BlackholeApplication >> ${BLACKHOLE_LOG}/server.log 2>&1 &

echo 'started stargate blackhole...'
exit 0