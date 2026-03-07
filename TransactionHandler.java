package ZeroCarbonFootprintTracker;
import java.time.LocalDateTime;

/**
 * Handles carbon offset purchase transactions for the GreenPrint system.
 *
 * The correct constant is {@code 15.0 / 1000.0} = $0.015 per kg CO2.</p>
 *
 * Rate: $15 per 1 000 kg CO2 = $0.015 per kg CO2.
 */
public class TransactionHandler {

    /** Cost rate: $15 per 1 000 kg CO2 = $0.015 per kg CO2 */
    private static final double OFFSET_RATE = 15.0 / 1000.0;

    private final FootprintTracker offsetTracker;

    /**
     * Constructs a TransactionHandler linked to the given FootprintTracker.
     *
     * @param offsetTracker the tracker whose emissions are used for offset calculation
     */
    public TransactionHandler(FootprintTracker offsetTracker) {
        this.offsetTracker = offsetTracker;
    }

    /**
     * Calculates the offset cost for the given user, builds a receipt,
     * stores it in offset history, and returns it.
     *
     * @param userName      the user whose emissions should be offset
     * @param paymentMethod the payment method chosen by the user
     * @return formatted receipt string, or an error message if no emissions found
     */
    public String CalculateOffSet(String userName, String paymentMethod) {
        double userEmissions = offsetTracker.getTotalEmissionsForUser(userName);

        if (userEmissions <= 0) {
            return "No emissions found for user \"" + userName + "\".\n"
                    + "Please add emission entries first.";
        }

        double totalCost = userEmissions * OFFSET_RATE;
        String receipt   = buildReceipt(userName, userEmissions, totalCost, paymentMethod);
        return receipt;
    }




    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private String buildReceipt(String userName, double emissions,
                                 double cost, String paymentMethod) {
        String timestamp = LocalDateTime.now().format(Logger.FORMATTER);
        return "====== Carbon Offset Receipt ======\n"
                + "Date/Time      : " + timestamp                          + "\n"
                + "User           : " + userName                           + "\n"
                + "Emissions      : " + String.format("%.2f", emissions)   + " kg CO2\n"
                + "Rate           : $" + OFFSET_RATE                       + " per kg CO2\n"
                + "Payment Method : " + paymentMethod                      + "\n"
                + "----------------------------------\n"
                + "Total Cost     : $" + String.format("%.2f", cost)       + "\n"
                + "Status         : CONFIRMED \u2713\n"
                + "==================================";
    }
}