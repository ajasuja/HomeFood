package hibernate.crud;

import junit.framework.Assert;
import hibernate.crud.impl.FoodEntryCrudOperationsDBAndIndex;
import hibernate.init.HibernateUtil;

import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import query.ElasticSearcher;
import query.Query;

import bos.FoodEntry;
import data.FoodEntryBuilder;

//TODO If tests are run together, then they overlap each other due to sleep in individual test
public class FoodEntryCrudOperationsDBAndIndexTest {

	private static SessionFactory sessionFactory;
	private static String ENVIRONMENT = "env";
	private static String ENVIRONMENT_VALUE = "dev";

	@BeforeClass
	public static void setup() {
		System.setProperty(ENVIRONMENT, ENVIRONMENT_VALUE);
		sessionFactory = HibernateUtil.SINGLETON.getSessionFactory();		
	}

	@AfterClass
	public static void teardown() {
		sessionFactory.close();
	}

	
	@Test
	public void testInsertOneFoodEntryIntoDBAndIndex() throws InterruptedException {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithoutId();
		ICrudOperations<FoodEntry> crudOperationsForFoodEntry = new FoodEntryCrudOperationsDBAndIndex();
		crudOperationsForFoodEntry.insert(foodEntry);
		Assert.assertEquals(1, crudOperationsForFoodEntry.numberOfRows("FoodEntry"));
		Query searcher = new ElasticSearcher();
		String foodIdToSearch = 1L + ""; 
		Assert.assertNotNull(searcher.getDocumentById(foodIdToSearch));
		searcher.closeNodeAfterTest();
		Thread.sleep(FoodEntryBuilder.SINGLETON.ORDER_VALID_TIME_FROM_CURRENT_IN_MILLIS);
		Assert.assertEquals("After Order Deadline Time is crossed", 0, crudOperationsForFoodEntry.numberOfRows("FoodEntry"));
		Assert.assertNull("After Order Deadline Time is crossed",searcher.getDocumentById(foodIdToSearch));

		ICrudOperations<FoodEntry> crudOperationsForFoodEntryDead = new FoodEntryCrudOperationsDBAndIndex();
		Assert.assertEquals("One Entry in FoodEntryDead Table", 1, crudOperationsForFoodEntryDead.numberOfRows("FoodEntryDead"));

//		crudOperationsForFoodEntry.deleteAllRows("FoodEntry");
//		Assert.assertEquals(0, crudOperationsForFoodEntry.numberOfRows("FoodEntry"));
//		crudOperationsForFoodEntryDead.deleteAllRows("FoodEntryDead");
//		Assert.assertEquals(0, crudOperationsForFoodEntryDead.numberOfRows("FoodEntryDead"));
	}
	
	@Test
	public void testInsertAndUpdateOneFoodEntryIntoDBAndIndex() throws InterruptedException {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithId();				
		ICrudOperations<FoodEntry> crudOperationsForFoodEntry = new FoodEntryCrudOperationsDBAndIndex();
		long foodIdInserted = crudOperationsForFoodEntry.insert(foodEntry);
		FoodEntry foodEntryAfterInsert = crudOperationsForFoodEntry.getPojo("FoodEntry", foodIdInserted);
		Query searcher = new ElasticSearcher();
		String foodIdToSearch = foodIdInserted + ""; 
		Assert.assertNotNull(searcher.getDocumentById(foodIdToSearch));
		Assert.assertEquals(1, crudOperationsForFoodEntry.numberOfRows("FoodEntry"));
		Assert.assertEquals(foodEntry.getFoodName(), foodEntryAfterInsert.getFoodName());
		
		String updatedFoodName = "Shahi Paneer";
		foodEntry.setFoodName(updatedFoodName);
		Assert.assertEquals("Before Update it doesn't contain updated name"
				, false, searcher.getDocumentById(foodIdToSearch).contains(updatedFoodName));
		crudOperationsForFoodEntry.update(foodEntry);
		FoodEntry foodEntryAfterUpdate = crudOperationsForFoodEntry.getPojo("FoodEntry", foodIdInserted);
		Assert.assertNotNull(searcher.getDocumentById(foodIdToSearch));
		Assert.assertEquals("After update it must contain updated name"
				,true, searcher.getDocumentById(foodIdToSearch).contains(updatedFoodName));
		Assert.assertEquals(1, crudOperationsForFoodEntry.numberOfRows("FoodEntry"));
		Assert.assertEquals(updatedFoodName, foodEntryAfterUpdate.getFoodName());
		searcher.closeNodeAfterTest();
		Thread.sleep(FoodEntryBuilder.SINGLETON.ORDER_VALID_TIME_FROM_CURRENT_IN_MILLIS);
		Assert.assertEquals("After Order Deadline Time is crossed", 0, crudOperationsForFoodEntry.numberOfRows("FoodEntry"));
		Assert.assertNull("After Order Deadline Time is crossed",searcher.getDocumentById(foodIdToSearch));

		ICrudOperations<FoodEntry> crudOperationsForFoodEntryDead = new FoodEntryCrudOperationsDBAndIndex();
		Assert.assertEquals("One Entry in FoodEntryDead Table", 1, crudOperationsForFoodEntryDead.numberOfRows("FoodEntryDead"));

		crudOperationsForFoodEntry.deleteAllRows("FoodEntry");
		Assert.assertEquals(0, crudOperationsForFoodEntry.numberOfRows("FoodEntry"));
		crudOperationsForFoodEntryDead.deleteAllRows("FoodEntryDead");
		Assert.assertEquals(0, crudOperationsForFoodEntryDead.numberOfRows("FoodEntryDead"));

	}
	
	@Test
	public void testInsertAndDeleteOneFoodEntryFromDBAndIndex() throws InterruptedException {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithoutId();
		ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDBAndIndex();
		long foodIdInserted = crudOperations.insert(foodEntry);
		Query searcher = new ElasticSearcher();
		String foodIdToSearch = foodIdInserted + ""; 
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		Assert.assertNotNull(searcher.getDocumentById(foodIdToSearch));
		long toDeleteFoodId = foodIdInserted;
		crudOperations.delete(toDeleteFoodId);
		Assert.assertEquals(0, crudOperations.numberOfRows("FoodEntry"));
		Assert.assertNull("After delete from index no element should be on search result"
				, searcher.getDocumentById(foodIdToSearch));
		searcher.closeNodeAfterTest();
		Thread.sleep(FoodEntryBuilder.SINGLETON.ORDER_VALID_TIME_FROM_CURRENT_IN_MILLIS);
		
		crudOperations.deleteAllRows("FoodEntry");
		Assert.assertEquals(0, crudOperations.numberOfRows("FoodEntry"));
	}

}
