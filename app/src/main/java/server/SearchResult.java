package server;

import java.util.ArrayList;
import java.util.List;

import model.Food;

public class SearchResult extends Result {
	
	/* DATA MEMBERS */
	private SearchList list;

	public SearchResult() {
		list = new SearchList();
	}
	
	
	@Override
	public String toString() {
		return list.toString();
	}
	
	public SearchList getSearchList() {
		return this.list;
	}
	
	public SearchListItem[] getSearchListItems() {
		return this.getSearchList().getItems();
	}

	public ArrayList<Food> getResultAsFoods() {
		ArrayList<Food> foodList = new ArrayList<>();

		if (this.list.getItems() == null) {
			return foodList;
		}

		int size = this.list.getItems().length;

		for (int i = 0; i < size; i++) {
			SearchListItem item = this.list.getItems()[i];
			String name = item.getName();
			String id = item.getID();
			String foodGroupName = item.getGroup();

			foodList.add(new Food(name, id, foodGroupName));
		}

		return foodList;
	}
	
}
