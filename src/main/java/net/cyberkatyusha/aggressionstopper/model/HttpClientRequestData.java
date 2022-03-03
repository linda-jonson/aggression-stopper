package net.cyberkatyusha.aggressionstopper.model;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class HttpClientRequestData {

    private HttpClient httpClient;
    private HttpRequest request;

    public HttpClientRequestData() {
    }

    public HttpClientRequestData(HttpClient httpClient, HttpRequest request) {
        this.httpClient = httpClient;
        this.request = request;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }
}
