package net.cyberkatyusha.aggressionstopper.model;

import java.net.URI;
import java.util.concurrent.Future;

public class RequestFuture {

    private URI uri;
    private Future<RequestExecutionResult> future;

    public RequestFuture() {
    }

    public RequestFuture(URI uri, Future<RequestExecutionResult> future) {
        this.uri = uri;
        this.future = future;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Future<RequestExecutionResult> getFuture() {
        return future;
    }

    public void setFuture(Future<RequestExecutionResult> future) {
        this.future = future;
    }
}
