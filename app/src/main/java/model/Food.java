package model;

public class Food implements Comparable {

    /* DATA MEMBERS */
    private String name;
    private String ID;
    private String foodGroupName;
    private String foodGroupID;
    private double weight;				// Weight of a serving size? (Weight that nutrients are relative to?
    private String measure;				// The measurement given.  May or may not be serving size
    private Nutrient[] nutrients;


    public Food() {
        // Default Constructor
        this.name = "";
        this.ID = "";
        this.foodGroupID = "";
        this.foodGroupName = "";
        this.weight = 0.0;
        this.measure = "";
    }

    public Food(String name, String ID) {
        this();
        this.name = name;
        this.ID = ID;
    }

    public Food(String name, String ID, String foodGroupName) {
        this(name, ID);
        this.foodGroupName = foodGroupName;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) return -1;
        if (o == this) return 0;
        if (o.getClass() != this.getClass()) return -1;  // This is actually kind of rip haha

        // Now, just compare based on name length
        // This is a kind of heavy handed way to do this, but it's whatever
        Food f = (Food) o;
        return new Integer(this.getName().length()).compareTo(new Integer(f.getName().length()));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("Name: " + this.getName() + '\n');
        str.append("ID: " + this.getID() + '\n');

        if (this.foodGroupName != null && !this.foodGroupName.equals("")) {
            str.append("Food Group: " + this.getFoodGroupName() + '\n');
        }

        str.append("Measure: " + this.getMeasure() + '\n');
        str.append("Weight: " + this.getWeight() + '\n');

        str.append("\nNutrients: \n");

        if (this.nutrients != null) {
            for (int i = 0; i < this.nutrients.length; i++) {
                str.append(this.nutrients[i].toString());
                str.append('\n');
            }
        }

        return str.toString();
    }



    /* ACCESSOR METHODS */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFoodGroupName() {
        return foodGroupName;
    }

    public void setFoodGroupName(String foodGroupName) {
        this.foodGroupName = foodGroupName;
    }

    public String getFoodGroupID() {
        return foodGroupID;
    }

    public void setFoodGroupID(String foodGroupID) {
        this.foodGroupID = foodGroupID;
    }

    public Nutrient[] getNutrients() {
        return this.nutrients;
    }

    public void setNutrients(Nutrient[] nutrients) {
        this.nutrients = nutrients;
    }

    public void addNutrient(Nutrient n) {
        // Handle the case where this is our first nutrient added
        if (this.nutrients == null) {
            this.nutrients = new Nutrient[1];
            this.nutrients[0] = n;
            return;
        }

        int currentSize = this.nutrients.length;

        // Create an array of a length +1
        Nutrient[] newNutrients = new Nutrient[currentSize + 1];

        // Add all the current nutrients
        for (int i = 0; i < currentSize; i++) {
            newNutrients[i] = this.nutrients[i];
        }

        // Add the passed nutrient, then set our data member
        newNutrients[currentSize] = n;
        this.nutrients = newNutrients;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    @Override
    public Food clone() {
        Food cloneFood = new Food();

        // Copy all data values
        cloneFood.setFoodGroupID(getFoodGroupID());
        cloneFood.setFoodGroupName(getFoodGroupName());
        cloneFood.setID(getID());
        cloneFood.setMeasure(getMeasure());
        cloneFood.setName(getName());
        cloneFood.setWeight(getWeight());

        // Clone the nutrients as well
        for (Nutrient n : nutrients) {
            cloneFood.addNutrient(n.clone());
        }

        return cloneFood;
    }




// I think the main model item, "food", should be represented as what comes back from the food report
    // That is, it has an array of nutrients
    // However, I don't think that the nutrients should contain their servings and junk
    // We should keep track of that here, in some kind of 2D arraylist data structure
    // A List, of lists of "Measures"
    // Each List belongs to nutrient[i] in our array of nutrients
    //		This would be constructed upon being fed a list of nutrients, depending on the construction
}
