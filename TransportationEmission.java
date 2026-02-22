/**
 * Represents an Transport-based emission source.
 * This class calculates carbon emissions based on distance travelled and type of transport mode used
 * 
 * Emission factors are expressed in kg CO2 per kWh.
 * 
 * @author Aaliya
 */

public class TransportationEmission extends EmissionSource {
    private double distance;
    private String transport;

    /**
     * 
     * @param sourceID
     * @param category
     * @param date
     * @param username
     * @param distance distance travelled using said vehicle
     * @param transport type of vehicle used to travel
     */


    public TransportationEmission(String sourceID, String category, String date, String username, double distance, String transport){
        super(sourceID, category, date, username);
        this.distance=distance;
        this.transport=transport.toLowerCase();
        

    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public String getTransport() {
        return transport;
    }
    public void setTransport(String transport) {
        this.transport = transport;
    }

    @Override
    /**
     * calculates the total emission caused based on each type of vehicle 
     */

    public double calculateEmission(){
        double emissiona=0.0;
        if(this.transport.equals("car")){
            emissiona=1.43; //random
        }
        else if(this.transport.equals("bus")){
            emissiona=2.65;
        }
        else if(this.transport.equals("train")){
            emissiona=3;
        }
        else if(this.transport.equals("cycle")){
            emissiona=0.0;
        
        }
        else {
            System.out.println("please put something from our choices");
    }
    return distance*emissiona;
    }

    @Override
    public String toString() {
        return super.toString()+" | transport used "+ transport +" | distance travelled in km "+distance+" total emission is | "+calculateEmission();

    }
}}
