package net.cyberkatyusha.aggressionstopper.model;

import java.net.URI;

public class RequestExecutionResult {

    private URI uri;
    private Boolean result;

    public RequestExecutionResult() {
    }

    public RequestExecutionResult(URI uri, Boolean result) {
        this.uri = uri;
        this.result = result;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

}
