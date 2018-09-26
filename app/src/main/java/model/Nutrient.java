package model;

// Not super sure what purposes these classes will serve right now
// but I'm making them 

public class Nutrient {
    /* DATA MEMBERS */
    private String nutrient_id;			// The nutrient ID!
    private String name;			// The name of the nutrient
    private String unit;				// The units of measure
    private double value;				// How much we have! (might want accessor that can return value as double?
    private double gm;					// The value represented as it's 100 gram equivalent


    public Nutrient() {
        // Default Constructor
        // Might want to do some stuff with this eventually
    }

    public String getNutrientID() {
        return nutrient_id;
    }

    public void setNutrientID(String id) {
        this.nutrient_id = id;
    }


    public String getNutrientName() {
        return name;
    }

    public void setNutrientName(String name) {
        this.name = name;
    }


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setValue(String value) {
        try {
            this.value = Double.parseDouble(value);
        } catch (Exception e) {
            this.value = 0.0;
        }
    }

    public double getGeneralMeasurement() {
        return gm;
    }

    public void setGeneralMeasurement(double gm) {
        this.gm = gm;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("Name: " + this.getNutrientName() + '\n');
        str.append("ID: " + this.getNutrientID() + '\n');
        str.append("Unit: " + this.getUnit() + '\n');
        str.append("Value: " + this.getValue() + '\n');

        return str.toString();
    }

    @Override
    public Nutrient clone() {
        Nutrient cloneNutrient = new Nutrient();

        cloneNutrient.setValue(getValue());
        cloneNutrient.setGeneralMeasurement(getGeneralMeasurement());
        cloneNutrient.setNutrientID(getNutrientID());
        cloneNutrient.setNutrientName(getNutrientName());
        cloneNutrient.setUnit(getUnit());

        return cloneNutrient;
    }

}
