package net.cyberkatyusha.aggressionstopper.model;

public class TcpAddress {

    private String host;
    private Integer port;

    public TcpAddress() {
    }

    public TcpAddress(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
