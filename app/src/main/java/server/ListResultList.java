package server;

// This is the json object that will be wrapped by our "ListResult" Class

public class ListResultList {
	
	/* DATA MEMBERS */
	private String lt;				// List Type
	private int start;				// Starting index
	private int end;				// ending index
	private int total;				// total items returned
	private String sr;				// Junk, don't really need it for anything :P
	private String sort;			// Sort order of the list!  n for name/id
	private ListResultItem[] item;	// The list of result items!
	
	public ListResultList() {
		// Default Constructor
	}

	/* ACCESSOR METHODS */
	public String getListType() {
		return lt;
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

	public String getSortType() {
		return sort;
	}

	public ListResultItem[] getItems() {
		return item.clone();
	}
	
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("List Type: " + getListTypeFormatted() + '\n');
		str.append("Start: " + this.getStart() + '\n');
		str.append("End: " + this.getEnd() + '\n');
		str.append("Total: " + this.getTotal() + '\n');
		str.append("Items: " + '\n');
		
		// Iterate over each item in the list
		for (ListResultItem i : this.item) {
			str.append(item.toString() + '\n');
		}
		
		return str.toString();
	}
	
	private String getListTypeFormatted() {
		if (getListType().equals("f")) {
			return "Food";
		}
		else if (getListType().equals("n")) {
			return "All Nutrients";
		}
		else if (getListType().equals("ns")) {
			return "Specialty Nutrients";
		}
		else if (getListType().equals("nr")) {
			return "Standard Release Nutrients Only";
		}
		else if (getListType().equals("g")) {
			return "Food Group";
		}
		else {
			return "UNKOWN";
		}
	}
	
	

}
