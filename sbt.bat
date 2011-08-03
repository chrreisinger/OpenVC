set SCRIPT_DIR=%~dp0
java -Dfile.encoding=UTF8 -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256m -server -Xmx512M -Xss3M -jar "%SCRIPT_DIR%project\sbt-launch.jar" %*