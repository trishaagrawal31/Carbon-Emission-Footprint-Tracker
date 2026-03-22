package ZeroCarbonFootprintTracker.src.util;
import java.util.regex.Pattern;

import ZeroCarbonFootprintTracker.src.model.EmissionSource;
import ZeroCarbonFootprintTracker.src.model.FootprintTracker;

/**
 * Helper class for validating user input values in the tracker UI.
 */
public class InputValidator {

    // Pattern
    private static final Pattern SOURCE_ID_PATTERN = Pattern.compile("[A-Z]-\\d{3}");// pattern verificaiton fromt the user

    /**
     * Validates that the source ID follows the pattern A-Z-### (for example: T-001).
     *
     * @param sourceId ID string to validate
     * @return true if the ID is non-null and correctly formatted
     */
    public static boolean validateSourceId(String sourceId) {
        return sourceId != null && SOURCE_ID_PATTERN.matcher(sourceId).matches();
    }

    /**
     * Validates that the source ID is valid and not already present in the tracker.
     *
     * @param sourceId ID string to validate
     * @param tracker  tracker which may contain existing entries
     * @return true if the ID is valid and unique
     */
    public static boolean validateUniqueSourceId(String sourceId, FootprintTracker tracker) {
        if (!validateSourceId(sourceId)) return false;
        if (tracker != null) {
            for (EmissionSource e : tracker.getEntries()) {
                if (e.getSourceID().equalsIgnoreCase(sourceId)) {
                    return false;
                }
            }
        }
        return true;
    }

}