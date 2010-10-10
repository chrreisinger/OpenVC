#!/bin/sh
java -server -Xmx512M -Xss3M -jar `dirname $0`/bin/sbt-launch.jar "$@"