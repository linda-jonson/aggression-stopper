#!/bin/sh

CURRENT_PATH=`pwd`

JAR_TO_RUN=`ls aggression-stopper-*.jar`

java -jar "$JAR_TO_RUN" --spring.config.location=application.yml
