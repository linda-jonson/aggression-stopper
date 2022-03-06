package net.cyberkatyusha.aggressionstopper.model;

public class SocketChannelSettings {

    private Integer connectTimeoutMillis;
    private Integer writeTimeoutMillis;
    private Boolean useLocalAddress;
    private String localAddress;
    private Integer channelCount;
    private Long finalStateMachineDelayMillis;
    private Long connectionSequenceMax;

    public Integer getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(Integer connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public Integer getWriteTimeoutMillis() {
        return writeTimeoutMillis;
    }

    public void setWriteTimeoutMillis(Integer writeTimeoutMillis) {
        this.writeTimeoutMillis = writeTimeoutMillis;
    }

    public Boolean getUseLocalAddress() {
        return useLocalAddress;
    }

    public void setUseLocalAddress(Boolean useLocalAddress) {
        this.useLocalAddress = useLocalAddress;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public Integer getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(Integer channelCount) {
        this.channelCount = channelCount;
    }

    public Long getFinalStateMachineDelayMillis() {
        return finalStateMachineDelayMillis;
    }

    public void setFinalStateMachineDelayMillis(Long finalStateMachineDelayMillis) {
        this.finalStateMachineDelayMillis = finalStateMachineDelayMillis;
    }

    public Long getConnectionSequenceMax() {
        return connectionSequenceMax;
    }

    public void setConnectionSequenceMax(Long connectionSequenceMax) {
        this.connectionSequenceMax = connectionSequenceMax;
    }
}
