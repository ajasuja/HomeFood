package index;

import java.util.List;

import bos.FoodEntry;

public interface Indexer {

	public void addFoodEntryToIndex(final FoodEntry foodEntry);
	
	public void addFoodEntriesToIndex(final List<FoodEntry> foodEntries);
	
	public void deleteFoodEntryFromIndex(final String foodId);
	
	//TODO remove it from here
	public void closeNodeAfterTest();
}
