package ZeroCarbonFootprintTracker;
import java.time.LocalDateTime;

/**
 * Handles carbon offset purchase transactions for the GreenPrint system.
 * Rate: $15 per 1 000 kg CO2 = $0.015 per kg CO2.
 */
public class TransactionHandler {

    /** Cost rate: $15 per 1 000 kg CO2 = $0.015 per kg CO2 */
    private static final double OFFSET_RATE = 0.015;

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




    /**
     * Builds a reciept string for a carbon offset purchase transaction, including user name, emissions, cost, payment method, and timestamp.
     *
     * @param userName      the user whose emissions should be offset
     * @param emissions     the total emissions for the user
     * @param cost          the total cost of the offset
     * @param paymentMethod the payment method chosen by the user
     * @return formatted receipt string
     */
    private String buildReceipt(String userName, double emissions,
                                 double cost, String paymentMethod) {
        String timestamp = LocalDateTime.now().format(Logger.FORMATTER);
        return "====== Carbon Offset Receipt ======\n"
                + "Date/Time      : " + timestamp + "\n"
                + "User           : " + userName + "\n"
                + "Emissions      : " + String.format("%.2f", emissions) + " kg CO2\n"
                + "Rate           : $" + OFFSET_RATE   + " per kg CO2\n"
                + "Payment Method : " + paymentMethod  + "\n"
                + "----------------------------------\n"
                + "Total Cost     : $" + String.format("%.2f", cost) + "\n"
                + "Status         : CONFIRMED \n"
                + "==================================";
    }
}