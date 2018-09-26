package server;

import model.Food;
import model.Nutrient;

public class NutrientReportBody {

	/* DATA MEMBERS */
	private String sr;						// Standard release version aka garb
	private String subset;					// Full List of foods or abridged list
	private int start;						// Starting index of report
	private int end;						// Ending index of report
	private int total;						// Total number of items returned
	private NR_Food[] foods;				// The array of nutrient-report formatted foods!




	public NutrientReportBody() {
		// Default Constructor
	}


	// Here I think we need a private class for food and a private class for nutrient
	// These will be in the special format that is given back to us by the report
	// We can then construct the "Proper" food objects based on the results


	public String getSubset() {
		return subset;
	}


	public int getStart() {
		return start;
	}


	public int getEnd() {
		return end;
	}


	public int getTotal() {
		return total;
	}


	public NR_Food[] getFoods() {
		return foods;
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append("Start: " + this.getStart() + '\n');
		str.append("End: " + this.getEnd() + '\n');
		str.append("Total: " +  this.getTotal() + '\n');
		str.append("Foods: " + '\n');

		// Add the toString for each food in our list!
		for (NR_Food f : this.foods) {
			str.append(f.toString());
			str.append('\n');
		}

		return str.toString();
	}

	public Food getReportAsFood() {
		return getFoodAtIndex(0);	// Return the first (and likely only) food in our structure
	}

	public Food getFoodAtIndex(int index) {
		if (this.foods == null || this.foods.length == 0 || index < 0 || index >= this.foods.length) {
			return null;
		}

		Food food = new Food();

		// initialize some of the data in the food object
		food.setID(this.foods[index].getFoodID());
		food.setName(this.foods[index].getName());
		food.setMeasure(this.foods[index].getMeasure());
		food.setWeight(this.foods[index].getWeight());

		// Now time to set the nutrients!
		NR_Nutrient[] nutrients = this.foods[index].getNutrients();

		int numNutrients = nutrients.length;

		// Iterate over the nutrients
		for (int i = 0; i < numNutrients; i++) {
			Nutrient n = new Nutrient();

			n.setNutrientID(nutrients[i].getNutrientID());
			n.setNutrientName(nutrients[i].getNutrientName());
			n.setUnit(nutrients[i].getUnit());
			n.setValue(nutrients[i].getValue());
			n.setGeneralMeasurement(nutrients[i].getGeneralMeasurement());

			// Add the nutrient to our food
			food.addNutrient(n);
		}

		return food;
	}






	/**
	 * NR_Food Class
	 * @author Gibson
	 *
	 * This class represents a food as presented by the return body of a nutrient report
	 * This is differently formatted than how foods are presented in food reports, so another class is needed
	 * One that can be tailored specifically to our needs!
	 */
	public class NR_Food {
		/* DATA MEMBERS */
		private String ndbno;				// Food ID#
		private String name;				// Name of the food
		private double weight;				// Weight of a serving size? (Weight that nutrients are relative to?
		private String measure;				// The measurement given.  May or may not be serving size
		private NR_Nutrient[] nutrients;	// The nutrient objects which we requested!


		public NR_Food() {
			// Default constructor
		}


		public String getFoodID() {
			return ndbno;
		}


		public String getName() {
			return name;
		}

		/**
		 *
		 * @return the weight of the serving size, in grams (?)
		 */
		public double getWeight() {
			return weight;
		}


		public String getMeasure() {
			return measure;
		}


		public NR_Nutrient[] getNutrients() {
			return nutrients.clone();
		}


		@Override
		public String toString() {
			StringBuilder str = new StringBuilder();

			str.append("Name: " + this.getName() + '\n');
			str.append("ID: " + this.getFoodID() + '\n');
			str.append("Measure: " + this.getMeasure() + '\n');
			str.append("Weight: " + this.getWeight() + '\n');
			str.append("Nutrients: " + '\n');

			// Add the descriptions of each of our nutrients!
			for (NR_Nutrient n : this.nutrients) {
				str.append(n.toString());
				str.append('\n');
			}

			return str.toString();
		}


	}


	/**
	 * NR_Nutrient Class
	 * @author Gibson
	 *
	 * This class is made to wrap up the nutrient data returned by the nutrient report request
	 * Because nutrient formatting is inconsistent across requests (grrrr...) we need another class
	 * The base Nutrient class (model.Nutrient.java) will be able to take an instance of NR_Nutrient as a parameter
	 * 		For construction, ensuring that data is consistently aggregated!
	 *
	 */
	public class NR_Nutrient {
		/* DATA MEMBERS */
		private String nutrient_id;			// The nutrient ID!
		private String nutrient;			// The name of the nutrient
		private String unit;				// The units of measure
		private String value;				// How much we have! (might want accessor that can return value as double?
		private double gm;					// The value represented as it's 100 gram equivalent


		public NR_Nutrient() {
			// Default constructor
		}


		public String getNutrientID() {
			return nutrient_id;
		}


		public String getNutrientName() {
			return nutrient;
		}


		public String getUnit() {
			return unit;
		}


		public String getValue() {
			return value;
		}


		public double getGeneralMeasurement() {
			return gm;
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


	}



}
