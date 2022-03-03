for %%F IN (aggression-stopper-*.jar) DO (SET JAR_TO_RUN=%%F)

java -jar %JAR_TO_RUN% --spring.config.location=application.yml
