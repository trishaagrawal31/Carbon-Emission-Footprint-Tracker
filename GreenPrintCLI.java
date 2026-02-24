public class GreenPrintCLI {
/**
        Entry point for the GreenPrint Carbon Footprint Tracking System.

        This class demonstrates how the FootprintTracker system works by:

        Creating a tracker instance
        Adding different emission entries (Transportation, Food, Energy)
        Using polymorphism to manage various emission types
        Printing a formatted daily emissions summary

        The main method shows multiple users adding different emission
        records for a single day.

        @author Adira
**/

    public static void main(String[] args) {

        FootprintTracker ft = new FootprintTracker("RIT GreenPrint 2026");

        // Explicitly pass category along with other fields
        ft.addEntry(new TransportationEmission(
                "T-001",
                "Transportation",
                "2026-02-12",
                "Alice",
                15,
                "Car"));

        ft.addEntry(new FoodEmission(
                "F-001",
                "Food",
                "2026-02-12",
                "Alice",
                "Vegan",
                2));

        ft.addEntry(new EnergyEmission(
                "E-001",
                "Energy",
                "2026-02-12",
                "alice",
                8.5,
                "Grid"));

        ft.addEntry(new TransportationEmission(
                "T-002",
                "Transportation",
                "2026-02-12",
                "Bob",
                22,
                "Bus"));

        ft.addEntry(new FoodEmission(
                "F-002",
                "Food",
                "2026-02-12",
                "Charlie",
                "Beef",1));

        ft.addEntry(new EnergyEmission(
                "E-002",
                "Energy",
                "2026-02-12",
                "Charlie",
                5,
                "Solar"));

        // Generates the final report
        ft.generateDailyReport();
    }
}
