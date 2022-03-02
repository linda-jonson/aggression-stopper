package net.cyberkatyusha.aggressionstopper.config;


import net.cyberkatyusha.aggressionstopper.model.HttpClientSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "aggression-stopper-settings")
public class AggressionStopperSettings {

    private HttpClientSettings httpClientSettings;
    private Integer requestCount;
    private Integer requestRetryCount;
    private Integer requestsRepeatCount;
    private Integer parallelism;
    private Integer commonExecutionTimeoutMillis;
    private Integer requestsDelayMillis;
    private Integer threadCount;
    private List<URI> uris;
    private Long logEachItemNumber;

    public HttpClientSettings getHttpClientSettings() {
        return httpClientSettings;
    }

    public void setHttpClientSettings(HttpClientSettings httpClientSettings) {
        this.httpClientSettings = httpClientSettings;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public Integer getRequestRetryCount() {
        return requestRetryCount;
    }

    public void setRequestRetryCount(Integer requestRetryCount) {
        this.requestRetryCount = requestRetryCount;
    }

    public Integer getRequestsRepeatCount() {
        return requestsRepeatCount;
    }

    public void setRequestsRepeatCount(Integer requestsRepeatCount) {
        this.requestsRepeatCount = requestsRepeatCount;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public Integer getCommonExecutionTimeoutMillis() {
        return commonExecutionTimeoutMillis;
    }

    public void setCommonExecutionTimeoutMillis(Integer commonExecutionTimeoutMillis) {
        this.commonExecutionTimeoutMillis = commonExecutionTimeoutMillis;
    }

    public Integer getRequestsDelayMillis() {
        return requestsDelayMillis;
    }

    public void setRequestsDelayMillis(Integer requestsDelayMillis) {
        this.requestsDelayMillis = requestsDelayMillis;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public List<URI> getUris() {
        return uris;
    }

    public void setUris(List<URI> uris) {
        this.uris = uris;
    }

    public Long getLogEachItemNumber() {
        return logEachItemNumber;
    }

    public void setLogEachItemNumber(Long logEachItemNumber) {
        this.logEachItemNumber = logEachItemNumber;
    }
}
