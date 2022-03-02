package net.cyberkatyusha.aggressionstopper;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import net.cyberkatyusha.aggressionstopper.config.AggressionStopperSettings;
import net.cyberkatyusha.aggressionstopper.model.RequestExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AggressionsStopperService {

    private static final Logger logger = LoggerFactory.getLogger(AggressionsStopperService.class);

    private final AggressionStopperSettings settings;

    public AggressionsStopperService(AggressionStopperSettings settings) {
        this.settings = settings;
    }

    public void execute() {

        logger.info("Execution started");

        final SslContext sslContext = getSslContext();

        List<Mono<RequestExecutionResult>> webClients = new ArrayList<>();

        ReactorClientHttpConnector reactorClientHttpConnector = getReactorClientHttpConnector(sslContext);

        AtomicLong order = new AtomicLong(0);

        for (URI uri : settings.getUris()) {

            for (int i = 0; i < settings.getRequestCount(); i++) {

                var webClient = WebClient.builder()
                        .clientConnector(reactorClientHttpConnector)
                        .defaultHeader("Proxy-Connection", "keep-alive")
                        .defaultHeader("Connection", "keep-alive")
                        .defaultHeader("Keep-Alive", "timeout=600", "max=999999")
                        .build()
                        .get()
                        .uri(uri);

                var mono = webClient
                        .exchangeToMono(
                                clientResponse -> {

                                    HttpStatus statusCode = clientResponse.statusCode();

                                    Boolean success = statusCode.is2xxSuccessful() ||
                                            statusCode.is1xxInformational() ||
                                            statusCode.is3xxRedirection();

                                    return Mono.just(new RequestExecutionResult(uri, success));

                                }
                        )
                        .retry(settings.getRequestRetryCount())
                        .onErrorReturn(new RequestExecutionResult(uri, false))
                        .doOnNext(
                                requestExecutionResult -> {

                                    long num = order.incrementAndGet();

                                    if (num % settings.getLogEachItemNumber() == 0) {
                                        logger.info("Request number: %d, uri: %s".formatted(num, uri));
                                    }

                                }
                        );

                webClients.add(mono);

            }

        }

        for (int i = 0; i < settings.getRequestsRepeatCount(); i++) {

            logger.info("Execution loop number: %d".formatted(i));

            List<RequestExecutionResult> requestExecutionResultList = Flux.fromIterable(webClients)
                    .parallel(settings.getParallelism())
                    .runOn(Schedulers.newBoundedElastic(settings.getThreadCount(), Integer.MAX_VALUE, "RequestExecutor"))
                    .flatMap(Function.identity())
                    .collectSortedList(Comparator.comparing(RequestExecutionResult::getUri))
                    .onErrorReturn(Collections.emptyList())
                    .block(Duration.ofSeconds(settings.getCommonExecutionTimeoutMillis()));

            assert requestExecutionResultList != null;
            logStatistics(requestExecutionResultList);

        }

        logger.info("Execution finished");

    }

    private void logStatistics(List<RequestExecutionResult> requestExecutionResultList) {

        logger.info("Total request count: %d".formatted((long) requestExecutionResultList.size()));

        var byUri = requestExecutionResultList
                .stream()
                .collect(Collectors.groupingBy(RequestExecutionResult::getUri));

        byUri.forEach(
                (uri, requestExecutionResults) -> {

                    var byResult = requestExecutionResults
                            .stream()
                            .collect(Collectors.groupingBy(RequestExecutionResult::getResult, Collectors.counting()));

                    logger.info(
                            "Uri: '%s', success request count: %d, failed request count: %d"
                                    .formatted(
                                            uri,
                                            Optional.ofNullable(byResult.get(Boolean.TRUE)).orElse(0L),
                                            Optional.ofNullable(byResult.get(Boolean.FALSE)).orElse(0L)
                                    )
                    );

                }
        );

    }

    private SslContext getSslContext() {
        final SslContext sslContext;
        try {
            sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } catch (SSLException e) {
            throw new ApplicationException(e);
        }
        return sslContext;
    }

    private ReactorClientHttpConnector getReactorClientHttpConnector(SslContext sslContext) {

        var httpClientSettings = settings.getHttpClientSettings();

        HttpClient httpClient = HttpClient
                .create()
                .followRedirect(true)
                .option(ChannelOption.SO_KEEPALIVE, httpClientSettings.getConnectionKeepAlive())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, httpClientSettings.getConnectTimeoutMillis())
                .responseTimeout(Duration.ofMillis(httpClientSettings.getResponseTimeoutMillis()))
                .compress(true)
                .secure(t -> t.sslContext(sslContext));

        var proxySettings = settings.getHttpClientSettings().getProxy();

        if (proxySettings.getEnabled()) {
            httpClient.proxy(
                    proxy -> {
                        proxy
                                .type(proxySettings.getProxyType())
                                .host(proxySettings.getHost())
                                .port(proxySettings.getPort())
                                .connectTimeoutMillis(proxySettings.getConnectTimeoutMillis())
                                .build();
                    }
            );

        }

        httpClient.doOnConnected(
                conn ->
                {
                    conn.addHandlerLast(
                                    new ReadTimeoutHandler(
                                            httpClientSettings.getReadTimeoutMillis(),
                                            TimeUnit.MILLISECONDS
                                    )
                            )
                            .addHandlerLast(
                                    new WriteTimeoutHandler(
                                            httpClientSettings.getWriteTimeoutMillis(),
                                            TimeUnit.MILLISECONDS
                                    )
                            );
                }
        );

        return new ReactorClientHttpConnector(httpClient);
    }

}
