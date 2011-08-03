#!/bin/sh
java -Dfile.encoding=UTF8 -Dsbt.boot.directory=~/.sbt/boot/ -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256m -server -Xmx512M -Xss3M -jar `dirname $0`/project/sbt-launch.jar "$@"