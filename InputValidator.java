import java.util.regex.Pattern;

public class InputValidator {
    
    // Regex pattern: one uppercase letter, hyphen, exactly three digits
    private static final Pattern SOURCE_ID_PATTERN = Pattern.compile("[A-Z]-\\d{3}");
    
    public static boolean validateSourceId(String sourceId) {
        return sourceId != null && SOURCE_ID_PATTERN.matcher(sourceId).matches();
    }
    
    public static String getValidationMessage(String sourceId) {
        if (sourceId == null || sourceId.isEmpty()) {
            return "Source ID cannot be empty";
        }
        if (!validateSourceId(sourceId)) {
            return "Invalid format. Use: [A-Z]-### (e.g., T-001)";
        }
        return "Valid";
    }
    
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isPositiveNumeric(String str) {
        if (!isNumeric(str)) return false;
        return Double.parseDouble(str) > 0;
    }
    
    public static boolean isPositiveInteger(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            int value = Integer.parseInt(str);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
