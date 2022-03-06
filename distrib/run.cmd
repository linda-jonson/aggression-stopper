for %%F IN (aggression-stopper-*.jar) DO (SET JAR_TO_RUN=%%F)

java -server -Xms128m -Xmx256m -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dio.netty.tryReflectionSetAccessible=true -jar %JAR_TO_RUN% --spring.config.location=application.yml
