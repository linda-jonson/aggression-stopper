package net.cyberkatyusha.aggressionstopper.model;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.Instant;

public class SocketChannelData {

    private SocketChannel socketChannel;
    private ByteBuffer byteBuffer;
    private SocketChannelStatus status;
    private Instant connectionStart;
    private Instant dataWriteStart;

    public SocketChannelData() {
    }

    public SocketChannelData(SocketChannel socketChannel, ByteBuffer byteBuffer, SocketChannelStatus status) {
        this.socketChannel = socketChannel;
        this.byteBuffer = byteBuffer;
        this.status = status;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
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
}
