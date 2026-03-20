package ZeroCarbonFootprintTracker;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for appending timestamped operation logs to greenprint_log.txt.
 * Each log entry includes the timestamp, operation type, and relevant details.
 */

public class Logger {

    public enum Operation { ENTRY_ADDED, OFFSET_PURCHASED, STATE_SAVED, STATE_LOADED }

    // relative path
    public static final String LOG_FILE = "./ZeroCarbonFootprintTracker/greenprint_log.txt";  // writes to whichever directory the JVM is started in

    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Writes a log message to the tracker log file.
     *
     * @param operationType type of operation being logged
     * @param details      extra details to include in the log record
     */
    public static void log(Operation operationType, String details) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logLine   = timestamp + " | " + operationType.name() + " | " + details;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logLine);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Logger: Failed to write log entry to " + LOG_FILE + ": " + e.getMessage());
        }
    }
}