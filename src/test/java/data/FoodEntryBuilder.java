package data;

import java.util.Date;

import bos.CurrencyType;
import bos.FoodEntry;

public enum FoodEntryBuilder {

	SINGLETON;
	
	public long FOOD_ID = 1L;
	public long COOD_ID = 1001L;
	public String FOOD_NAME = "Paalak Paneer";
	public String FOOD_DESC = "Spinach with Paneer cooked in tomato/onion gravy " +
			"along with indian spices";
	public double PRICE_PER_PERSON = 170.00;
	public int NUM_OF_PERSONS = 10;
		
	public FoodEntry buildDefaultFoodEntryWithId() {
		
		FoodEntry foodEntry = new FoodEntry();
		foodEntry.setFoodId(FOOD_ID);
		foodEntry.setCookId(COOD_ID);
		foodEntry.setFoodName(FOOD_NAME);
		foodEntry.setFoodDescription(FOOD_DESC);
		foodEntry.setPricePerPerson(PRICE_PER_PERSON);
		foodEntry.setNumOfPersons(NUM_OF_PERSONS);
		foodEntry.setCurrencyType(CurrencyType.US_DOLLAR);
		foodEntry.setEntryCreatedTime(new Date());
		foodEntry.setOrderTimeDeadline(new Date());
		foodEntry.setChangeTime(new Date());
		return foodEntry;
	}
	
	public FoodEntry buildDefaultFoodEntryWithoutId() {
		
		FoodEntry foodEntry = new FoodEntry();
		foodEntry.setCookId(COOD_ID);
		foodEntry.setFoodName(FOOD_NAME);
		foodEntry.setFoodDescription(FOOD_DESC);
		foodEntry.setPricePerPerson(PRICE_PER_PERSON);
		foodEntry.setNumOfPersons(NUM_OF_PERSONS);
		foodEntry.setCurrencyType(CurrencyType.US_DOLLAR);
		foodEntry.setEntryCreatedTime(new Date());
		foodEntry.setOrderTimeDeadline(new Date());
		foodEntry.setChangeTime(new Date());
		return foodEntry;
	}

}
