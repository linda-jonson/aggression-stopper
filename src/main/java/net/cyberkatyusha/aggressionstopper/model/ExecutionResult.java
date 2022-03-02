package net.cyberkatyusha.aggressionstopper.model;

import java.util.List;

public class ExecutionResult {

    private List<RequestExecutionResult> requestExecutionResults;

    public List<RequestExecutionResult> getRequestExecutionResults() {
        return requestExecutionResults;
    }

    public void setRequestExecutionResults(List<RequestExecutionResult> requestExecutionResults) {
        this.requestExecutionResults = requestExecutionResults;
    }
}
