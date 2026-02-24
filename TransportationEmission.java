/**
 * Represents an Transport-based emission source.
 * This class calculates carbon emissions based on distance travelled and type of transport mode used
 *
 * Emission factors are expressed in kg CO2 per km
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
    /**
     * 
     * @return double distance in km
     */
    public double getDistance() {return distance;}
    /**
     * 
     * @param distance
     * @return void
     */
    public void setDistance(double distance) {this.distance = distance;}
    /**
     * 
     * @return String type of transport
     */
    public String getTransport() {return transport;}
    /**
     * 
     * @param transport
     * @return void
     */
    public void setTransport(String transport) {this.transport = transport;}

    @Override
    /**
     * calculates the total emission caused based on each type of vehicle
     * Which form of transport has the smallest carbon footprint? - Our World in Data
     * @return double total emission factor
     */


    public double calculateEmission(){
        double emissiona=0.0;
        if(this.transport.equals("car")){
            emissiona=0.170;
        }
        else if(this.transport.equals("bus")){
            emissiona=0.097;
        }
        else if(this.transport.equals("train")){
            emissiona=0.035;
        }
        else if(this.transport.equals("cycle")){
            emissiona=0.00;
        }
        else {
            System.out.println("please put something from our choices");
    }
    double totalEmission = distance*emissiona;
    return Math.round(totalEmission*100.0)/100.0;
    }

    @Override
    /**
     * Returns a formatted string representation of the transport emission record.
     * @return formatted emission details including calculated CO2
     */
    public String toString() {
        return super.toString()+" | Transport used :"+ transport +" | Distance travelled in km: "+distance+" | total emission is: "+calculateEmission() +" kg CO2/km";
    }
}
