/**
 * Represents an energy-based emission source.
 * This class calculates carbon emissions based on electricity usage (kWh) and the type of energy source used.
 * 
 * Emission factors are expressed in kg CO2 per kWh.
 * 
 * @author Trisha
 */
public class EnergyEmission extends EmissionSource {

    
    private double kWhUsed;
    private String energySource;
    
    /**
     * Constructs an EnergyEmission object.
     *
     * @param sourceID Unique identifier for the emission source
     * @param category Category of emission (e.g., Energy)
     * @param date Date of emission entry
     * @param userName Name of the user logging the emission
     * @param kWhUsed Amount of electricity consumed in kilowatt-hours
     * @param energySource Type of energy source used
     */
    public EnergyEmission(String sourceID, String category, String date, String userName,
                          double kWhUsed, String energySource) {
        super(sourceID, category, date, userName);
        this.kWhUsed = Math.max(0, kWhUsed);
        this.energySource = energySource.toLowerCase();
    }

    /**
     * Calculates the carbon emission based on the energy source
     * and electricity consumption.
     * Emission factors are applied according to the type of energy source.
     *
     * @return Total carbon emission in kilograms of CO2
     */
    @Override
    public double calculateEmission() {
        /** Emission factor in kg CO₂ per kWh */
        double emissionFactor=0.0;

        if (this.energySource.equals(null)) {
            emissionFactor = 0.37;
        }else if (this.energySource.equals("grid")) {
            emissionFactor = 0.37;

        } else if (this.energySource.equals("solar")) {
            emissionFactor = 0.041;

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
            
        } else if (this.energySource.equals("natural gas")) {
            emissionFactor = 0.309;

        
        }else {
            System.out.println("Data unavailable for the energy source.");
            
        }

        return kWhUsed * emissionFactor;
    }

    /**
     * Gets the electricity usage in kWh.
     *
     * @return electricity consumption in kilowatt-hours
     */
    public double getkWhUsed() {
        return kWhUsed;
    }

    /**
     * Sets the electricity usage.
     *
     * @param kWhUsed electricity consumption in kilowatt-hours
     */
    public void setkWhUsed(double kWhUsed) {
        this.kWhUsed = kWhUsed;
    }

    /**
     * Gets the energy source type.
     *
     * @return energy source name
     */
    public String getEnergySource() {
        return energySource;
    }

    /**
     * Sets the energy source type.
     * Automatically converts it to lowercase for consistency.
     *
     * @param energySource energy source name
     */
    public void setEnergySource(String energySource) {
        this.energySource = energySource.toLowerCase();
    }

    /**
     * Returns a formatted string representation of the energy emission record.
     *
     * @return formatted emission details including calculated CO2
     */
    @Override
    public String toString() {
        return super.toString() + " | " + energySource + ", " + kWhUsed + " kWh | "+ String.format("%.2f", calculateEmission()) + " kg CO2";
    }
}