package net.cyberkatyusha.aggressionstopper.model;

import java.util.concurrent.Future;

public class SocketChannelRequestFuture {

    private Long id;
    private TcpAddress address;
    private Future<SocketChannelRequestExecutionResult> future;

    public SocketChannelRequestFuture() {
    }

    public SocketChannelRequestFuture(Long id, TcpAddress address, Future<SocketChannelRequestExecutionResult> future) {
        this.id = id;
        this.address = address;
        this.future = future;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TcpAddress getAddress() {
        return address;
    }

    public void setAddress(TcpAddress address) {
        this.address = address;
    }

    public Future<SocketChannelRequestExecutionResult> getFuture() {
        return future;
    }

    public void setFuture(Future<SocketChannelRequestExecutionResult> future) {
        this.future = future;
    }
}