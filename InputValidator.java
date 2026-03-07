package ZeroCarbonFootprintTracker;
import java.util.regex.Pattern;

public class InputValidator {

    // Pattern: one uppercase letter, hyphen, exactly three digits e.g. T-001
    private static final Pattern SOURCE_ID_PATTERN = Pattern.compile("[A-Z]-\\d{3}");

    public static boolean validateSourceId(String sourceId) {
        return sourceId != null && SOURCE_ID_PATTERN.matcher(sourceId).matches();
    }

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