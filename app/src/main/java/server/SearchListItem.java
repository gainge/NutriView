package server;

public class SearchListItem {

	/* DATA MEMBERS */
	private int offset;				// Offset from the start of the search
	private String group;			// Food Group the result belongs to
	private String name;			// Name of the food item
	private String ndbno;			// ID# of the food item
	private String ds;				// Data source, branded or standard reference
	
	
	/* CONSTRUCTORS */
	public SearchListItem() {
		// Deafult Constructor
	}

	// Getters
	public int getOffset() {
		return offset;
	}

	public String getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}
	
	public String getNdbno() {
		return ndbno;
	}
	
	// More intuitive methods for id# accessors
	public String getID() {
		return this.getNdbno();
	}

	public String getDataSource() {
		return ds;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("name: " + this.getName() + "\n");
		sb.append("group: " + this.getGroup() + "\n");
		sb.append("ID: " + this.getNdbno() + "\n");
		
		return sb.toString();
	}
	
	
	
	
	
}
