package com.blind.paylock.component;

import lombok.extern.slf4j.Slf4j;

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

    public static String processDuration(long startTime) {
        return (System.currentTimeMillis() - startTime) + "ms";
    }
}