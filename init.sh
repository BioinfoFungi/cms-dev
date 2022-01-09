#!/bin/bash
git pull
./mvnw clean
./mvnw install
dir=`pwd`
app="cms-dev-0.0.1-SNAPSHOT.jar"
jar="${dir}/target/${app}"

if [ ! -f $jar ];then
  echo "build failure！！！"
  exit 8
fi

pid=$(jps | grep $app | awk '{print $1}')

if [ $pid ]
then
  echo "kill ${pid}"
  kill -9 ${pid}
fi

echo $jar
nohup java -jar $jar 2>&1 > bioinfo.log &
tail -f bioinfo.log