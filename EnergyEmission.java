public class EnergyEmission extends EmissionSource{
    private double kWhUsed;
    private String energySource;
    
    public EnergyEmission(String sourceID, String category, String date, String userName, double kWhUsed, String energySource) {
        super(sourceID, category, date, userName);
        this.kWhUsed = kWhUsed;
        this.energySource = energySource.toLowerCase();
        
    }

    @Override
    public double calculateEmission() {

        double emissionFactor;
        
        if (this.energySource.equals("grid")) {
            emissionFactor = 0.37; // average grid emission factor
        } else if (this.energySource.equals("solar")) {
            emissionFactor = 0.041; // solar emission factor
        } else if (this.energySource.equals("wind")) {
            emissionFactor = 0.011;
            
        } else if (this.energySource.equals("hydro")) {

            emissionFactor = 0.004;
        } else if (this.energySource.equals("nuclear")) {

            emissionFactor = 0.012;
        } else if (this.energySource.equals("coal")) {

            emissionFactor = 0.405;
        
        } else if (this.energySource.equals("diesel")) {

            emissionFactor = 0.324;
        }else if (this.energySource.equals("natural gas")) {

            emissionFactor = 0.309;
        } else {
            emissionFactor = 0.37; // fallback to grid average
    }
        return kWhUsed * emissionFactor;                

    }


        
        

    public double getkWhUsed() {
        return kWhUsed;
    }

    public void setkWhUsed(double kWhUsed) {
        this.kWhUsed = kWhUsed;
    }

    public String getEnergySource() {
        return energySource;
    }

    public void setEnergySource(String energySource) {
        this.energySource = energySource.toLowerCase();
    }
    @Override
    public String toString() {
        return super.toString() +" | " + energySource +", " + kWhUsed + " kWh | " +String.format("%.2f", calculateEmission()) +" kg CO2";
    }



}
