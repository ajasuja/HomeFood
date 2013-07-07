package hibernate.crud;

import index.impl.ElasticClientType;
import junit.framework.Assert;
import hibernate.crud.impl.FoodEntryCrudOperationsDBAndIndex;

import org.junit.Test;

import query.ElasticSearcher;
import query.Query;

import bos.FoodEntry;
import data.FoodEntryBuilder;

//TODO Tests not running properly if ran together, running seperately fine, looks like issue of threading in database
public class FoodEntryCrudOperationsDBAndIndexTest {

	private static final ElasticClientType CLIENT_TYPE = ElasticClientType.TEST;
	
	@Test
	public void testInsertOneFoodEntryIntoDBAndIndex() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithoutId();
		ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDBAndIndex(CLIENT_TYPE);
		crudOperations.insert(foodEntry);
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		Query searcher = new ElasticSearcher(CLIENT_TYPE);
		String foodIdToSearch = 1L + ""; 
		Assert.assertNotNull(searcher.getDocumentById(foodIdToSearch));
		searcher.closeNodeAfterTest();
	}
	
	@Test
	public void testInsertAndUpdateOneFoodEntryIntoDBAndIndex() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithId();				
		ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDBAndIndex(CLIENT_TYPE);
		crudOperations.insert(foodEntry);
		FoodEntry foodEntryAfterInsert = crudOperations.getPojo("FoodEntry", 1L);
		Query searcher = new ElasticSearcher(CLIENT_TYPE);
		String foodIdToSearch = 1L + ""; 
		Assert.assertNotNull(searcher.getDocumentById(foodIdToSearch));
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		Assert.assertEquals(foodEntry.getFoodName(), foodEntryAfterInsert.getFoodName());
		
		String updatedFoodName = "Shahi Paneer";
		foodEntry.setFoodName(updatedFoodName);
		Assert.assertEquals("Before Update it doesn't contain updated name"
				, false, searcher.getDocumentById(foodIdToSearch).contains(updatedFoodName));
		crudOperations.update(foodEntry);
		FoodEntry foodEntryAfterUpdate = crudOperations.getPojo("FoodEntry", 1L);
		Assert.assertNotNull(searcher.getDocumentById(foodIdToSearch));
		Assert.assertEquals("After update it must contain updated name"
				,true, searcher.getDocumentById(foodIdToSearch).contains(updatedFoodName));
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		Assert.assertEquals(updatedFoodName, foodEntryAfterUpdate.getFoodName());
		searcher.closeNodeAfterTest();
	}
	
	@Test
	public void testInsertAndDeleteOneFoodEntryFromDBAndIndex() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithoutId();
		ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDBAndIndex(CLIENT_TYPE);
		crudOperations.insert(foodEntry);
		Query searcher = new ElasticSearcher(CLIENT_TYPE);
		String foodIdToSearch = 1L + ""; 
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		Assert.assertNotNull(searcher.getDocumentById(foodIdToSearch));
		long toDeleteFoodId = 1L;
		crudOperations.delete(toDeleteFoodId);
		Assert.assertEquals(0, crudOperations.numberOfRows("FoodEntry"));
		Assert.assertNull(searcher.getDocumentById(foodIdToSearch));
		searcher.closeNodeAfterTest();
	}

}
