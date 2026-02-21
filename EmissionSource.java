public abstract class EmissionSource {

    private String sourceID;
    private String category;
    private String date;
    private String userName;

    public EmissionSource(String sourceID, String category, String date, String userName){
        this.sourceID = sourceID;
        this.category = category;
        this.date = date;
        this.userName = userName;
    }

    public String getSourceID(){return this.sourceID;}
    public void setSourceID(String sourceID){this.sourceID = sourceID;}

    public String getCategory(){return this.category;}
    public void setCategory(String category){this.category = category;}

    public String getDate(){return this.date;}
    public void setDate(String date){this.date = date;}

    public String getUserName(){return this.userName;}
    public void setUserName(String userName){this.userName = userName;}

    public abstract double calculateEmission();
    
    @Override
    public String toString(){
        return "Source ID: " + this.sourceID + 
        " Category: " + this.category + " Date: " 
        + this.date + " UserName: " + this.userName;
    }
    
}
