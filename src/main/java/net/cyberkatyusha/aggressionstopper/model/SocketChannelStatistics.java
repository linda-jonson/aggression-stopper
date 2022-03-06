package net.cyberkatyusha.aggressionstopper.model;

public class SocketChannelStatistics {

    private Long connectCount;
    private Long connectSuccessCount;
    private Long connectTimeoutCount;
    private Long connectErrorCount;
    private Long sendDataCount;
    private Long sendDataSuccessCount;
    private Long sendDataTimeoutCount;
    private Long sendDataErrorCount;

    public Long getConnectCount() {
        return connectCount;
    }

    public void setConnectCount(Long connectCount) {
        this.connectCount = connectCount;
    }

    public Long getConnectSuccessCount() {
        return connectSuccessCount;
    }

    public void setConnectSuccessCount(Long connectSuccessCount) {
        this.connectSuccessCount = connectSuccessCount;
    }

    public Long getConnectTimeoutCount() {
        return connectTimeoutCount;
    }

    public void setConnectTimeoutCount(Long connectTimeoutCount) {
        this.connectTimeoutCount = connectTimeoutCount;
    }

    public Long getConnectErrorCount() {
        return connectErrorCount;
    }

    public void setConnectErrorCount(Long connectErrorCount) {
        this.connectErrorCount = connectErrorCount;
    }

    public Long getSendDataCount() {
        return sendDataCount;
    }

    public void setSendDataCount(Long sendDataCount) {
        this.sendDataCount = sendDataCount;
    }

    public Long getSendDataSuccessCount() {
        return sendDataSuccessCount;
    }

    public void setSendDataSuccessCount(Long sendDataSuccessCount) {
        this.sendDataSuccessCount = sendDataSuccessCount;
    }

    public Long getSendDataTimeoutCount() {
        return sendDataTimeoutCount;
    }

    public void setSendDataTimeoutCount(Long sendDataTimeoutCount) {
        this.sendDataTimeoutCount = sendDataTimeoutCount;
    }

    public Long getSendDataErrorCount() {
        return sendDataErrorCount;
    }

    public void setSendDataErrorCount(Long sendDataErrorCount) {
        this.sendDataErrorCount = sendDataErrorCount;
    }
}
