package server;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import Utility.Values;

public class SearchRequest extends Request {
	private static final String FOOD_SEARCH_CONTEXT = "/search/";
	
	/* DATA MEMBERS */
	private String searchTerms;
	private String dataSource;
	private String foodGroup;
	private String sortType;
	private String maxItems;
	private String offset;
	private String format;
	
	
	public SearchRequest() {
		// Initialize variables to default values
		this.searchTerms = "";
		this.dataSource = SOURCE_STANDARD;
		this.foodGroup = "";
		this.sortType = SORT_ORDER_DEFAULT;
		this.maxItems = MAX_ITEMS_DEFAULT;
		this.offset = OFFSET_DEFAULT;
		this.format = FORMAT_DEFAULT;
	}
	
	public SearchRequest(String searchTerms) {
		this();
		this.searchTerms = searchTerms;
	}
	
	public SearchRequest(String searchTerms, String dataSource, String foodGroup) {
		this(searchTerms);
		this.dataSource = dataSource;
		this.foodGroup = foodGroup;
	}
	
	public SearchRequest(String searchTerms, String dataSource, String foodGroup, String sortType, String maxItems) {
		this(searchTerms, dataSource, foodGroup);
		this.sortType = sortType;
		this.maxItems = maxItems;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Search: " + this.searchTerms + '\n');
		sb.append("Data Source: " + this.dataSource + '\n');
		sb.append("Food Group: " + this.foodGroup + '\n');
		sb.append("Sort: " + this.sortType + '\n');
		sb.append("Max Items: " + this.maxItems + '\n');
		sb.append("Offset: " + this.offset + '\n');
		sb.append("Format: " + this.format + '\n');
		
		return sb.toString();
	}
	
	
	@Override
	public URL getURL() {
		URL result = null;
		
		try {
			URI uri = new URI(this.URL_PREFIX + this.FOOD_SEARCH_CONTEXT);
			uri = appendURI(uri, this.FIELD_FORMAT + this.format);
			if (!this.searchTerms.equals(""))
				uri = appendURI(uri, this.FIELD_SEARCH_TERMS + this.searchTerms);
			uri = appendURI(uri, this.FIELD_DATA_SOURCE + this.dataSource);
			if (!this.foodGroup.equals("")) 
				uri = appendURI(uri, this.FIELD_FOOD_GROUP + this.foodGroup);
			uri = appendURI(uri, this.FIELD_SORT_ORDER + this.sortType);
			uri = appendURI(uri, this.FIELD_MAX_ITEMS + this.maxItems);
			uri = appendURI(uri, this.FIELD_OFFSET + this.offset);
			uri = appendURI(uri, this.FIELD_API + Values.API_KEY);
			
			result = uri.toURL();
		} catch (URISyntaxException | MalformedURLException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	
	public String getSearchTerms() {
		return searchTerms;
	}

	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getFoodGroup() {
		return foodGroup;
	}

	public void setFoodGroup(String foodGroup) {
		this.foodGroup = foodGroup;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public String getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(String maxItems) {
		this.maxItems = maxItems;
	}

	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	
	
}
