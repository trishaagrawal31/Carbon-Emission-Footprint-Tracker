import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * Interactive CLI for the GreenPrint carbon footprint tracking system.
 * 
 * This class provides a menu-driven interface that allows users to log their carbon emissions
 * across three categories: Transportation, Food, and Energy. 
 * @author Aaliya
 */
public class GreenPrintCLI1 {
        /**
         * The method asks user for sourceID, Date and username, and gives the list of emissions for the user to choose from,
         * the method then uses FootprintTracker.addEntry() and delivers an output.
         * @param args
         */
        public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        FootprintTracker ft=new FootprintTracker("RIT GreenPrint 2026");
        
        System.out.println("Welcome!");

        while (true) {
                try {
                System.out.println("\n--- Main Menu ---");
                System.out.println("1. Add Transportation Emission");
                System.out.println("2. Add Food Emission");
                System.out.println("3. Add Energy Emission");
                System.out.println("4. Generate Report & Finish");
                System.out.print("Choose an option: ");
                
                int choice=scanner.nextInt();
                scanner.nextLine();

                
                if (choice==4){
                        break;
                }
                if(choice<1 || choice >3) {  //|| or operator
                        System.out.println("Invalid choice! Please select 1-4.");
                        continue;
                }

                
                System.out.print("Enter Source ID:");
                String id = scanner.nextLine();
                if(id.isEmpty()) { System.out.println("ID cannot be empty!"); continue; }
                System.out.print("Enter Date:");
                String date = scanner.nextLine();
                if(date.isEmpty()) { System.out.println("Date cannot be empty!"); continue; }
                System.out.print("Enter User Name:");
                String user = scanner.nextLine();
                if(user.isEmpty()) { System.out.println("User name cannot be empty!"); continue; }

                // ---TRANSPORTATION ---
                if (choice==1){
                        System.out.print("Transport Type (Car, Bus, Train, Cycle): ");
                        String transport = scanner.nextLine().toLowerCase();

                        if (transport.equals("car") || transport.equals("bus") || transport.equals("train") || transport.equals("cycle")){
                        System.out.print("Enter Distance (km): ");
                        double dist = scanner.nextDouble();
                        if (dist<0){
                                System.out.println("Distance cant be negative. Try again!");
                        }else{
                                ft.addEntry(new TransportationEmission(id, "transportation", date, user, dist, transport));
                                System.out.println("Transportation added.");
                        }
                } else{
                        System.out.println("Error: '" + transport +". Please put something from our system.");
                }

                // --- FOOD ---
                } else if(choice==2){
                        System.out.print("Enter Meal Type (Vegan, Vegetarian, Poultry, Beef): ");
                        String meal = scanner.nextLine().toLowerCase();

                        if(meal.equals("vegan") || meal.equals("vegetarian") || meal.equals("poultry") || meal.equals("beef")) {
                        System.out.print("enter Number of Meals: ");
                        int meals = scanner.nextInt();
                        if(meals<0){
                                System.out.println("No of meals cant be negative. Try again!");
                        } else{
                                ft.addEntry(new FoodEmission(id, "Food", date, user, meal, meals));
                                System.out.println("Food added.");
                        }
                } else{
                        System.out.println("Error: '" + meal + ". Please put something from our system.");
                }

                // --- ENERGY ---
                } else if(choice == 3){
                        System.out.print("Enter Energy Source (Grid, Solar, Wind, Coal, Natural Gas, Nuclear, Diesel, Hydro): ");
                        String energy = scanner.nextLine().toLowerCase();

                        if(energy.equals("grid") || energy.equals("solar") || energy.equals("wind") || energy.equals("coal") || energy.equals("natural gas") || energy.equals("nuclear") || energy.equals("diesel") || energy.equals("hydro")) {
                        
                        System.out.print("Enter kWh used: ");
                        double kwh = scanner.nextDouble();
                        if(kwh<0) {
                                System.out.println("Energy usage cant be negative. Try again!");
                        }else{
                                ft.addEntry(new EnergyEmission(id, "Energy", date, user, kwh, energy));
                                System.out.println("Energy added.");
                        }
                }else{
                        System.out.println("Error: '" + energy + "'. Please put something in our system.");
                }
                }

                }catch(InputMismatchException e) {
                System.out.println("ENTER A NUMBER.");
                scanner.nextLine();
        }
        }

        ft.generateDailyReport();
        scanner.close();
}
}
