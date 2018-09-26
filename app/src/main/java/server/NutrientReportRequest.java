package server;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import Utility.Values;

public class NutrientReportRequest extends Request {

	private static final String NUTRIENT_REPORT_CONTEXT = "/nutrients/";


	/* DATA MEMBERS */
	private String foodGroup;
	private String format;
	private String maxItems;
	private String offset;
	private String foodID;
	private ArrayList<String> nutrients;
	private String sortType;
	private String subSet;


	public NutrientReportRequest() {
		this.foodGroup = "";
		this.format = this.FORMAT_DEFAULT;
		this.maxItems = this.MAX_ITEMS_DEFAULT;
		this.offset = this.OFFSET_DEFAULT;
		this.foodID = "";
		this.nutrients = new ArrayList<String>();
		this.sortType = this.SORT_ORDER_FOODNAME; 	// This request will mostly be used for single foods
		this.subSet = this.SUBSET_ABRIDGED;			// Use the abridged subset by default
	}

	public NutrientReportRequest(String foodID, ArrayList<String> nutrients) {
		this();
		this.foodID = foodID;
		this.nutrients = nutrients;
	}

	public NutrientReportRequest(ArrayList<String> nutrients) {
		this();
		this.nutrients = nutrients;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("NBDNO: " + this.foodID + '\n');
		sb.append("Nutrients: ");
		for (String n : this.nutrients) {
			sb.append(n + " ");
		}
		sb.append('\n');
		sb.append("Food Group: " + this.foodGroup + '\n');
		sb.append("Sort: " + this.sortType + '\n');
		sb.append("Max Items: " + this.maxItems + '\n');
		sb.append("Offset: " + this.offset + '\n');
		sb.append("Subset: " + this.subSet + '\n');
		sb.append("Format: " + this.format + '\n');

		return sb.toString();
	}

	@Override
	public URL getURL() {
		URL result = null;

		try {
			URI uri = new URI(this.URL_PREFIX + this.NUTRIENT_REPORT_CONTEXT);
			uri = appendURI(uri, this.FIELD_FORMAT + this.format);
			if (!this.foodGroup.equals(""))
				uri = appendURI(uri, this.FIELD_FOOD_GROUP + this.foodGroup);
			uri = appendURI(uri, this.FIELD_MAX_ITEMS + this.maxItems);
			uri = appendURI(uri, this.FIELD_OFFSET + this.offset);
			if (!this.foodID.equals("")) {
				uri = appendURI(uri, this.FIELD_FOOD_NUMBER + this.foodID);
//				uri = appendURI(uri, this.FIELD_SORT_ORDER + this.sortType); // Sort was breaking stuff :P
			}
			uri = appendURI(uri, this.FIELD_SUBSET + this.subSet);

			for (String nutrient : this.nutrients) {
				uri = appendURI(uri, this.FIELD_NUTRIENTS + nutrient);
			}
			uri = appendURI(uri, this.FIELD_SORT_ORDER + this.sortType);
			uri = appendURI(uri, this.FIELD_API + Values.API_KEY);

			// Return the URL!
			result = uri.toURL();
		} catch (URISyntaxException | MalformedURLException e) {
			e.printStackTrace();
		}

		return result;
	}


	public String getFoodGroup() {
		return foodGroup;
	}


	public void setFoodGroup(String foodGroup) {
		this.foodGroup = foodGroup;
	}


	public String getFormat() {
		return format;
	}


	public void setFormat(String format) {
		this.format = format;
	}


	public String getMaxItems() {
		return maxItems;
	}


	public void setMaxItems(String maxItems) {
		this.maxItems = maxItems;
	}


	public String getFoodID() {
		return foodID;
	}


	public void setFoodID(String foodID) {
		this.foodID = foodID;
	}


	public ArrayList<String> getNutrients() {
		return nutrients;
	}


	public void setNutrients(ArrayList<String> nutrients) {
		this.nutrients = nutrients;
	}


	public void addNutrient(String nutrientID) {
		this.nutrients.add(nutrientID);
	}


	public String getSortType() {
		return sortType;
	}


	public void setSortType(String sortType) {
		this.sortType = sortType;
	}


	public String getSubSet() {
		return subSet;
	}


	public void setSubSet(String subSet) {
		this.subSet = subSet;
	}






}
