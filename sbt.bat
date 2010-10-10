set SCRIPT_DIR=%~dp0
java -server -Xmx512M -Xss3M -jar "%SCRIPT_DIR%bin/sbt-launch.jar" %*