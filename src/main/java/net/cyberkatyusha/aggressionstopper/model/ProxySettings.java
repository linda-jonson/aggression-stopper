package net.cyberkatyusha.aggressionstopper.model;

import reactor.netty.transport.ProxyProvider;

public class ProxySettings {

    private Boolean enabled;
    private ProxyProvider.Proxy proxyType;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private Integer connectTimeoutMillis;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ProxyProvider.Proxy getProxyType() {
        return proxyType;
    }

    public void setProxyType(ProxyProvider.Proxy proxyType) {
        this.proxyType = proxyType;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(Integer connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }
}
