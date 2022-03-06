package net.cyberkatyusha.aggressionstopper.model;

public final class SocketChannelStatisticsBuilder {
    private Long connectCount;
    private Long connectSuccessCount;
    private Long connectTimeoutCount;
    private Long connectErrorCount;
    private Long sendDataCount;
    private Long sendDataSuccessCount;
    private Long sendDataTimeoutCount;
    private Long sendDataErrorCount;

    private SocketChannelStatisticsBuilder() {
    }

    public static SocketChannelStatisticsBuilder aSocketChannelStatistics() {
        return new SocketChannelStatisticsBuilder();
    }

    public SocketChannelStatisticsBuilder withConnectCount(Long connectCount) {
        this.connectCount = connectCount;
        return this;
    }

    public SocketChannelStatisticsBuilder withConnectSuccessCount(Long connectSuccessCount) {
        this.connectSuccessCount = connectSuccessCount;
        return this;
    }

    public SocketChannelStatisticsBuilder withConnectTimeoutCount(Long connectTimeoutCount) {
        this.connectTimeoutCount = connectTimeoutCount;
        return this;
    }

    public SocketChannelStatisticsBuilder withConnectErrorCount(Long connectErrorCount) {
        this.connectErrorCount = connectErrorCount;
        return this;
    }

    public SocketChannelStatisticsBuilder withSendDataCount(Long sendDataCount) {
        this.sendDataCount = sendDataCount;
        return this;
    }

    public SocketChannelStatisticsBuilder withSendDataSuccessCount(Long sendDataSuccessCount) {
        this.sendDataSuccessCount = sendDataSuccessCount;
        return this;
    }

    public SocketChannelStatisticsBuilder withSendDataTimeoutCount(Long sendDataTimeoutCount) {
        this.sendDataTimeoutCount = sendDataTimeoutCount;
        return this;
    }

    public SocketChannelStatisticsBuilder withSendDataErrorCount(Long sendDataErrorCount) {
        this.sendDataErrorCount = sendDataErrorCount;
        return this;
    }

    public SocketChannelStatistics build() {
        SocketChannelStatistics socketChannelStatistics = new SocketChannelStatistics();
        socketChannelStatistics.setConnectCount(connectCount);
        socketChannelStatistics.setConnectSuccessCount(connectSuccessCount);
        socketChannelStatistics.setConnectTimeoutCount(connectTimeoutCount);
        socketChannelStatistics.setConnectErrorCount(connectErrorCount);
        socketChannelStatistics.setSendDataCount(sendDataCount);
        socketChannelStatistics.setSendDataSuccessCount(sendDataSuccessCount);
        socketChannelStatistics.setSendDataTimeoutCount(sendDataTimeoutCount);
        socketChannelStatistics.setSendDataErrorCount(sendDataErrorCount);
        return socketChannelStatistics;
    }
}
