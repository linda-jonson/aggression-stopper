package net.cyberkatyusha.aggressionstopper.model;

public class SocketClientSettings {

    private Integer connectTimeoutMillis;
    private Integer soTimeoutMillis;
    private Boolean useLocalAddress;
    private String localAddress;

    public Integer getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(Integer connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public Integer getSoTimeoutMillis() {
        return soTimeoutMillis;
    }

    public void setSoTimeoutMillis(Integer soTimeoutMillis) {
        this.soTimeoutMillis = soTimeoutMillis;
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
}
