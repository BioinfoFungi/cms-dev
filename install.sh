#!/bin/bash
git pull

dir=`pwd`
app="cms-dev-0.0.1-SNAPSHOT.jar"
jar="${dir}/cms-boot/target/${app}"



pid=$(jps | grep $app | awk '{print $1}')

if [ $pid ]
then
  echo "kill ${pid}"
  kill -9 ${pid}
fi

mvn clean
mvn install

if [ ! -f $jar ];then
  echo "build failure！！！"
  exit 8
fi

echo $jar
nohup java -jar $jar 2>&1 > bioinfo.log &
tail -f bioinfo.log