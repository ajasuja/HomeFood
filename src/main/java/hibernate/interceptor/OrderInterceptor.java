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
//	private SessionFactory sessionFactory;
	
	private Set<Order> inserts = new HashSet<Order>();
	private Set<Order> updates = new HashSet<Order>();
	//TODO need to have hashcode & equals in Order class
	private Map<Order, OrderStateDiff> orderUpdates = new HashMap<Order, OrderStateDiff>();
	private Set<Order> deletes = new HashSet<Order>();
 
	
	public OrderInterceptor() {
	}
	
//	public OrderInterceptor(SessionFactory sessionFactory) {
//		this.sessionFactory = sessionFactory;
//	}
	
	public void onDelete(Object entity,
	                     Serializable id,
	                     Object[] state,
	                     String[] propertyNames,
	                     Type[] types) {
		if (entity instanceof Order) {
			Order order = (Order) entity;
			System.out.println("Delete Operation : " + order.getOrderId());
			deletes.add(order);
		}
	   }

	   // This method is called when FoodEntry object gets updated.
	   public boolean onFlushDirty(Object entity,
	                     Serializable id,
	                     Object[] currentState,
	                     Object[] previousState,
	                     String[] propertyNames,
	                     Type[] types) {
	       if ( entity instanceof Order ) {
	    	   	Order currentOrder = (Order) entity;
		       	System.out.println("Update Operation >>>> " + currentOrder.getOrderId());
		       	ICrudOperations<Order> crudOperationsForOrder = new OrderCrudOperationsDB();
		       	Order previousOrder = crudOperationsForOrder.getPojo("Order", currentOrder.getOrderId());
		       	int updatedNumberOfPersons = (currentOrder.getNumOfPersons() - previousOrder.getNumOfPersons());
		       	OrderStateDiff orderStateDiff = new OrderStateDiff();
		       	orderStateDiff.setUpdatedNumberOfPersons(updatedNumberOfPersons);
		       	orderUpdates.put(currentOrder, orderStateDiff);
		       	System.out.println("Number of Persons Changed : " + (currentOrder.getNumOfPersons() - previousOrder.getNumOfPersons()));
		       	updates.add(currentOrder);
				return true; 
	       }
	       return false;
	   }
	   public boolean onLoad(Object entity,
	                    Serializable id,
	                    Object[] state,
	                    String[] propertyNames,
	                    Type[] types) {
	       // do nothing
	       return true;
	   }
	   // This method is called when FoodEntry object gets created.
	   public boolean onSave(Object entity,
	                    Serializable id,
	                    Object[] state,
	                    String[] propertyNames,
	                    Type[] types) {
	       if ( entity instanceof Order ) {
	    	   Order order = (Order) entity;
	    	   System.out.println(this.getClass().getSimpleName() + " >>>> Just checking the caller is Order before saving");
	    	   inserts.add(order);
	    	   return true; 
	       }
	       return false;
	   }
	   //called before commit into database
	   @Override
	   public void preFlush(Iterator iterator) {
		   System.out.println(this.getClass().getSimpleName() + " >>>> Nothing is done in preflush stage");
	   }
	   //called after committed into database
	   @Override
	   public void postFlush(Iterator iterator) {
	      System.out.println(this.getClass().getSimpleName() + " >>>>> Before postFlush Operation");
	  	try{
			for(Order order : inserts) {
				long foodIdToOrder = order.getFoodId();
				ICrudOperations<FoodEntry> crudOperationsForFoodEntry = new FoodEntryCrudOperationsDBAndIndex();
				FoodEntry foodEntryOrdered = crudOperationsForFoodEntry.getPojo("FoodEntry", foodIdToOrder);
				int currentNumOfPersons = foodEntryOrdered.getNumOfPersons();
				if (currentNumOfPersons < order.getNumOfPersons()) {
					System.out.println("Food [" + order.getFoodId() + "] is left for only " + currentNumOfPersons +" persons");
					continue;
				}
				foodEntryOrdered.setNumOfPersons(currentNumOfPersons - order.getNumOfPersons());
				inserts.clear();
				crudOperationsForFoodEntry.update(foodEntryOrdered);
			    System.out.println("postFlush Insert is taken care for order id" + order.getOrderId());	
			}	
	 
//			for (Iterator it = updates.iterator(); it.hasNext();) {
//				
//				Order order = (Order)it.next();
//				updates.clear();
//			    System.out.println("postFlush - update");
//			}	
			
			for (Map.Entry<Order, OrderStateDiff> entry : orderUpdates.entrySet()) {
				Order order = entry.getKey();
				OrderStateDiff orderStateDiff = entry.getValue();
				long foodIdToOrder = order.getFoodId();
				ICrudOperations<FoodEntry> crudOperationsForFoodEntry = new FoodEntryCrudOperationsDBAndIndex();
				FoodEntry foodEntryOrdered = crudOperationsForFoodEntry.getPojo("FoodEntry", foodIdToOrder);
				int currentNumOfPersons = foodEntryOrdered.getNumOfPersons();
				foodEntryOrdered.setNumOfPersons(currentNumOfPersons - orderStateDiff.getUpdatedNumberOfPersons());
				crudOperationsForFoodEntry.update(foodEntryOrdered);
			    System.out.println("postFlush Update is taken care for order id" + order.getOrderId());	
			}
	 
//			for (Iterator it = deletes.iterator(); it.hasNext();) {
//				Order order = (Order)it.next();
//			    System.out.println("postFlush - delete");
//			} 
	  	}
			finally {
			inserts.clear();
			updates.clear();
			deletes.clear();
		}
	  	}

}
