package ZeroCarbonFootprintTracker.src.network;

 
/**
 * Calculates discounted carbon emissions.
 * Kept as a separate class so it can be unit tested with JUnit.
 */
public class DiscountCalculator {
 
    /**
     * Applies a discount percentage to a total emission value.
     *
     * @param totalEmission total kg CO2 (must be >= 0)
     * @param discountPct   discount percentage (0 to 30)
     * @return discounted emission rounded to 2 decimal places
     */
    public static double applyDiscount(double totalEmission, int discountPct) {
        if (totalEmission < 0) totalEmission = 0;
        if (discountPct < 0) discountPct = 0;
        if (discountPct > 30) discountPct = 30;
        
        return totalEmission * (1.0 - discountPct / 100.0);
        
    }
}
 