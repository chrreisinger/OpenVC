#!/bin/sh
java -Xmx512M -Xss3M -jar `dirname $0`/bin/sbt-launch.jar "$@"