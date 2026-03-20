package ZeroCarbonFootprintTracker;

import java.io.*;
import java.util.ArrayList;

public class PersistenceManager {
    private static final String FILE_PATH = "greenprint_state.txt";

    public static void saveState(ArrayList<EmissionSource> entries) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (EmissionSource e : entries) {
                // Simplified CSV format: ClassType,ID,Category,Date,User,Val1,Val2
                String type = e.getClass().getSimpleName();
                if (e instanceof TransportationEmission t) {
                    writer.printf("%s,%s,%s,%s,%s,%.2f,%s\n", type, t.getSourceID(), t.getCategory(), t.getDate(), t.getUserName(), t.getDistance(), t.getTransport());
                } else if (e instanceof EnergyEmission en) {
                    writer.printf("%s,%s,%s,%s,%s,%.2f,%s\n", type, en.getSourceID(), en.getCategory(), en.getDate(), en.getUserName(), en.getkWhUsed(), en.getEnergySource());
                } else if (e instanceof FoodEmission f) {
                    writer.printf("%s,%s,%s,%s,%s,%d,%s\n", type, f.getSourceID(), f.getCategory(), f.getDate(), f.getUserName(), f.getNumberOfMeals(), f.getMealType());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving state: " + e.getMessage());
        }
    }

    public static void loadState(FootprintTracker tracker) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String type = parts[0];
                switch (type) {
                    case "TransportationEmission" -> tracker.addEntry(new TransportationEmission(parts[1], parts[2], parts[3], parts[4], Double.parseDouble(parts[5]), parts[6]));
                    case "EnergyEmission" -> tracker.addEntry(new EnergyEmission(parts[1], parts[2], parts[3], parts[4], Double.parseDouble(parts[5]), parts[6]));
                    case "FoodEmission" -> tracker.addEntry(new FoodEmission(parts[1], parts[2], parts[3], parts[4], parts[6], Integer.parseInt(parts[5])));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading state: " + e.getMessage());
        }
    }
}
