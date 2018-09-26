package server;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import Utility.Values;

public class ListRequest extends Request {

	// Context constant
	private static final String LIST_CONTEXT = "/list/";
	
	/* DATA MEMBERS */
	private String listType;
	private String maxItems;
	private String offset;
	private String sortType;
	private String format;
	
	
	public ListRequest() {
		// Initialize default values
		this.listType = this.LIST_TYPE_DEFAULT;
		this.maxItems = this.MAX_ITEMS_DEFAULT;
		this.offset = this.OFFSET_DEFAULT;
		this.sortType = this.SORT_ORDER_NAME;
		this.format = this.FORMAT_DEFAULT;
	}
	
	public ListRequest(String listType) {
		this();
		this.listType = listType;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("List Type: " + this.listType + '\n');
		sb.append("Max Items: " + this.maxItems + '\n');
		sb.append("Offset: " + this.offset + '\n');
		sb.append("Sort Type: " + this.sortType + '\n');
		sb.append("Format: " + this.format + '\n');
		
		return sb.toString();
	}
	
	@Override
	public URL getURL() {
		URL result = null;
		
		try {
			URI uri = new URI(this.URL_PREFIX + this.LIST_CONTEXT);
			uri = appendURI(uri, this.FIELD_FORMAT + this.format);
			uri = appendURI(uri, this.FIELD_LIST_TYPE + this.listType);
			uri = appendURI(uri, this.FIELD_MAX_ITEMS + this.maxItems);
			uri = appendURI(uri, this.FIELD_OFFSET + this.offset);
			uri = appendURI(uri, this.FIELD_SORT_ORDER + this.sortType);
			uri = appendURI(uri, this.FIELD_API + Values.API_KEY);
			
			result = uri.toURL();
		} catch (URISyntaxException | MalformedURLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
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

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	

}
