import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for appending timestamped operation logs to greenprint_log.txt.
 * Use the provided constants for operationType to ensure consistent log formatting.
 */

public class Logger {

    public static final String ENTRY_ADDED      = "ENTRY_ADDED";
    public static final String OFFSET_PURCHASED = "OFFSET_PURCHASED";
    public static final String STATE_SAVED      = "STATE_SAVED";

    private static final String LOG_FILE        = "greenprint_log.txt";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Appends a single log entry to the log file.
     *
     * @param operationType the type of operation (use class constants)
     * @param details       relevant context, e.g. source ID, user name, amount
     */

    public static void log(String operationType, String details) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logLine   = timestamp + " | " + operationType + " | " + details;

        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logLine);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}
