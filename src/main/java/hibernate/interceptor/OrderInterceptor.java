package hibernate.interceptor;

import hibernate.crud.ICrudOperations;
import hibernate.crud.impl.FoodEntryCrudOperationsDBAndIndex;
import hibernate.crud.impl.OrderCrudOperationsDB;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

import bos.FoodEntry;
import bos.Order;
import bos.OrderStateDiff;

public class OrderInterceptor extends EmptyInterceptor implements Interceptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5701739348436080650L;
	
	/**
	 * Set of all {@link Order} which got inserted in this session.
	 */
	private Set<Order> insertedOrderSet = new HashSet<Order>();
	/**
	 * Set of all {@link Order} which got updated in this session.
	 */
	private Set<Order> updatedOrderSet = new HashSet<Order>();
	/**
	 * Set of all {@link Order} which got deleted in this session.
	 */
	private Set<Order> deletedOrderSet = new HashSet<Order>();

	//TODO need to have hashcode & equals in Order class
	/**
	 * Map of all {@link Order} to its {@link OrderStateDiff} which got updated in this session.
	 */
	private Map<Order, OrderStateDiff> orderToUpdateOrderStateDiffMap = new HashMap<Order, OrderStateDiff>();
 
	/**
	 * Called when on object is getting deleted.<br><br>
	 * 
	 * 1. Updates the set of deleted Orders {@link #deletedOrderSet} in this session.<br>
	 * 
	 * @see Order 
	 * @see org.hibernate.EmptyInterceptor#onDelete(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state
			, String[] propertyNames, Type[] types) {
		if (entity instanceof Order) {
			Order order = (Order) entity;
			deletedOrderSet.add(order);
		}
	   }

	/** 
	 * Called when an object is getting updated.<br><br>
	 * 
	 * 1. Updates the set of updated Orders {@link #updatedOrderSet} in this session.<br>
	 * 2. Updates the map of updated Orders to its Updated State Diff {@link #orderToUpdateOrderStateDiffMap} in this session.<br>
	 * 
	 * @see org.hibernate.EmptyInterceptor#onFlushDirty(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState
			, Object[] previousState, String[] propertyNames, Type[] types) {
	       if ( entity instanceof Order ) {
	    	   	Order currentOrder = (Order) entity;
		       	ICrudOperations<Order> crudOperationsForOrder = new OrderCrudOperationsDB();
		       	Order previousOrder = crudOperationsForOrder.getPojo("Order", currentOrder.getOrderId());
		       	int updatedNumberOfPersons = (currentOrder.getNumOfPersons() - previousOrder.getNumOfPersons());
		       	OrderStateDiff orderStateDiff = new OrderStateDiff();
		       	orderStateDiff.setUpdatedNumberOfPersons(updatedNumberOfPersons);
		       	orderToUpdateOrderStateDiffMap.put(currentOrder, orderStateDiff);
		       	updatedOrderSet.add(currentOrder);
				return true; 
	       }
	       return false;
	}

	/**
	 * Called before an object is saved.<br><br>
	 * 
	 * 1. Updates the set of inserted Orders {@link #insertedOrderSet} in this session.<br> 
	 * 
	 * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override	
	public boolean onSave(Object entity, Serializable id, Object[] state
			, String[] propertyNames, Type[] types) {
	       if ( entity instanceof Order ) {
	    	   Order order = (Order) entity;
	    	   insertedOrderSet.add(order);
	    	   return true; 
	       }
	       return false;
	}
	
	/**
	 * Flushes in-memory state into database.<br><br>
	 * 
	 * 1. For each new order inserted defined by set of {@link #insertedOrderSet},
	 * 	it updates the respective FoodEntry {@link FoodEntry#getNumOfPersons()}. <br>
	 * 2. For each updated order defined by map of {@link #orderToUpdateOrderStateDiffMap},
	 * 	it updates the respective FoodEntry {@link FoodEntry#getNumOfPersons()}. <br>
	 * 
	 * @see org.hibernate.EmptyInterceptor#postFlush(java.util.Iterator)
	 */
	@Override
	public void postFlush(@SuppressWarnings("rawtypes") Iterator iterator) {
	  	try{
			for(Order order : insertedOrderSet) {
				long foodIdToOrder = order.getFoodId();
				ICrudOperations<FoodEntry> crudOperationsForFoodEntry = new FoodEntryCrudOperationsDBAndIndex();
				FoodEntry foodEntryOrdered = crudOperationsForFoodEntry.getPojo("FoodEntry", foodIdToOrder);
				int currentNumOfPersons = foodEntryOrdered.getNumOfPersons();
//				if (currentNumOfPersons < order.getNumOfPersons()) {
//					System.out.println("Food [" + order.getFoodId() + "] is left for only " + currentNumOfPersons +" persons");
//					continue;
//				}
				foodEntryOrdered.setNumOfPersons(currentNumOfPersons - order.getNumOfPersons());
				insertedOrderSet.clear();
				crudOperationsForFoodEntry.update(foodEntryOrdered);
			}	
	 
			for (Map.Entry<Order, OrderStateDiff> entry : orderToUpdateOrderStateDiffMap.entrySet()) {
				Order order = entry.getKey();
				OrderStateDiff orderStateDiff = entry.getValue();
				long foodIdToOrder = order.getFoodId();
				ICrudOperations<FoodEntry> crudOperationsForFoodEntry = new FoodEntryCrudOperationsDBAndIndex();
				FoodEntry foodEntryOrdered = crudOperationsForFoodEntry.getPojo("FoodEntry", foodIdToOrder);
				//TODO NULL CHECKING
				int currentNumOfPersons = foodEntryOrdered.getNumOfPersons();
				foodEntryOrdered.setNumOfPersons(currentNumOfPersons - orderStateDiff.getUpdatedNumberOfPersons());
				crudOperationsForFoodEntry.update(foodEntryOrdered);
			}
	  	}
			finally {
			insertedOrderSet.clear();
			updatedOrderSet.clear();
			deletedOrderSet.clear();
			orderToUpdateOrderStateDiffMap.clear();
			}
	  	}

}
