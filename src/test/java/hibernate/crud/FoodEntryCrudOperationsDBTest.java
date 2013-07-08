package hibernate.crud;

import junit.framework.Assert;
import hibernate.crud.impl.FoodEntryCrudOperationsDB;
import hibernate.init.HibernateUtil;

import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import data.FoodEntryBuilder;

import bos.FoodEntry;

public class FoodEntryCrudOperationsDBTest {
	
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void setup() {
		System.setProperty("env", "test");
		sessionFactory = HibernateUtil.SINGLETON.getSessionFactory();		
	}

	@AfterClass
	public static void teardown() {
		sessionFactory.close();
	}
	
	@Test
	public void testInsertOneFoodEntryIntoDB() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithoutId();
		ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDB();
		crudOperations.insert(foodEntry);
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		crudOperations.deleteAllRows("FoodEntry");
		Assert.assertEquals(0, crudOperations.numberOfRows("FoodEntry"));
	}

	@Test
	public void testInsertTwoFoodEntryIntoDB() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithoutId();
		ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDB();
		crudOperations.insert(foodEntry);
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		crudOperations.insert(foodEntry);
		Assert.assertEquals(2, crudOperations.numberOfRows("FoodEntry"));
		crudOperations.deleteAllRows("FoodEntry");
		Assert.assertEquals(0, crudOperations.numberOfRows("FoodEntry"));
	}
	
	@Test
	public void testInsertAndUpdateOneFoodEntryIntoDB() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithId();				
		ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDB();
		long insertedFoodId = crudOperations.insert(foodEntry);
		FoodEntry foodEntryAfterInsert = crudOperations.getPojo("FoodEntry", insertedFoodId);
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		Assert.assertEquals(foodEntry.getFoodName(), foodEntryAfterInsert.getFoodName());
		String updatedFoodName = "Shahi Paneer";
		foodEntryAfterInsert.setFoodName(updatedFoodName);
		crudOperations.update(foodEntryAfterInsert);
		FoodEntry foodEntryAfterUpdate = crudOperations.getPojo("FoodEntry", insertedFoodId);
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		Assert.assertEquals(updatedFoodName, foodEntryAfterUpdate.getFoodName());
		crudOperations.deleteAllRows("FoodEntry");
		Assert.assertEquals(0, crudOperations.numberOfRows("FoodEntry"));
	}
	
	@Test
	public void testInsertAndDeleteOneFoodEntryFromDB() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithoutId();
		ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDB();
		long insertedFoodId = crudOperations.insert(foodEntry);
		Assert.assertEquals(1, crudOperations.numberOfRows("FoodEntry"));
		long toDeleteFoodId = insertedFoodId;
		crudOperations.delete(toDeleteFoodId);
		Assert.assertEquals(0, crudOperations.numberOfRows("FoodEntry"));
		crudOperations.deleteAllRows("FoodEntry");
		Assert.assertEquals(0, crudOperations.numberOfRows("FoodEntry"));
	}
}
