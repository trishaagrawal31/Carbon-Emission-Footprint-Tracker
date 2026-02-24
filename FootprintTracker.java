import java.util.ArrayList;
import java.util.HashSet;

/**
 * Manages a collection of emission entries for all users
 * in the GreenPrint carbon footprint tracking system.
 * 
 * 
 * @author Zainab
 */
public class FootprintTracker {


    private String trackerName;
    
    /** Collection of all emission entries, demonstrating polymorphic storage */
    private ArrayList<EmissionSource> entries;

    /**
     * Constructs a new FootprintTracker with the specified name.
     * Initializes an empty ArrayList to store emission entries.
     *
     * @param trackerName The name to identify this tracker instance
     */
    public FootprintTracker(String trackerName) {
        this.trackerName = trackerName;
        this.entries = new ArrayList<>();
    }

    /**
     * Gets the name of this tracker.
     *
     * @return The current tracker name
     */
    public String getTrackerName() {
        return trackerName;
    }

    /**
     * Sets or updates the name of this tracker.
     *
     * @param trackerName The new name for this tracker
     */
    public void setTrackerName(String trackerName) {
        this.trackerName = trackerName;
    }

    /**
     * Gets the complete list of all emission entries stored in this tracker.
     *
     * @return ArrayList containing all EmissionSource objects
     */
    public ArrayList<EmissionSource> getEntries() {
        return entries;
    }

    /**
     * Adds a new emission entry to the tracker's collection.
     * Performs a null check before adding to ensure data integrity.
     *
     * @param entry The emission entry to add
     */
    public void addEntry(EmissionSource entry) {
        if (entry != null) {
            entries.add(entry);
        }
    }

    /**
     * Calculates the total carbon emissions across all entries in the tracker.
     *
     * @return Total emissions in kilograms of CO₂ for all entries
     */
    public double getTotalEmissions() {
        double total = 0.0;
        for (EmissionSource entry : entries) {
            total += entry.calculateEmission();
        }
        return total;
    }

    /**
     * Calculates the total carbon emissions for a specific user only.
     * Filters entries by username before summing their emissions.
     *
     * @param userName The username to filter entries by
     * @return Total emissions in kilograms of CO2 for the specified user
     */
    public double getTotalEmissionsForUser(String userName) {
        double total = 0.0;

        for (EmissionSource entry : entries) {
            if (entry.getUserName().equalsIgnoreCase(userName)) {
                total += entry.calculateEmission();
            }
        }

        return total;
    }

    /**
     * Generates and prints a formatted daily report to the console.
     * The report includes:
     * - Entries grouped by user (case-insensitive)
     * - Each entry's complete details
     * - Per-user subtotals formatted to 2 decimal places
     * - A grand total across all users formatted to 2 decimal places
     * 
     */
    public void generateDailyReport() {

        System.out.println("\n=== " + trackerName + " — Daily Report ===\n");

        HashSet<String> uniqueUsers = new HashSet<>();

        // Store lowercase versions to group case-insensitively
        for (EmissionSource entry : entries) {
            uniqueUsers.add(entry.getUserName().toLowerCase());
        }

        for (String userName : uniqueUsers) {

            System.out.println("User: " + userName);

            // Print entries for this user
            for (EmissionSource entry : entries) {
                if (entry.getUserName().equalsIgnoreCase(userName)) {
                    System.out.println(entry.toString());
                }
            }

            
            double userSubtotal = getTotalEmissionsForUser(userName);
            System.out.printf("Subtotal: %.2f kg CO2\n\n", userSubtotal);
        }

        System.out.printf("Grand Total: %.2f kg CO2\n", getTotalEmissions());
    }
}