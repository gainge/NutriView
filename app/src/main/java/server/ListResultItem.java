package server;

public class ListResultItem {
	
	/* DATA MEMBERS */
	
	private int offset;					// Offset in the total list
	private String id;					// The id (food id, nutrient id, food group id, etc.) associated with this item
	private String name;				// The name of the item!
	
	public ListResultItem() {
		// Default Constructor
	}

	public int getOffset() {
		return offset;
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		// Build the string!
		str.append("Name: " + getName() + '\n');
		str.append("ID: " + getID() + '\n');
		str.append("Offset: " + getOffset() + '\n');
		
		return str.toString();
	}

}
