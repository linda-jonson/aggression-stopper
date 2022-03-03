package net.cyberkatyusha.aggressionstopper.model;

public class TcpRequestExecutionResult {

    private TcpAddress address;
    private Boolean result;

    public TcpRequestExecutionResult() {
    }

    public TcpRequestExecutionResult(TcpAddress address, Boolean result) {
        this.address = address;
        this.result = result;
    }

    public TcpAddress getAddress() {
        return address;
    }

    public void setAddress(TcpAddress address) {
        this.address = address;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }
}
