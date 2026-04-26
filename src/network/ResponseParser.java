package ZeroCarbonFootprintTracker.src.network;

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
     */
    public ResponseParser(String response) {
        this.discountPct = 0;
        this.discountedValue = 0.0;

        if (response == null || response.isBlank()) {
            System.out.println("ResponseParser: Response is null or empty.");
            return;
        }

        String[] parts = response.trim().split(":");
        if (parts.length!= 3 ||!parts[0].equals("DISCOUNT")) {
            System.out.println("ResponseParser: Unexpected format: " + response);
            return;
        }

        try {
            this.discountPct = Integer.parseInt(parts[1]);
            this.discountedValue = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            System.out.println("ResponseParser: Could not parse numbers: " + response);
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

