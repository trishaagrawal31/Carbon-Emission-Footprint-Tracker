/**
 * Represents an Food-based emission source.
 * This class calculates carbon emissions based on meal type and number of meals used
 *
 * Emission factors are expressed in kg CO2 per kWh.
 *
 * @author Sadaf
 */



public class FoodEmission extends EmissionSource  {
    private String mealType;
    private int numberOfMeals;
    /**
     * 
     * @param sourceID
     * @param category
     * @param date
     * @param username
     * @param MealType the type of meal consumed
     * @param numberOfMeals the number of meals consumed
     */
    public FoodEmission(String sourceID, String category, String date, String userName, String mealType, int numberOfMeals){
        super(sourceID, category,date,userName);
    this.mealType= mealType.toLowerCase();
    this.numberOfMeals= numberOfMeals;

    
}
    public String getMealType() {
        return mealType;
    }
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
    public int getNumberOfMeals() {
        return numberOfMeals;
    }
    public void setNumberOfMeals(int numberOfMeals) {
        this.numberOfMeals = numberOfMeals;
    }
@Override
/*Calculates emissions based on  each type of meal and the number of meals */
public double calculateEmission(){
    double emissions=0.0;
    if(this.mealType.equals("vegan")){
            emissions=2.90; //random
        }
        else if(this.mealType.equals("vegetarian")){
            emissions= 3.80;
        }
        else if(this.mealType.equals("poultry")){
            emissions=4.08;
        }
        else if(this.mealType.equals("beef")){
            emissions=15.50;}
        else{
            System.out.println("Try again: Please pick from the following options Vegan | Vegetarian | Poultry | Beef |");

        }
        double totalEmission = numberOfMeals*emissions;
        return Math.round(totalEmission*100.0)/100.0; //@returns the amount of emissions per the number of meals;
}

@Override
public String toString(){
    return super.toString()+ "| The meal Type: " + mealType + "| The number of meals: " + numberOfMeals + " | Total Food emissions: " + calculateEmission();
}




}
