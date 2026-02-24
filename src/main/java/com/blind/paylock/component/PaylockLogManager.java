package com.blind.paylock.component;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public final class PaylockLogManager {

    private PaylockLogManager() {}

    private static final String FORMAT =
            "TransactionID={} | Process={} | ProcessDuration={} | Message={}";

    public static void info(String txId,
                            String process,
                            String duration,
                            String message) {

        log.info(FORMAT, txId, process, duration, message);
    }

    public static void error(String txId,
                             String process,
                             String duration,
                             String message) {

        log.error(FORMAT, txId, process, duration, message);
    }

    public static String processDuration(LocalDateTime startTime) {
        Duration duration = Duration.between(startTime, LocalDateTime.now());
        long millis = duration.toMillis();
        if (millis < 1000) {
            return millis + "ms";
        }
        long seconds = millis / 1000;
        long remainingMillis = millis % 1000;
        return seconds + "s " + remainingMillis + "ms";
    }
}