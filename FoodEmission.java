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
    public FoodEmission(String sourceID, String category, String date, String userName, String MealType, int numberOfMeals){
        super(sourceID, category,date,userName);
    this.mealType= mealType.toLowerCase();
    this.numberOfMeals= numberOfMeals;

    
}
    public String getMealType() {
        return mealType;//@returns the meal type 
    }
    public void setMealType(String mealType) {
        this.mealType = mealType;//@sets the meal type
    }
    public int getNumberOfMeals() {
        return numberOfMeals; //@return it returns number of meals
    }
    public void setNumberOfMeals(int numberOfMeals) {
        this.numberOfMeals = numberOfMeals;//@ it sets the number of meals
    }
@Override
/*Calculates emissions based on  each type of meal and the number of meals */
public double calculateEmission(){
    double emissions=0.0;
    if(this.mealType.equals("vegan")){
            emissions=2.90; //sets the emission value as 2.90 when vegan meal type is selected 
        }
        else if(this.mealType.equals("vegetarian")){
            emissions= 3.80;//sets the emission value as 3.80 when vegan meal type is selected
        }
        else if(this.mealType.equals("poultry")){
            emissions=4.08;//sets the emission value as 4.08 when vegan meal type is selected
        }
        else if(this.mealType.equals("beef")){
            emissions=15.50;}//sets the emission value as 15.50 when vegan meal type is selected
        else{
            System.out.println("Try again: Please pick from the following options Vegan | Vegetarian | Poultry | Beef |");}
        //incase a wrong meal type is selected by the user it prompts them to select again based on the programmed options 
        double totalEmission = numberOfMeals*emissions;
        return Math.round(totalEmission*100.0)/100.0; //@returns the amount of emissions per the number of meals 
}

@Override
public String toString(){
    return super.toString()+ "| The meal Type: " + mealType + "| The number of meals: " + numberOfMeals + " | Total Food emissions: " + calculateEmission();
}// @ returns for example "Source ID: T-001 Category: Food Date:2026-02-12   UserName: Alice | The meal Type: Vegan | The number of meals: 1 | Total Food emissions:3.80 ;




}
