package net.cyberkatyusha.aggressionstopper.model;

public class HttpClientSettings {

    private ProxySettings proxy;
    private Boolean connectionKeepAlive;
    private Integer connectTimeoutMillis;
    private Integer responseTimeoutMillis;
    private Integer readTimeoutMillis;
    private Integer writeTimeoutMillis;

    public ProxySettings getProxy() {
        return proxy;
    }

    public void setProxy(ProxySettings proxy) {
        this.proxy = proxy;
    }

    public Boolean getConnectionKeepAlive() {
        return connectionKeepAlive;
    }

    public void setConnectionKeepAlive(Boolean connectionKeepAlive) {
        this.connectionKeepAlive = connectionKeepAlive;
    }

    public Integer getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(Integer connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public Integer getResponseTimeoutMillis() {
        return responseTimeoutMillis;
    }

    public void setResponseTimeoutMillis(Integer responseTimeoutMillis) {
        this.responseTimeoutMillis = responseTimeoutMillis;
    }

    public Integer getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(Integer readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public Integer getWriteTimeoutMillis() {
        return writeTimeoutMillis;
    }

    public void setWriteTimeoutMillis(Integer writeTimeoutMillis) {
        this.writeTimeoutMillis = writeTimeoutMillis;
    }
}
