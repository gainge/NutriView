package server;

public class ListResult extends Result {
	
	/* DATA MEMBERS */
	private ListResultList list;
	
	public ListResult() {
		// Default Constructor
	}
	
	// Accessor for the list itself
	public ListResultList getList() {
		return this.list;
	}
	
	@Override
	public String toString() {
		return this.list.toString();
	}
	
	// We'll definitely need more functions here that can make acessing the data easier
	public ListResultItem[] getItems() {
		return this.list.getItems();
	}
	
	

	
	
	
	

}
