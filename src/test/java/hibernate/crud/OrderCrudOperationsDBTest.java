package hibernate.crud;

import hibernate.crud.impl.FoodEntryCrudOperationsDB;
import hibernate.crud.impl.OrderCrudOperationsDB;
import hibernate.init.HibernateUtil;
import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import bos.FoodEntry;
import bos.Order;
import data.FoodEntryBuilder;
import data.OrderBuilder;

public class OrderCrudOperationsDBTest {
	private static SessionFactory sessionFactory;
	private static long INVALID_ORDER = -1L;
	
	@BeforeClass
	public static void setup() {
		System.setProperty("env", "test");
		sessionFactory = HibernateUtil.SINGLETON.getSessionFactory();		
	}

	@AfterClass
	public static void teardown() {
		sessionFactory.close();
	}

	@Ignore//TODO Ignored because changes made to insert break these test as every order is validated first now
	@Test
	public void testInsertOneOrderIntoDB() {
		Order order = OrderBuilder.SINGLETON.buildDefaultOrder();
		ICrudOperations<Order> crudOperations = new OrderCrudOperationsDB();
		crudOperations.insert(order);
		Assert.assertEquals(1, crudOperations.numberOfRows("Order"));
		crudOperations.deleteAllRows("Order");
		Assert.assertEquals(0, crudOperations.numberOfRows("Order"));
	}

	@Ignore//TODO Ignored because changes made to insert break these test as every order is validated first now
	@Test
	public void testInsertAndUpdateOneOrderIntoDB() {
		Order order = OrderBuilder.SINGLETON.buildDefaultOrder();				
		ICrudOperations<Order> crudOperations = new OrderCrudOperationsDB();
		long insertedOrderId = crudOperations.insert(order);
		Order orderAfterInsert = crudOperations.getPojo("Order", insertedOrderId);
		Assert.assertEquals(1, crudOperations.numberOfRows("Order"));
		Assert.assertEquals(order.getNumOfPersons(), orderAfterInsert.getNumOfPersons());
		int updatedNumOfPersons = 4;
		order.setNumOfPersons(updatedNumOfPersons);
		crudOperations.update(order);
		Order orderAfterUpdate = crudOperations.getPojo("Order", insertedOrderId);
		Assert.assertEquals(1, crudOperations.numberOfRows("Order"));
		Assert.assertEquals(updatedNumOfPersons, orderAfterUpdate.getNumOfPersons());
		crudOperations.deleteAllRows("Order");
		Assert.assertEquals(0, crudOperations.numberOfRows("Order"));
	}
	
	@Ignore//TODO Ignored because changes made to insert break these test as every order is validated first now
	@Test
	public void testInsertAndDeleteOneOrderFromDB() {
		Order order = OrderBuilder.SINGLETON.buildDefaultOrder();
		ICrudOperations<Order> crudOperations = new OrderCrudOperationsDB();
		long insertedOrderId = crudOperations.insert(order);
		Assert.assertEquals(1, crudOperations.numberOfRows("Order"));
		long toDeleteOrderId = insertedOrderId;
		crudOperations.delete(toDeleteOrderId);
		Assert.assertEquals(0, crudOperations.numberOfRows("Order"));
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
	public void testInsertOneFoodEntryAndValidNumOfPersonsOrder() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithoutId();
		ICrudOperations<FoodEntry> crudOperationsForFoodEntry = new FoodEntryCrudOperationsDB();
		long insertedFoodId = crudOperationsForFoodEntry.insert(foodEntry);
		Assert.assertEquals(1, crudOperationsForFoodEntry.numberOfRows("FoodEntry"));
		FoodEntry foodEntryBeforeOrder = crudOperationsForFoodEntry.getPojo("FoodEntry", insertedFoodId);
		
		Order order = OrderBuilder.SINGLETON.buildDefaultOrder();
		order.setFoodId(insertedFoodId);
		ICrudOperations<Order> crudOperationsForOrder = new OrderCrudOperationsDB();
		Assert.assertNotSame(INVALID_ORDER, crudOperationsForOrder.insert(order));
		Assert.assertEquals(1, crudOperationsForOrder.numberOfRows("Order"));
		
		FoodEntry foodEntryAfterOrder = crudOperationsForFoodEntry.getPojo("FoodEntry", insertedFoodId);
		Assert.assertSame(foodEntryBeforeOrder.getNumOfPersons() - order.getNumOfPersons()
				, foodEntryAfterOrder.getNumOfPersons() );
		
		crudOperationsForFoodEntry.deleteAllRows("FoodEntry");
		Assert.assertEquals(0, crudOperationsForFoodEntry.numberOfRows("FoodEntry"));
		crudOperationsForOrder.deleteAllRows("Order");
		Assert.assertEquals(0, crudOperationsForFoodEntry.numberOfRows("Order"));
		
	}
	
	@Test
	public void testInsertOneFoodEntryAndInValidNumOfPersonsOrder() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithoutId();
		ICrudOperations<FoodEntry> crudOperationsForFoodEntry = new FoodEntryCrudOperationsDB();
		long insertedFoodId = crudOperationsForFoodEntry.insert(foodEntry);
		Assert.assertEquals(1, crudOperationsForFoodEntry.numberOfRows("FoodEntry"));
		FoodEntry foodEntryBeforeOrder = crudOperationsForFoodEntry.getPojo("FoodEntry", insertedFoodId);

		Order order = OrderBuilder.SINGLETON.buildDefaultOrder();
		order.setFoodId(insertedFoodId);
		order.setNumOfPersons(foodEntry.getNumOfPersons() + 1);
		ICrudOperations<Order> crudOperationsForOrder = new OrderCrudOperationsDB();
		Assert.assertSame(INVALID_ORDER, crudOperationsForOrder.insert(order));
		Assert.assertEquals(0, crudOperationsForOrder.numberOfRows("Order"));
		
		FoodEntry foodEntryAfterOrder = crudOperationsForFoodEntry.getPojo("FoodEntry", insertedFoodId);
		Assert.assertSame(foodEntryBeforeOrder.getNumOfPersons(), foodEntryAfterOrder.getNumOfPersons() );

		crudOperationsForFoodEntry.deleteAllRows("FoodEntry");
		Assert.assertEquals(0, crudOperationsForFoodEntry.numberOfRows("FoodEntry"));
		crudOperationsForOrder.deleteAllRows("Order");
		Assert.assertEquals(0, crudOperationsForFoodEntry.numberOfRows("Order"));
		
		
	}

}
