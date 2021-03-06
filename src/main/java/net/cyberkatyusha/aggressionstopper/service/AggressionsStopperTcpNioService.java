package net.cyberkatyusha.aggressionstopper.service;

import net.cyberkatyusha.aggressionstopper.config.AggressionStopperSettings;
import net.cyberkatyusha.aggressionstopper.exception.ApplicationException;
import net.cyberkatyusha.aggressionstopper.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AggressionsStopperTcpNioService {

    private static final Logger logger = LoggerFactory.getLogger(AggressionsStopperTcpNioService.class);

    private final AggressionStopperSettings settings;

    public AggressionsStopperTcpNioService(AggressionStopperSettings settings) {
        this.settings = settings;
    }

    public void execute() {

        logger.info("Execution started");

        Long requestNumber = 0L;

        List<SocketChannelRequestFuture> requestFutureList = new ArrayList<>();

        ExecutorService executorService = Executors.newCachedThreadPool();

        byte[] firstRandomBytes = getRandomBytes();

        long futureId = 0;

        for (TcpAddress address : settings.getTcpAddresses()) {
            for (int i = 0; i < settings.getRequestCount(); i++) {

                futureId++;

                long finalFutureId = futureId;

                Future<SocketChannelRequestExecutionResult> future = executorService.submit(
                        () -> sendRequest(finalFutureId, address, firstRandomBytes)
                );

                requestFutureList.add(new SocketChannelRequestFuture(futureId, address, future));
            }
        }

        logger.info("Socket Channel Futures created");

        while (requestNumber < settings.getRequestsRepeatCount()) {

            byte[] secondRandomBytes = getRandomBytes();

            List<SocketChannelRequestFuture> doneFutureList = new ArrayList<>();
            List<SocketChannelRequestFuture> canceledFutureList = new ArrayList<>();

            for (SocketChannelRequestFuture requestFuture : requestFutureList) {

                Future<SocketChannelRequestExecutionResult> future = requestFuture.getFuture();

                if (future.isDone()) {
                    doneFutureList.add(requestFuture);
                    SocketChannelRequestExecutionResult requestExecutionResult = getRequestResult(
                            requestFuture.getAddress(),
                            future
                    );
                } else if (future.isCancelled()) {
                    canceledFutureList.add(requestFuture);
                }

                if (future.isDone() || future.isCancelled()) {
                    requestNumber++;
                    if (requestNumber % settings.getLogEachItemNumber() == 0) {
                        logger.info("Request number: %d".formatted(requestNumber));
                    }
                }
            }

            requestFutureList.removeAll(doneFutureList);
            requestFutureList.removeAll(canceledFutureList);

            for (SocketChannelRequestFuture requestFuture : doneFutureList) {

                Future<SocketChannelRequestExecutionResult> future = executorService.submit(
                        () -> sendRequest(requestFuture.getId(),
                                requestFuture.getAddress(),
                                secondRandomBytes
                        )
                );

                requestFutureList.add(new SocketChannelRequestFuture(++futureId, requestFuture.getAddress(), future));
            }

            for (SocketChannelRequestFuture requestFuture : canceledFutureList) {
                Future<SocketChannelRequestExecutionResult> future = executorService.submit(
                        () -> sendRequest(requestFuture.getId(),
                                requestFuture.getAddress(),
                                secondRandomBytes
                        )
                );

                requestFutureList.add(new SocketChannelRequestFuture(++futureId, requestFuture.getAddress(), future));
            }

            try {
                Thread.sleep(settings.getRequestsDelayMillis());
            } catch (InterruptedException e) {
                //do nothing
            }

        }

        executorService.shutdown();

        logger.info("Execution finished");

    }

    private SocketChannelRequestExecutionResult getRequestResult(TcpAddress address, Future<SocketChannelRequestExecutionResult> future) {

        SocketChannelRequestExecutionResult result;

        try {
            result = future.get();
        } catch (Throwable t) {
            result = new SocketChannelRequestExecutionResult(
                    address,
                    SocketChannelStatisticsBuilder
                            .aSocketChannelStatistics()
                            .withConnectCount(0L)
                            .withConnectSuccessCount(0L)
                            .withConnectTimeoutCount(0L)
                            .withConnectErrorCount(0L)
                            .withSendDataCount(0L)
                            .withSendDataSuccessCount(0L)
                            .withSendDataTimeoutCount(0L)
                            .withSendDataErrorCount(0L)
                            .build()
            );
        }

        return result;
    }

    private SocketChannelRequestExecutionResult sendRequest(long id, TcpAddress address, byte[] bytes) {

        final int channelCount = settings.getSocketChannelSettings().getChannelCount();

        List<SocketChannelData> socketChannelDataList = new ArrayList<>();

        String remoteHostAddressAsString;

        try {
            remoteHostAddressAsString = InetAddress.getByName(address.getHost()).getHostAddress();

            logger.info("Calculated remote host address: '%s'".formatted(remoteHostAddressAsString));

        } catch (UnknownHostException e) {
            logger.error("Can not resolve remote host: '%s'".formatted(address.getHost()), e);
            return new SocketChannelRequestExecutionResult(
                    address,
                    SocketChannelStatisticsBuilder
                            .aSocketChannelStatistics()
                            .withConnectCount(0L)
                            .withConnectSuccessCount(0L)
                            .withConnectTimeoutCount(0L)
                            .withConnectErrorCount(0L)
                            .withSendDataCount(0L)
                            .withSendDataSuccessCount(0L)
                            .withSendDataTimeoutCount(0L)
                            .withSendDataErrorCount(0L)
                            .build()
            );
        }

        InetSocketAddress remoteAddress = new InetSocketAddress(remoteHostAddressAsString, address.getPort());

        long totalConnectCount = 0;
        long totalConnectSuccessCount = 0;
        long totalConnectTimeoutCount = 0;
        long totalConnectErrorCount = 0;
        long totalSendDataCount = 0;
        long totalSendDataSuccessCount = 0;
        long totalSendDataTimeoutCount = 0;
        long totalSendDataErrorCount = 0;

        try {

            for (int i = 0; i < channelCount; i++) {

                try {

                    SocketChannelData socketChannelData = createSocketChannelData(bytes);
                    socketChannelDataList.add(socketChannelData);

                } catch (Throwable t) {
                    logger.error(
                            "Can not open SocketChannel: '%s', iteration number: %d".formatted(address.getHost(), i),
                            t
                    );
                }
            }

            InetSocketAddress localAddress = null;

            //Get local address if it is set to use in settings
            if (settings.getSocketChannelSettings().getUseLocalAddress()) {

                final String localHostAsString = settings.getSocketChannelSettings().getLocalAddress();
                final String localHostAddressAsString;

                try {
                    localHostAddressAsString = InetAddress.getByName(localHostAsString).getHostAddress();

                    logger.info("Calculated local host address: '%s'".formatted(localHostAddressAsString));

                } catch (Throwable t) {
                    throw new ApplicationException(
                            "Can not resolve local host: id: %d, remote address: '%s:%d', local address: '%s' '%s'"
                                    .formatted(
                                            id,
                                            address.getHost(),
                                            address.getPort(),
                                            localHostAsString,
                                            t.getMessage()
                                    ),
                            t
                    );
                }

                localAddress = new InetSocketAddress(localHostAddressAsString, 0);
            }

            //Set local address if it is set to use in settings
            if (settings.getSocketChannelSettings().getUseLocalAddress()) {
                for (SocketChannelData socketChannelData : socketChannelDataList) {
                    try {
                        socketChannelData.getSocketChannel().bind(localAddress);
                    } catch (Throwable t) {

                        for (SocketChannelData scd : socketChannelDataList) {
                            try {
                                scd.getSocketChannel().close();
                            } catch (Throwable ignored) {
                            }
                        }

                        throw new ApplicationException(
                                (
                                        "Can not set local address to SocketChannel," +
                                                " id: %d," +
                                                " remote address: %s:%d," +
                                                " local address: %s" +
                                                " error: '%s'"
                                )
                                        .formatted(
                                                id,
                                                address.getHost(),
                                                address.getPort(),
                                                settings.getSocketChannelSettings().getLocalAddress(),
                                                t.getMessage()
                                        ),
                                t
                        );
                    }
                }

                logger.info("Set local host address '%s' to socket Channels."
                        .formatted(settings.getSocketChannelSettings().getLocalAddress())
                );

            }

            long connectCount;
            long connectSuccessCount;
            long connectTimeoutCount;
            long connectErrorCount;
            long sendDataCount;
            long sendDataSuccessCount;
            long sendDataTimeoutCount;
            long sendDataErrorCount;

            final long connectionSequenceMax = settings.getSocketChannelSettings().getConnectionSequenceMax();

            logger.info(
                    "Read connection sequence max: %d, socket channel data list size: %d"
                            .formatted(connectionSequenceMax, socketChannelDataList.size())
            );

            Instant loopStartTime = java.time.Instant.now();

            while (socketChannelDataList.size() > 0 && totalConnectCount < connectionSequenceMax) {

                connectCount = 0;
                connectSuccessCount = 0;
                connectTimeoutCount = 0;
                connectErrorCount = 0;
                sendDataCount = 0;
                sendDataSuccessCount = 0;
                sendDataTimeoutCount = 0;
                sendDataErrorCount = 0;

                List<SocketChannelData> socketChannelDataToAddList = new ArrayList<>();
                List<SocketChannelData> socketChannelDataToRemoveList = new ArrayList<>();

                for (SocketChannelData channelData : socketChannelDataList) {
                    try {

                        if (SocketChannelStatus.OPENED.equals(channelData.getStatus())) {

                            channelData.setConnectionStart(java.time.Instant.now());

                            try {

                                connectCount++;
                                channelData.setStatus(SocketChannelStatus.CONNECTING);
                                channelData.setConnectionStatus(channelData.getSocketChannel().connect(remoteAddress));

                            } catch (Throwable t) {

                                logger.debug(
                                        (
                                                "Can not establish connection: id: %d, address: " +
                                                        "'%s:%d', status: '%s', error: '%s'"
                                        )
                                                .formatted(
                                                        id,
                                                        address.getHost(),
                                                        address.getPort(),
                                                        channelData.getStatus(),
                                                        t.getMessage()
                                                )
                                );

                                try {
                                    channelData.getSocketChannel().close();
                                } catch (Throwable ignored) {
                                }

                                channelData.setStatus(SocketChannelStatus.ERROR);
                                socketChannelDataToRemoveList.add(channelData);
                                connectErrorCount++;

                            }

                        } else if (SocketChannelStatus.CONNECTING.equals(channelData.getStatus())) {

                            if (!channelData.getConnectionStatus().isDone()) {

                                Instant end = java.time.Instant.now();
                                Duration between = Duration.between(channelData.getConnectionStart(), end);

                                Integer connectTimeoutMillis = settings.getSocketChannelSettings().getConnectTimeoutMillis();

                                if (between.toMillis() > connectTimeoutMillis) {

                                    channelData.getConnectionStatus().cancel(true);

                                    try {
                                        channelData.getSocketChannel().close();
                                    } catch (Throwable ignored) {
                                    }

                                    channelData.setStatus(SocketChannelStatus.TIMEOUT);
                                    socketChannelDataToRemoveList.add(channelData);

                                    connectTimeoutCount++;

                                    logger.debug(
                                            (
                                                    "Connect timeout: id: %d, address: " +
                                                            "'%s:%d', status: '%s', timeout mills: %d"
                                            )
                                                    .formatted(
                                                            id,
                                                            address.getHost(),
                                                            address.getPort(),
                                                            channelData.getStatus(),
                                                            connectTimeoutMillis
                                                    )
                                    );

                                }

                            } else if (channelData.getConnectionStatus().isDone()) {

                                boolean connectedSuccessfully;

                                try {

                                    if (Objects.isNull(channelData.getConnectionStatus().get())) {
                                        connectedSuccessfully = true;
                                    } else {

                                        logger.debug(
                                                (
                                                        "Can not connect to remote resource: " +
                                                                "id: %d, address: '%s:%d', status: '%s'"
                                                )
                                                        .formatted(
                                                                id,
                                                                address.getHost(),
                                                                address.getPort(),
                                                                channelData.getStatus()
                                                        )
                                        );

                                        connectedSuccessfully = false;
                                    }

                                } catch (Throwable t) {

                                    logger.debug(
                                            (
                                                    "Can not connect to remote resource: " +
                                                            "id: %d, address: '%s:%d', status: '%s', error: '%s'"
                                            )
                                                    .formatted(
                                                            id,
                                                            address.getHost(),
                                                            address.getPort(),
                                                            channelData.getStatus(),
                                                            t.getMessage()
                                                    ),
                                            t
                                    );

                                    connectedSuccessfully = false;
                                }

                                if (connectedSuccessfully) {

                                    connectSuccessCount++;

                                    try {

                                        sendDataCount++;
                                        channelData.setWriteStatus(
                                                channelData.getSocketChannel().write(channelData.getByteBuffer())
                                        );
                                        channelData.setStatus(SocketChannelStatus.DATA_SENDING);
                                        channelData.setDataWriteStart(java.time.Instant.now());

                                    } catch (Throwable t) {

                                        sendDataErrorCount++;
                                        channelData.setStatus(SocketChannelStatus.ERROR);
                                        socketChannelDataToRemoveList.add(channelData);

                                        logger.debug(
                                                (
                                                        "Can not write date to socket channel: " +
                                                                "id: %d, address: '%s:%d', status: '%s', error: '%s'"
                                                )
                                                        .formatted(
                                                                id,
                                                                address.getHost(),
                                                                address.getPort(),
                                                                channelData.getStatus(),
                                                                t.getMessage()
                                                        )
                                        );

                                    }

                                } else {

                                    try {
                                        channelData.getSocketChannel().close();
                                    } catch (Throwable ignored) {
                                    }

                                    channelData.setStatus(SocketChannelStatus.ERROR);
                                    socketChannelDataToRemoveList.add(channelData);
                                    sendDataTimeoutCount++;

                                    logger.debug(
                                            (
                                                    "Connection error: id: %d, address: '%s:%d', status: '%s'"
                                            )
                                                    .formatted(
                                                            id,
                                                            address.getHost(),
                                                            address.getPort(),
                                                            channelData.getStatus()
                                                    )
                                    );

                                }

                            } else {
                                logger.warn("Unknown connection state, status: '%s'".formatted(channelData.getStatus()));
                            }

                        } else if (SocketChannelStatus.DATA_SENDING.equals(channelData.getStatus())) {

                            Future<Integer> writeFuture = channelData.getWriteStatus();

                            if (writeFuture.isDone()) {

                                if (bytes.length == writeFuture.get()) {
                                    sendDataSuccessCount++;
                                } else {
                                    sendDataErrorCount++;
                                }

                                try {
                                    channelData.getSocketChannel().close();
                                } catch (Throwable ignored) {
                                }

                                channelData.setStatus(SocketChannelStatus.CLOSED);
                                socketChannelDataToRemoveList.add(channelData);

                            } else {

                                Instant end = java.time.Instant.now();
                                Duration between = Duration.between(channelData.getDataWriteStart(), end);

                                Integer writeTimeoutMillis = settings.getSocketChannelSettings().getWriteTimeoutMillis();

                                if (between.toMillis() > writeTimeoutMillis) {

                                    channelData.getWriteStatus().cancel(true);

                                    try {
                                        channelData.getSocketChannel().close();
                                    } catch (Throwable ignored) {
                                    }

                                    channelData.setStatus(SocketChannelStatus.TIMEOUT);
                                    socketChannelDataToRemoveList.add(channelData);
                                    sendDataTimeoutCount++;

                                    logger.debug(
                                            (
                                                    "Write timeout: id: %d, address: " +
                                                            "'%s:%d', status: '%s', timeout mills: %d"
                                            )
                                                    .formatted(
                                                            id,
                                                            address.getHost(),
                                                            address.getPort(),
                                                            channelData.getStatus(),
                                                            writeTimeoutMillis
                                                    )
                                    );

                                }

                            }

                        } else {
                            logger.warn("Unhandled status: id: %d, address: '%s:%d', '%s'"
                                    .formatted(
                                            id,
                                            address.getHost(),
                                            address.getPort(),
                                            channelData.getStatus()
                                    )
                            );
                        }

                    } catch (Throwable t) {

                        channelData.setStatus(SocketChannelStatus.ERROR);

                        try {
                            channelData.getSocketChannel().close();
                        } catch (Throwable ignored) {
                        }

                        socketChannelDataToRemoveList.add(channelData);

                        logger.error(
                                "Unhandled status: id: %d, address: '%s:%d', status: '%s', error: '%s'"
                                        .formatted(
                                                id,
                                                address.getHost(),
                                                address.getPort(),
                                                channelData.getStatus(),
                                                t.getMessage()
                                        ),
                                t
                        );

                    }
                }

                for (SocketChannelData socketChannelData : socketChannelDataToRemoveList) {
                    try {
                        socketChannelData.getSocketChannel().close();
                    } catch (Throwable ignored) {
                    }
                }

                for (int i = 0; i < socketChannelDataToRemoveList.size(); i++) {
                    recreateSocketChannel(id, address, bytes, localAddress, socketChannelDataToAddList);
                }

                socketChannelDataList.removeAll(socketChannelDataToRemoveList);
                socketChannelDataList.addAll(socketChannelDataToAddList);

                totalConnectCount += connectCount;
                totalConnectSuccessCount += connectSuccessCount;
                totalConnectTimeoutCount += connectTimeoutCount;
                totalConnectErrorCount += connectErrorCount;
                totalSendDataCount += sendDataCount;
                totalSendDataSuccessCount += sendDataSuccessCount;
                totalSendDataTimeoutCount += sendDataTimeoutCount;
                totalSendDataErrorCount += sendDataErrorCount;

                Duration between = Duration.between(loopStartTime, java.time.Instant.now());

                if (between.toMillis() > settings.getLogEachMills()) {

                    logger.info(
                            (
                                    "Id: %d, " +
                                            "Address: '%s:%d', " +

                                            "connectCount: %d, " +
                                            "connectSuccessCount: %d, " +
                                            "connectTimeoutCount: %d, " +
                                            "connectErrorCount: %d, " +
                                            "sendDataCount: %d, " +
                                            "sendDataSuccessCount: %d, " +
                                            "sendDataTimeoutCount: %d, " +
                                            "sendDataErrorCount: %d, " +

                                            "totalConnectCount: %d, " +
                                            "totalConnectSuccessCount: %d, " +
                                            "totalConnectTimeoutCount: %d, " +
                                            "totalConnectErrorCount: %d, " +
                                            "totalSendDataCount: %d, " +
                                            "totalSendDataSuccessCount: %d, " +
                                            "totalSendDataTimeoutCount: %d, " +
                                            "totalSendDataErrorCount: %d"
                            )
                                    .formatted(
                                            id,
                                            address.getHost(),
                                            address.getPort(),

                                            connectCount,
                                            connectSuccessCount,
                                            connectTimeoutCount,
                                            connectErrorCount,
                                            sendDataCount,
                                            sendDataSuccessCount,
                                            sendDataTimeoutCount,
                                            sendDataErrorCount,

                                            totalConnectCount,
                                            totalConnectSuccessCount,
                                            totalConnectTimeoutCount,
                                            totalConnectErrorCount,
                                            totalSendDataCount,
                                            totalSendDataSuccessCount,
                                            totalSendDataTimeoutCount,
                                            totalSendDataErrorCount
                                    )
                    );

                    try {
                        Thread.sleep(settings.getSocketChannelSettings().getFinalStateMachineDelayMillis());
                    } catch (InterruptedException ignored) {
                    }

                    loopStartTime = java.time.Instant.now();

                }

            }

        } finally {

            for (SocketChannelData socketChannelData : socketChannelDataList) {
                try {
                    socketChannelData.getSocketChannel().close();
                } catch (Throwable ignored) {
                }
            }

            logger.info(
                    "Socket channels closed: id: %d, address: '%s:%d', socket channels count: '%d'"
                            .formatted(
                                    id,
                                    address.getHost(),
                                    address.getPort(),
                                    socketChannelDataList.size()
                            )
            );

        }

        return new SocketChannelRequestExecutionResult(
                address,
                SocketChannelStatisticsBuilder
                        .aSocketChannelStatistics()
                        .withConnectCount(totalConnectCount)
                        .withConnectSuccessCount(totalConnectSuccessCount)
                        .withConnectTimeoutCount(totalConnectTimeoutCount)
                        .withConnectErrorCount(totalConnectErrorCount)
                        .withSendDataCount(totalSendDataCount)
                        .withSendDataSuccessCount(totalSendDataSuccessCount)
                        .withSendDataTimeoutCount(totalSendDataTimeoutCount)
                        .withSendDataErrorCount(totalSendDataErrorCount)
                        .build()
        );
    }

    private void recreateSocketChannel(long id, TcpAddress address, byte[] bytes, InetSocketAddress localAddress, List<SocketChannelData> socketChannelDataToAddList) {
        SocketChannelData newSocketChannelData = null;
        try {
            newSocketChannelData = createSocketChannelData(bytes);

            if (settings.getSocketChannelSettings().getUseLocalAddress()) {
                newSocketChannelData.getSocketChannel().bind(localAddress);
            }

            socketChannelDataToAddList.add(newSocketChannelData);

        } catch (Throwable t) {

            logger.error("Can not recreate socket channel: id: %d, address: '%s:%d', error: '%s'"
                            .formatted(
                                    id,
                                    address.getHost(),
                                    address.getPort(),
                                    t.getMessage()
                            ),
                    t
            );

            try {
                if (Objects.nonNull(newSocketChannelData)) {
                    newSocketChannelData.getSocketChannel().close();
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private SocketChannelData createSocketChannelData(byte[] bytes) throws IOException {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        return new SocketChannelData(channel, byteBuffer, SocketChannelStatus.OPENED);
    }

    private byte[] getRandomBytes() {
        final byte[] bytes = new byte[
                getRandomNumberUsingInts(
                        settings.getRandomBytesArraySettings().getMinSize(),
                        settings.getRandomBytesArraySettings().getMaxSize()
                )
                ];
        ThreadLocalRandom.current().nextBytes(bytes);
        return bytes;
    }

    private int getRandomNumberUsingInts(int min, int max) {
        Random random = new Random();
        return random.ints(min, max).findFirst().orElse(settings.getRandomBytesArraySettings().getDefaultSize());
    }

}
