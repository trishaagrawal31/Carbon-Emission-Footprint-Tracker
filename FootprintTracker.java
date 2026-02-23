import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages a collection of emission entries for all users in the GreenPrint carbon footprint tracking system.
 * This class serves as the central repository for all emission entries and provides functionality
 * to add entries, calculate totals, and generate formatted reports.
 * 
 * The tracker demonstrates polymorphism by storing any subclass of EmissionSource
 * (TransportationEmission, EnergyEmission, FoodEmission) in its internal ArrayList.
 * 
 * @author Zainab
 */

public class FootprintTracker {
    /** The name of this tracker instance (e.g., "RIT GreenPrint 2026") */
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
     * @return ArrayList containing all EmissionSource objects (polymorphic collection)
     */

    public ArrayList<EmissionSource> getEntries() {
        return entries;
    }

    /**
     * Adds a new emission entry to the tracker's collection.
     * Performs a null check before adding to ensure data integrity.
     * This method demonstrates polymorphism by accepting any subclass of EmissionSource.
     *
     * @param entry The emission entry to add (can be TransportationEmission, 
     *              EnergyEmission, or FoodEmission)
     */

    public void addEntry(EmissionSource entry) {
        if (entry != null) {
            entries.add(entry);
        }
    }

    /**
     * Calculates the total carbon emissions across all entries in the tracker.
     * Iterates through the entire collection and sums the results of each entry's
     * polymorphic calculateEmission() method.
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
     * @return Total emissions in kilograms of CO₂ for the specified user
     */

    public double getTotalEmissionsForUser(String userName) {
        double total = 0.0;
        for (EmissionSource entry : entries) {
            if (entry.getUserName().equals(userName)) {
                total += entry.calculateEmission();
            }
        }
        return total;
    }

    /**
     * Generates and prints a formatted daily report to the console.
     * The report includes:
     * <ul>
     *   <li>Entries grouped by user</li>
     *   <li>Each entry's complete details (using each entry's toString() method)</li>
     *   <li>Per-user subtotals formatted to 2 decimal places</li>
     *   <li>A grand total across all users formatted to 2 decimal places</li>
     * </ul>
     * 
     * The method uses a HashMap to efficiently group entries by username
     * before printing the formatted report.
     */

    public void generateDailyReport() {
        System.out.println("\n=== " + trackerName + " — Daily Report ===\n");
        // Map to group entries by username for organized display
        Map<String, ArrayList<EmissionSource>> userEntries = new HashMap<>();
        // Group all entries by their associated username
        for (EmissionSource entry : entries) {
            String userName = entry.getUserName();
            if (!userEntries.containsKey(userName)) {
                userEntries.put(userName, new ArrayList<>());
            }
            userEntries.get(userName).add(entry);
        }
        
        double grandTotal = 0.0;
        // Process and display each user's entries
        for (Map.Entry<String, ArrayList<EmissionSource>> userGroup : userEntries.entrySet()) {
            String userName = userGroup.getKey();
            ArrayList<EmissionSource> userEntryList = userGroup.getValue();
            
            System.out.println("**User:** " + userName);
            
            double userSubtotal = 0.0;
            // Display each entry for the current user
            for (EmissionSource entry : userEntryList) {
                System.out.println(entry.toString());
                userSubtotal += entry.calculateEmission();
            }
            // Display user subtotal with 2 decimal places
            System.out.printf("**Subtotal:** %.2f kg CO₂\n\n", userSubtotal);
            grandTotal += userSubtotal;
        }
        // Display grand total with 2 decimal places
        System.out.printf("**Grand Total:** %.2f kg CO₂\n", grandTotal);
    }
}