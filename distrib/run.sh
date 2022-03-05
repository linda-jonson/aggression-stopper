#!/bin/sh

CURRENT_PATH=`pwd`

JAR_TO_RUN=`ls aggression-stopper-*.jar`

java -server -Xms128m -Xmx256m -XX:+UseContainerSupport -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dio.netty.tryReflectionSetAccessible=true -jar "$JAR_TO_RUN" --spring.config.location=application.yml
