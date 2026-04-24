package ZeroCarbonFootprintTracker;

/**
 * Parses the response string sent by the GreenPrint Discount Server.
 *
 * Expected format: "DISCOUNT:<pct>:<discountedValue>"
 * Example:         "DISCOUNT:15:10.84"
 */
public class ResponseParser {
 
    private int discountPct;
    private double discountedValue;
 
    /**
     * Constructs a ResponseParser and parses the given server response.
     *
     * @param response the raw response line read from the server via BufferedReader
     * @throws IllegalArgumentException if the response is null, blank, badly formatted,
     *                                  or contains non-numeric values
     */
    public ResponseParser(String response) {
        if (response == null || response.isBlank()) {
            throw new IllegalArgumentException("Response is null or empty.");
        }
 
        // Split the response by ":" 
        String[] parts = response.trim().split(":");
 
        if (parts.length != 3 || !parts[0].equals("DISCOUNT")) {
            throw new IllegalArgumentException("Unexpected response format: " + response);
        }
 
        try {
            this.discountPct     = Integer.parseInt(parts[1]);
            this.discountedValue = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Could not parse numbers in response: " + response);
        }
    }
 
    /**
     * Gets the discount percentage from the server response.
     * @return discount percentage (e.g. 15)
     */
    public int getDiscountPct() {
        return discountPct;
    }
 
    /**
     * Gets the discounted emission value from the server response.
     * @return discounted emission in kg CO2 (e.g. 10.84)
     */
    public double getDiscountedValue() {
        return discountedValue;
    }
}

