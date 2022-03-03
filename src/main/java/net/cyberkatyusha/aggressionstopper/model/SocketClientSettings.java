package net.cyberkatyusha.aggressionstopper.model;

public class SocketClientSettings {

    private Integer connectTimeoutMillis;
    private Integer soTimeoutMillis;

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
}
