@Slf4j
public final class PaylockLogManager {

    private PaylockLogManager() {}

    private static final String BASE_FORMAT =
        "TransactionID={} | Process={} | ProcessDuration={} | Message={}";

    public static void info(String txId,
                            String process,
                            String duration,
                            String message) {

        log.info(BASE_FORMAT, txId, process, duration, message);
    }

    public static void error(String txId,
                             String process,
                             String duration,
                             String message) {

        log.error(BASE_FORMAT, txId, process, duration, message);
    }

    public static String processDuration(long startTime) {
        return (System.currentTimeMillis() - startTime) + "ms";
    }
}