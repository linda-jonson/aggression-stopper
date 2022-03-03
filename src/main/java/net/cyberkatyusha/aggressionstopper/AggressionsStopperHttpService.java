package net.cyberkatyusha.aggressionstopper;

import net.cyberkatyusha.aggressionstopper.config.AggressionStopperSettings;
import net.cyberkatyusha.aggressionstopper.model.HttpClientRequestData;
import net.cyberkatyusha.aggressionstopper.model.RequestExecutionResult;
import net.cyberkatyusha.aggressionstopper.model.RequestFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class AggressionsStopperHttpService {

    private static final Logger logger = LoggerFactory.getLogger(AggressionsStopperHttpService.class);

    private final AggressionStopperSettings settings;

    public AggressionsStopperHttpService(AggressionStopperSettings settings) {
        this.settings = settings;
    }

    public void execute() {

        logger.info("Execution started");

        Long requestNumber = 0L;

        List<RequestFuture> requestFutureList = new ArrayList<>();

        ExecutorService executorService = Executors.newCachedThreadPool();

        Map<URI, HttpClientRequestData> clientRequestDataMap = createHttpClientRequestMap(settings.getUris());

        for (URI uri : settings.getUris()) {
            for (int i = 0; i < settings.getRequestCount(); i++) {
                HttpClientRequestData httpClientRequestData = clientRequestDataMap.get(uri);
                Future<RequestExecutionResult> future = executorService.submit(() -> sendRequest(httpClientRequestData));
                requestFutureList.add(new RequestFuture(uri, future));
            }
        }

        while (requestNumber < settings.getRequestsRepeatCount()) {

            List<RequestFuture> doneFutureList = new ArrayList<>();
            List<RequestFuture> canceledFutureList = new ArrayList<>();

            for (RequestFuture requestFuture : requestFutureList) {

                Future<RequestExecutionResult> future = requestFuture.getFuture();

                if (future.isDone()) {
                    doneFutureList.add(requestFuture);
                    RequestExecutionResult requestExecutionResult = getRequestResult(requestFuture.getUri(), future);
                } else if (future.isCancelled()) {
                    canceledFutureList.add(requestFuture);
                }

                if (future.isDone() || future.isCancelled()) {
                    requestNumber++;
                    if (requestNumber % settings.getLogEachItemNumber() == 0) {
                        logger.info("Request number: %d".formatted(requestNumber));
                    }
                }
            }

            requestFutureList.removeAll(doneFutureList);
            requestFutureList.removeAll(canceledFutureList);

            for (RequestFuture requestFuture : doneFutureList) {
                Future<RequestExecutionResult> future = executorService.submit(() -> sendRequest(
                        clientRequestDataMap.get(requestFuture.getUri()))
                );
                requestFutureList.add(new RequestFuture(requestFuture.getUri(), future));
            }

            for (RequestFuture requestFuture : canceledFutureList) {
                Future<RequestExecutionResult> future = executorService.submit(() -> sendRequest(
                        clientRequestDataMap.get(requestFuture.getUri()))
                );
                requestFutureList.add(new RequestFuture(requestFuture.getUri(), future));
            }

            try {
                Thread.sleep(settings.getRequestsDelayMillis());
            } catch (InterruptedException e) {
                //do nothing
            }

        }

        executorService.shutdown();

        logger.info("Execution finished");

    }

    private RequestExecutionResult getRequestResult(URI uri, Future<RequestExecutionResult> future) {

        RequestExecutionResult result;

        try {
            result = future.get();
        } catch (Throwable t) {
            result = new RequestExecutionResult(uri, false);
        }

        return result;
    }

    private Map<URI, HttpClientRequestData> createHttpClientRequestMap(List<URI> uriList) {
        Map<URI, HttpClientRequestData> result = new HashMap<>();
        uriList.forEach(
                uri -> {
                    result.put(uri, createHttpClientRequestData(uri));
                }
        );
        return result;
    }

    private HttpClientRequestData createHttpClientRequestData(URI uri) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofMillis(settings.getHttpClientSettings().getConnectTimeoutMillis()))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofMillis(settings.getHttpClientSettings().getResponseTimeoutMillis()))
                .header("Proxy-Connection", "keep-alive")
                .header("Keep-Alive", "timeout=600")
                .header("Keep-Alive", "max=999999")
                .GET()
                .uri(uri)
                .build();

        return new HttpClientRequestData(client, request);
    }

    private RequestExecutionResult sendRequest(HttpClientRequestData clientRequestData) {

        HttpClient client = clientRequestData.getHttpClient();
        HttpRequest request = clientRequestData.getRequest();

        final HttpResponse<String> response;
        boolean success;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            HttpStatus statusCode = HttpStatus.resolve(response.statusCode());

            assert statusCode != null;
            success = statusCode.is2xxSuccessful() ||
                    statusCode.is1xxInformational() ||
                    statusCode.is3xxRedirection();

        } catch (Throwable t) {
            success = false;
        }

        return new RequestExecutionResult(request.uri(), success);

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

}
