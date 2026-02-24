/**
 * Represents a food-based emission source.
 * This class calculates carbon emissions based on meal type and number of meals consumed.
 *
 * Emission factors are expressed in kg CO2 per meal for different meal types:
 * - Vegan: 0.5 kg CO2
 * - Vegetarian: 0.8 kg CO2
 * - Poultry: 1.82 kg CO2
 * - Beef: 3.3 kg CO2
 *
 * @author Sadaf
 */
public class FoodEmission extends EmissionSource {
    

    private String mealType;
    
    private int numberOfMeals;
    /**
     * Constructs a FoodEmission object.
     * 
     * @param sourceID Unique identifier for this emission source
     * @param category Category of emission 
     * @param date Date of the emission entry 
     * @param userName Name of the user logging this emission
     * @param mealType The type of meal consumed (vegan, vegetarian, poultry, or beef)
     * @param numberOfMeals The number of meals of this type consumed
     */
    public FoodEmission(String sourceID, String category, String date, String userName, String mealType, int numberOfMeals) {
        super(sourceID, category, date, userName);
        this.mealType = mealType.toLowerCase();
        this.numberOfMeals = numberOfMeals;
    }
    /**
     * Gets the meal type.
     * 
     * @return The type of meal consumed (vegan, vegetarian, poultry, or beef)
     */
    public String getMealType() {
        return mealType;
    }
    
    /**
     * Sets the meal type.
     * 
     * @param mealType The type of meal to set
     */
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
    
    /**
     * Gets the number of meals.
     * 
     * @return The number of meals consumed
     */
    public int getNumberOfMeals() {
        return numberOfMeals;
    }
    
    /**
     * Sets the number of meals.
     * 
     * @param numberOfMeals The number of meals to set
     */
    public void setNumberOfMeals(int numberOfMeals) {
        this.numberOfMeals = numberOfMeals;
    }
    /**
     * Calculates the total carbon emissions based on meal type and number of meals.
     * Each meal type has a specific emission factor that is multiplied by the number of meals.
     *
     * @return Total carbon emissions in kilograms of CO2
     */
    @Override
    public double calculateEmission() {
        double emissions = 0.0;

        if (this.mealType.equals("vegan")) {
            emissions = 0.50;
        } else if (this.mealType.equals("vegetarian")) {
            emissions = 0.80;
        } else if (this.mealType.equals("poultry")) {
            emissions = 1.82;
        } else if (this.mealType.equals("beef")) {
            emissions =  3.3;
        }
        
        return emissions * numberOfMeals;
}

    /**
     * Returns a formatted string representation of the food emission record.
     * 
     * @return Formatted emission details including meal type, number of meals, and calculated CO2
     */
    @Override
    public String toString() {
        return super.toString() + " | " + mealType + ", " + numberOfMeals + " meals | " + String.format("%.2f", calculateEmission()) + " kg CO2";
    }




}
