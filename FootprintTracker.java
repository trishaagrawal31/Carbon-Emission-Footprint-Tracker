import java.util.ArrayList;
import java.util.HashSet;

/**
 * Manages a collection of emission entries for all users
 * in the GreenPrint carbon footprint tracking system.
 * Usernames are treated case-insensitively (Alice = alice).
 */
public class FootprintTracker {

    private String trackerName;
    private ArrayList<EmissionSource> entries;

    public FootprintTracker(String trackerName) {
        this.trackerName = trackerName;
        this.entries = new ArrayList<>();
    }

    public String getTrackerName() {
        return trackerName;
    }

    public void setTrackerName(String trackerName) {
        this.trackerName = trackerName;
    }

    public ArrayList<EmissionSource> getEntries() {
        return entries;
    }

    public void addEntry(EmissionSource entry) {
        if (entry != null) {
            entries.add(entry);
        }
    }

    /**
     * Calculates total emissions across all entries.
     */
    public double getTotalEmissions() {
        double total = 0.0;
        for (EmissionSource entry : entries) {
            total += entry.calculateEmission();
        }
        return total;
    }

    /**
     * Calculates total emissions for a specific user (case-insensitive).
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
     * Generates formatted daily report.
     * Reuses total calculation methods.
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

            // Reuse method
            double userSubtotal = getTotalEmissionsForUser(userName);
            System.out.printf("Subtotal: %.2f kg CO2\n\n", userSubtotal);
        }

        // Reuse grand total method
        System.out.printf("Grand Total: %.2f kg CO2\n", getTotalEmissions());
    }
}