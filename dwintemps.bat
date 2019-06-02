@ECHO OFF
set CLASSPATH=.

%JAVA_HOME%\bin\java -Xms128m -Xmx384m -Xnoclassgc -Dlogging.level.root=error -jar target/dwin-temps-0.0.1-SNAPSHOT.jar %*
