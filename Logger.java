package ZeroCarbonFootprintTracker;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for appending timestamped operation logs to greenprint_log.txt.
 *
 * Use the provided constants for operationType to ensure consistent log formatting.
 * All file I/O is wrapped in try-catch blocks so a log failure never crashes the app.
 */
public class Logger {

    public enum Operation { ENTRY_ADDED, OFFSET_PURCHASED, STATE_SAVED, STATE_LOADED }

    // relative path (current working directory) – example uses "./log.txt" style
    private static final String LOG_FILE = "./ZeroCarbonFootprintTracker/greenprint_log.txt";  // writes to whichever directory the JVM is started in

    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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