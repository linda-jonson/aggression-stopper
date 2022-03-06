package net.cyberkatyusha.aggressionstopper.model;

public class SocketChannelRequestExecutionResult {

    private TcpAddress address;
    private SocketChannelStatistics statistics;

    public SocketChannelRequestExecutionResult() {
    }

    public SocketChannelRequestExecutionResult(TcpAddress address, SocketChannelStatistics statistics) {
        this.address = address;
        this.statistics = statistics;
    }

    public TcpAddress getAddress() {
        return address;
    }

    public void setAddress(TcpAddress address) {
        this.address = address;
    }

    public SocketChannelStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(SocketChannelStatistics statistics) {
        this.statistics = statistics;
    }
}
