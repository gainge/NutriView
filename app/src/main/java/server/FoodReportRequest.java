package server;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import Utility.Values;

public class FoodReportRequest extends Request {
	
	// Context identifier
	private static final String FOOD_REPORT_CONTEXT = "/reports/";
	
	/* DATA MEMBERS */
	private String foodID;
	private String reportType;
	private String format;

	public FoodReportRequest() {
		// Default Constructor
		this.foodID = "";
		this.reportType = this.TYPE_DEFAULT;
		this.format = this.FORMAT_DEFAULT;
	}
	
	public FoodReportRequest(String foodID) {
		this();
		this.foodID = foodID;
	}
	
	public FoodReportRequest(String foodID, String reportType) {
		this();
		this.foodID = foodID;
		this.reportType = reportType;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("NDBNO: " + this.foodID + 'n');
		sb.append("Report Type: " + this.reportType + 'n');
		sb.append("Format: " + this.format + 'n');
		
		return sb.toString();
	}
	
	@Override
	public URL getURL() {
		URL result = null;
		
		try {
			URI uri = new URI(this.URL_PREFIX + this.FOOD_REPORT_CONTEXT);
			uri = appendURI(uri, this.FIELD_FORMAT + this.format);
			uri = appendURI(uri, this.FIELD_FOOD_NUMBER + this.foodID);
			uri = appendURI(uri, this.FIELD_TYPE + this.reportType);
			uri = appendURI(uri, this.FIELD_API + Values.API_KEY);
			
			result = uri.toURL();
		} catch (URISyntaxException | MalformedURLException e) {
			e.printStackTrace();
		}
		
		return result;
		
	}

	public String getFoodID() {
		return foodID;
	}

	public void setFoodID(String foodID) {
		this.foodID = foodID;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	
	

}
