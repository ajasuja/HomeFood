package hibernate.crud;

import hibernate.crud.impl.FoodEntryCrudOperationsDB;
import bos.FoodEntry;
import bos.Order;

public class OrderValidity {

	private static boolean isNumOfPersonValid(Order order) {
		long foodIdForOrder = order.getFoodId();
		ICrudOperations<FoodEntry> crudOperationsForFoodEntry = new FoodEntryCrudOperationsDB();
		FoodEntry foodEntryForOrder = crudOperationsForFoodEntry.getPojo("FoodEntry", foodIdForOrder);
		int foodLeftForPersons = foodEntryForOrder.getNumOfPersons();
		int foodOrderedForPersons = order.getNumOfPersons();
		if (foodLeftForPersons < foodOrderedForPersons) {
			System.out.println( "OrderValidity >>>>>>> " +
					"FoodId ["+ foodIdForOrder +"] is remaining for [" + foodLeftForPersons +"] Persons." +
					" You ordered for ["+foodOrderedForPersons+"] Persons.");
			return false;
		}
		return true;
	}
	public static boolean isValidOrder(Order order) {
		/**/
		return (isNumOfPersonValid(order));
	}
}
