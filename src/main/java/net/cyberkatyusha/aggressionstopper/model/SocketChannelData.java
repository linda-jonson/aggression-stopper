package net.cyberkatyusha.aggressionstopper.model;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.time.Instant;
import java.util.concurrent.Future;

public class SocketChannelData {

    private AsynchronousSocketChannel socketChannel;
    private ByteBuffer byteBuffer;
    private SocketChannelStatus status;
    private Instant connectionStart;
    private Instant dataWriteStart;
    private Future<Void> connectionStatus;
    private Future<Integer> writeStatus;

    public SocketChannelData() {
    }

    public SocketChannelData(AsynchronousSocketChannel socketChannel, ByteBuffer byteBuffer, SocketChannelStatus status) {
        this.socketChannel = socketChannel;
        this.byteBuffer = byteBuffer;
        this.status = status;
    }

    public AsynchronousSocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public SocketChannelStatus getStatus() {
        return status;
    }

    public void setStatus(SocketChannelStatus status) {
        this.status = status;
    }

    public Instant getConnectionStart() {
        return connectionStart;
    }

    public void setConnectionStart(Instant connectionStart) {
        this.connectionStart = connectionStart;
    }

    public Instant getDataWriteStart() {
        return dataWriteStart;
    }

    public void setDataWriteStart(Instant dataWriteStart) {
        this.dataWriteStart = dataWriteStart;
    }

    public Future<Void> getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(Future<Void> connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public Future<Integer> getWriteStatus() {
        return writeStatus;
    }

    public void setWriteStatus(Future<Integer> writeStatus) {
        this.writeStatus = writeStatus;
    }
}
