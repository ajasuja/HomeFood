package hibernate.crud;

import index.impl.ElasticClientType;
import junit.framework.Assert;
import hibernate.crud.impl.FoodEntryCrudOperationsDB;

import org.junit.Test;

import data.FoodEntryBuilder;

import bos.FoodEntry;

//TODO Tests not running properly if ran together, running seperately fine, looks like issue of threading	
public class FoodEntryCrudOperationsDBTest {

	private static final ElasticClientType CLIENT_TYPE = ElasticClientType.TEST;

	@Test
	public void testInsertOneFoodEntryIntoDB() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithoutId();
		ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDB(CLIENT_TYPE);
		crudOperations.insert(foodEntry);
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
	}
	
	@Test
	public void testInsertAndUpdateOneFoodEntryIntoDB() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithId();				
		ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDB(CLIENT_TYPE);
		crudOperations.insert(foodEntry);
		FoodEntry foodEntryAfterInsert = crudOperations.getPojo("FoodEntry", 1L);
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		Assert.assertEquals(foodEntry.getFoodName(), foodEntryAfterInsert.getFoodName());
		String updatedFoodName = "Shahi Paneer";
		foodEntry.setFoodName(updatedFoodName);
		crudOperations.update(foodEntry);
		FoodEntry foodEntryAfterUpdate = crudOperations.getPojo("FoodEntry", 1L);
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		Assert.assertEquals(updatedFoodName, foodEntryAfterUpdate.getFoodName());
	}
	
	@Test
	public void testInsertAndDeleteOneFoodEntryFromDB() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithoutId();
		ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDB(CLIENT_TYPE);
		crudOperations.insert(foodEntry);
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		long toDeleteFoodId = 1L;
		crudOperations.delete(toDeleteFoodId);
		Assert.assertEquals(0, crudOperations.numberOfRows("FoodEntry"));
	}
}
