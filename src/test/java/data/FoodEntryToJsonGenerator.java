package data;

import org.junit.Test;


import serialize.impl.FoodEntryToJson;

import bos.FoodEntry;

public class FoodEntryToJsonGenerator {

	@Test
	public void generateDefaultFoodEntryJSON() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithId();		
		FoodEntryToJson foodEntryToJson = new FoodEntryToJson();
		System.out.println(foodEntryToJson.getJsonFromPojo(foodEntry));
	}
}
