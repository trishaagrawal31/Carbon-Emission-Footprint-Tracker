/**
Represents a general emission source in the carbon footprint tracking system.

This abstract class serves as a base class for all specific emission types
(e.g., energy, transport, waste). It stores common information such as
source ID, category, date, and user name.

@author Adira 
**/

public abstract class EmissionSource {

    private String sourceID;
    private String category;
    private String date;
    private String userName;

     /**
    Constructs an EmissionSource object.
    
    @param sourceID ID for the emission source
    @param category category of emission 
    @param date date of emission entry
    @param userName name of the user logging into the emission
     **/

    public EmissionSource(String sourceID, String category, String date, String userName){
        this.sourceID = sourceID;
        this.category = category;
        this.date = date;
        this.userName = userName;
    }

    /**
     * Gets the source ID for this emission entry.
     * @return The unique identifier for this emission source
     */
    public String getSourceID(){return sourceID;}
    
    /**
     * Sets the source ID for this emission entry.
     * @param sourceID The unique identifier to assign
     */
    public void setSourceID(String sourceID){this.sourceID = sourceID;}

    /**
     * Gets the category of emission.
     * @return The category (e.g., Transportation, Energy, Food)
     */
    public String getCategory(){return category;}
    
    /**
     * Sets the category of emission.
     * @param category The emission category to assign
     */
    public void setCategory(String category){this.category = category;}

    /**
     * Gets the date of the emission entry.
     * @return The date string for this emission
     */
    public String getDate(){return date;}
    
    /**
     * Sets the date of the emission entry.
     * @param date The date to assign
     */
    public void setDate(String date){this.date = date;}

    /**
     * Gets the username of the person who logged this emission.
     * @return The name of the user
     */
    public String getUserName(){return userName;}
    
    /**
     * Sets the username of the person who logged this emission.
     * @param userName The user name to assign
     */
    public void setUserName(String userName){this.userName = userName;}

    /**
    Abstract method to calculate carbon emissions.
      
    Each subclass must provide its own implementation
    depending on the emission type.

    **/

    public abstract double calculateEmission();

    /**
    Returns formatted source information
    **/
    
    @Override
    public String toString(){
        return this.sourceID + " | "+ this.category + " | " + this.date + " | " + this.userName;
    }
    
}
