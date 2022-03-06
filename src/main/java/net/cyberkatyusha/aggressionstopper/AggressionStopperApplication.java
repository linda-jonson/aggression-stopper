package net.cyberkatyusha.aggressionstopper;

import net.cyberkatyusha.aggressionstopper.config.AggressionStopperSettings;
import net.cyberkatyusha.aggressionstopper.exception.ApplicationException;
import net.cyberkatyusha.aggressionstopper.model.ExecutionMode;
import net.cyberkatyusha.aggressionstopper.service.AggressionsStopperHttpService;
import net.cyberkatyusha.aggressionstopper.service.AggressionsStopperTcpNioService;
import net.cyberkatyusha.aggressionstopper.service.AggressionsStopperTcpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Instant;

@SpringBootApplication(
        scanBasePackageClasses = {AggressionStopperApplication.class}
)
@Configuration
@EnableConfigurationProperties
public class AggressionStopperApplication implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AggressionStopperApplication.class);

    private final AggressionStopperSettings settings;
    private final AggressionsStopperHttpService aggressionsStopperHttpService;
    private final AggressionsStopperTcpService aggressionsStopperTcpService;
    private final AggressionsStopperTcpNioService aggressionsStopperTcpNioService;

    public AggressionStopperApplication(
            AggressionStopperSettings settings,
            AggressionsStopperHttpService aggressionsStopperHttpService,
            AggressionsStopperTcpService aggressionsStopperTcpService,
            AggressionsStopperTcpNioService aggressionsStopperTcpNioService) {
        this.settings = settings;
        this.aggressionsStopperHttpService = aggressionsStopperHttpService;
        this.aggressionsStopperTcpService = aggressionsStopperTcpService;
        this.aggressionsStopperTcpNioService = aggressionsStopperTcpNioService;
    }

    public static void main(String[] args) {
        logger.info("Starting the application");

        SpringApplication application = new SpringApplication(AggressionStopperApplication.class);

        application.setWebApplicationType(WebApplicationType.NONE);

        application.run(args);

        logger.info("Application finished");
    }

    @Override
    public void run(ApplicationArguments args) {

        Instant start = java.time.Instant.now();

        final ExecutionMode executionMode = settings.getExecutionMode();

        if (ExecutionMode.HTTP.equals(executionMode)) {
            aggressionsStopperHttpService.execute();
        } else if (ExecutionMode.TCP.equals(executionMode)) {
            aggressionsStopperTcpService.execute();
        } else if (ExecutionMode.TCP_NIO.equals(executionMode)) {
            aggressionsStopperTcpNioService.execute();
        } else {
            throw new ApplicationException("Unknown execution mode, possible values: 'http', 'tcp'.");
        }

        Instant end = java.time.Instant.now();
        Duration between = Duration.between(start, end);

        logger.info(
                "Time spent to handle all requests: %s".formatted(
                        "%d days, %02d:%02d:%02d.%04d".formatted(
                                between.toDays(),
                                between.toHours(),
                                between.toMinutes(),
                                between.getSeconds(),
                                between.toMillis()
                        )
                )
        );

    }

}
