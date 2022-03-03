package net.cyberkatyusha.aggressionstopper.model;

import java.util.concurrent.Future;

public class TcpRequestFuture {

    private TcpAddress address;
    private Future<TcpRequestExecutionResult> future;

    public TcpRequestFuture() {
    }

    public TcpRequestFuture(TcpAddress address, Future<TcpRequestExecutionResult> future) {
        this.address = address;
        this.future = future;
    }

    public TcpAddress getAddress() {
        return address;
    }

    public void setAddress(TcpAddress address) {
        this.address = address;
    }

    public Future<TcpRequestExecutionResult> getFuture() {
        return future;
    }

    public void setFuture(Future<TcpRequestExecutionResult> future) {
        this.future = future;
    }
}
