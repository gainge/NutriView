package server;

public class SearchList {
	
	/* DATA MEMBERS */
	private String q;					// String of search terms used
	private int start;					// Start index of search results
	private int end;					// End index of search results
	private int total;					// Total search results
	private String group;				// Food group
	private String sort;				// 'n' by name, 'r' by relevance
	private SearchListItem[] item;
	
	
	public SearchList() {
		// Default Constructor
	}


	public String getQuery() {
		return q;
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


	public String getGroup() {
		return group;
	}


	public String getSort() {
		return sort;
	}


	public SearchListItem[] getItems() {
		return item;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Search Terms: " + this.getQuery() + "\n");
		sb.append("Total results: " + this.getTotal() + "\n");
		sb.append("----------------------------------------\n");
		
		for (int i = 0; i < this.getItems().length; i++) {
			sb.append(this.getItems()[i].toString() + "\n");
		}
		
		return sb.toString();
	}
	
	
}
