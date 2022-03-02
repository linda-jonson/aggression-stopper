package net.cyberkatyusha.aggressionstopper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
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
public class AggressionStopperApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AggressionStopperApplication.class);

    private final AggressionsStopperService aggressionsStopperService;

    public AggressionStopperApplication(AggressionsStopperService aggressionsStopperService) {
        this.aggressionsStopperService = aggressionsStopperService;
    }

    public static void main(String[] args) {
        logger.info("Starting the application");

        SpringApplication application = new SpringApplication(AggressionStopperApplication.class);

        application.setWebApplicationType(WebApplicationType.NONE);

        application.run(args);

        logger.info("Application finished");
    }

    @Override
    public void run(String... args) {

        Instant start = java.time.Instant.now();

        aggressionsStopperService.execute();

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
