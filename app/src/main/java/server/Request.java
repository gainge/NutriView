package server;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class Request {

	/* DATA MEMBERS */

	
	public Request() {
		// Default constructor
	}
	
	public abstract URL getURL();
	
	protected URI appendURI(URI uri, String appendQuery) {
        URI oldUri = uri;

        String newQuery = oldUri.getQuery();
        if (newQuery == null) {
            newQuery = appendQuery;
        } else {
            newQuery += "&" + appendQuery;  
        }

        // Create a new URI object from the modified old
        URI newUri = null;
		try {
			newUri = new URI(oldUri.getScheme(), 
								oldUri.getAuthority(),
								oldUri.getPath(), 
								newQuery, 
								oldUri.getFragment());
		} catch (URISyntaxException e) {
			System.out.println("URI format not valid!");
			System.out.println("URI: " + oldUri.toString());
			System.out.println("Append: " + appendQuery);
			e.printStackTrace();
		} finally {
			// If an exception was thrown, output and return an unchanged URI
			if (newUri == null) {
				return oldUri;
			}
		}

        return newUri;
    }
	
	// URL prefix for food database
	protected static final String URL_PREFIX = "https://api.nal.usda.gov/ndb";
	// API Key for making requests
	
	/* REQUEST FIELD STRINGS */
	protected static final String FIELD_API = "api_key=";
	protected static final String FIELD_FOOD_NUMBER = "ndbno=";
	protected static final String FIELD_NUTRIENTS = "nutrients=";
	protected static final String FIELD_SUBSET = "subset=";
	protected static final String FIELD_TYPE = "type=";
	protected static final String FIELD_FORMAT = "format=";
	protected static final String FIELD_DATA_SOURCE = "ds=";
	protected static final String FIELD_FOOD_GROUP= "fg=";
	protected static final String FIELD_SEARCH_TERMS = "q=";
	protected static final String FIELD_LIST_TYPE = "lt=";
	protected static final String FIELD_MAX_ITEMS = "max=";
	protected static final String FIELD_OFFSET = "offset=";
	protected static final String FIELD_SORT_ORDER = "sort=";
	
	/* REQUEST FIELD VALUES */
	// We might want to move these to the utility package eventually
	protected static final String SOURCE_BRANDED = "Branded Food Products";
	protected static final String SOURCE_STANDARD = "Standard Reference";
	
	protected static final String FG_BAKED_PRODUCTS = "Baked Products";
	protected static final String FG_FAST_FOOD = "Fast Foods";
	protected static final String FG_MEALS_ENTREES = "Meals, Entrees, and Side Dishes";
	protected static final String FG_SPICES_HERBS = "Spices and Herbs";
	protected static final String FG_SWEETS = "Sweets";
	protected static final String FG_VEGETABLE = "Vegetables and Vegetable Products";
	
	protected static final String SORT_ORDER_DEFAULT = "r";
	protected static final String SORT_ORDER_NAME = "n";
	protected static final String SORT_ORDER_RELEVANCE = "r";
	protected static final String SORT_ORDER_CONTENT = "c";
	protected static final String SORT_ORDER_FOODNAME = "f";
	
	protected static final String SUBSET_ABRIDGED = "1";
	protected static final String SUBSET_FULL = "0";
	
	protected static final String MAX_ITEMS_DEFAULT = "50";
	protected static final String OFFSET_DEFAULT = "0";
	
	protected static final String TYPE_DEFAULT = "b";
	protected static final String TYPE_BASIC = "b";
	protected static final String TYPE_FULL = "f";
	protected static final String TYPE_STATS = "s";
	
	protected static final String FORMAT_DEFAULT = "json";
	protected static final String FORMAT_JSON = "json";
	protected static final String FORMAT_XML = "xml";
	
	protected static final String LIST_TYPE_DEFAULT = "f";
	protected static final String LIST_TYPE_FOOD = "f";
	protected static final String LIST_TYPE_ALL_NUTRIENTS = "n";
	protected static final String LIST_TYPE_SPECIALTY_NUTRIENTS = "ns";
	protected static final String LIST_TYPE_STANDARD_RELEASE_NUTRIENTS = "nr";
	protected static final String LIST_TYPE_FOOD_GROUP = "g";
}
