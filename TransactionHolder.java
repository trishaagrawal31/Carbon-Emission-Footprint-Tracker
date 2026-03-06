public class TransactionHolder {
    private static double OFF_SET_VALUE= 15/1000;
    private FootprintTracker offsettracker;
    public TransactionHolder(FootprintTracker offsettracker){
        this.offsettracker=offsettracker;
    }



        public String CalculateOffSet(String userName) {

        // 👇 THIS is where we use your method
        double userEmissions = offsettracker.getTotalEmissionsForUser(userName);

        if (userEmissions <= 0) {
            return "No emissions found for this user";
        }

        double totalCost = userEmissions * OFF_SET_VALUE ;

        return OffsetReceipt(userName, userEmissions, totalCost);
    }
        private String OffsetReceipt(String userName, double emissions, double cost) {

        return "====== Carbon Offset Receipt ======\n"
                + "User: " + userName + "\n"
                + "Total Emissions: " + emissions + " kg CO2\n"
                + "Cost per kg: $" + OFF_SET_VALUE + "\n"
                + "----------------------------------\n"
                + "Total Offset Cost: $" + String.format("%.2f", cost) + "\n"
                
                + "==================================";
    }
}
        
    
    





