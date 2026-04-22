package ZeroCarbonFootprintTracker;

 
/**
 * Calculates discounted carbon emissions.
 * Kept as a separate class so it can be unit tested with JUnit.
 */
public class DiscountCalculator {
 
    /**
     * Applies a discount percentage to a total emission value.
     *
     * @param totalEmission total kg CO2 (must be >= 0)
     * @param discountPct   discount percentage (0 to 100)
     * @return discounted emission rounded to 2 decimal places
     * @throws IllegalArgumentException if inputs are out of range
     */
    public static double applyDiscount(double totalEmission, int discountPct) {
        if (totalEmission < 0) {
            throw new IllegalArgumentException("Emission cannot be negative.");
        }
        if (discountPct < 0 || discountPct > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100.");
        }
        double result = totalEmission * (1.0 - discountPct / 100.0);
        return Math.round(result * 100.0) / 100.0;
    }
}
 