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
    
    @param sourceID Unique identifier for the emission source
    @param category Category of emission (e.g., Energy, Transport)
    @param date Date of emission entry
    @param userName Name of the user logging into the emission
     **/

    public EmissionSource(String sourceID, String category, String date, String userName){
        this.sourceID = sourceID;
        this.category = category;
        this.date = date;
        this.userName = userName;
    }

    /**
    Gets and sets the basic emission source properties:
    source ID, category, date, and user name.

    These methods provide controlled access to the private
    instance variables of the emission source.
    **/

    public String getSourceID(){return this.sourceID;}
    public void setSourceID(String sourceID){this.sourceID = sourceID;}

    public String getCategory(){return this.category;}
    public void setCategory(String category){this.category = category;}

    public String getDate(){return this.date;}
    public void setDate(String date){this.date = date;}

    public String getUserName(){return this.userName;}
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
        return "Source ID: " + this.sourceID + 
        " Category: " + this.category + " Date: " 
        + this.date + " UserName: " + this.userName;
    }
    
}
