#!/bin/sh

state=0
while [ $state -eq 0 ]; do
  nc -z -v mysql_container 3306;
  if [ $? -eq 0 ];then
    state=1;
  else
    echo "Wait for mysql"
    sleep 3;
  fi;
  done;
  
java -jar project.jar
