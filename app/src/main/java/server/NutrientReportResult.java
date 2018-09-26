package server;

import java.util.ArrayList;

import model.Food;

public class NutrientReportResult extends Result {

	/* DATA MEMBERS */
	// sub-wrapper class goes here
	private NutrientReportBody report;


	public NutrientReportResult() {
		// Default Constructor
	}

	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append(report.toString());

		return str.toString();
	}


	public Food getResultAsFood() {
		return this.report.getReportAsFood();
	}

	public ArrayList<Food> getResultAsFoodArrayList() {
		ArrayList<Food> resultList = new ArrayList<Food>();

		// Add each food in our result set
		for (int i = 0; i < this.report.getFoods().length; i++) {
			resultList.add(getFoodAtIndex(i));
		}

		return resultList;
	}

	public Food getFoodAtIndex(int index) {
		return this.report.getFoodAtIndex(index);
	}



}
