package hibernate.interceptor;

import hibernate.crud.ICrudOperations;
import hibernate.crud.OrderValidity;
import hibernate.crud.impl.FoodEntryCrudOperationsDB;
import index.impl.ElasticClientType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;

import bos.FoodEntry;
import bos.Order;

public class OrderInterceptor extends EmptyInterceptor implements Interceptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5701739348436080650L;
	private Object entity;
//	private SessionFactory sessionFactory;
	
	private Set<Order> inserts = new HashSet<Order>();
	private Set<Order> updates = new HashSet<Order>();
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
	    	   Order order = (Order) entity;
		       	System.out.println("Update Operation" + order.getOrderId());
		       	updates.add(order);
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
		   this.entity = entity;
	       if ( entity instanceof Order ) {
	    	   Order order = (Order) entity;
	    	   System.out.println(this.getClass().getSimpleName() +">>>>"+"Create/Insert Operation : " + order.getOrderId());
	    	   inserts.add(order);
	    	   return true; 
	       }
	       return false;
	   }
	   //called before commit into database
	   public void preFlush(Iterator iterator) {
		   System.out.println("Preflush");
			for (Iterator it = inserts.iterator(); it.hasNext();) {
				/*CHECK IF ORDER IS VALID*/
				Order order = (Order)it.next();
				if (!OrderValidity.isValidOrder(order)) {
					System.out.println(">>>>>>>Invalid Order Insert>>>>>>>>>>");
				}
			}	
	 
			for (Iterator it = updates.iterator(); it.hasNext();) {
				/*CHECK IF ORDER IS VALID*/
				Order order = (Order)it.next();
				if (!OrderValidity.isValidOrder(order)) {
					System.out.println(">>>>>>>Invalid Order Update>>>>>>>>>>");
				}
			}
	 
			for (Iterator it = deletes.iterator(); it.hasNext();) {
				Order order = (Order)it.next();
			    System.out.println("Preflush Stage : Before Deleting Order");
			} 

	   }
	   //called after committed into database
	   public void postFlush(Iterator iterator) {
	      System.out.println("postFlush");
	  	try{
			for (Iterator it = inserts.iterator(); it.hasNext();) {
				Order order = (Order)it.next();
				long foodIdToOrder = order.getFoodId();
				ICrudOperations<FoodEntry> crudOperationsForFoodEntry = new FoodEntryCrudOperationsDB();
				FoodEntry foodEntryOrdered = crudOperationsForFoodEntry.getPojo("FoodEntry", foodIdToOrder);
				int currentNumOfPersons = foodEntryOrdered.getNumOfPersons();
				if (currentNumOfPersons < order.getNumOfPersons()) {
					System.out.println("Food [" + order.getFoodId() + "] is left for only " + currentNumOfPersons +" persons");
					continue;
				}
				foodEntryOrdered.setNumOfPersons(currentNumOfPersons - order.getNumOfPersons());
				inserts.clear();
				crudOperationsForFoodEntry.update(foodEntryOrdered);
			    System.out.println("postFlush - insert");	
			}	
	 
			for (Iterator it = updates.iterator(); it.hasNext();) {
				Order order = (Order)it.next();
				long foodIdToOrder = order.getFoodId();
				ICrudOperations<FoodEntry> crudOperationsForFoodEntry = new FoodEntryCrudOperationsDB();
				FoodEntry foodEntryOrdered = crudOperationsForFoodEntry.getPojo("FoodEntry", foodIdToOrder);
				int currentNumOfPersons = foodEntryOrdered.getNumOfPersons();
				if (currentNumOfPersons < order.getNumOfPersons()) {
					System.out.println("Food [" + order.getFoodId() + "] is left for only " + currentNumOfPersons +" persons");
					continue;
				}
				foodEntryOrdered.setNumOfPersons(currentNumOfPersons - order.getNumOfPersons());
				crudOperationsForFoodEntry.update(foodEntryOrdered);
			    System.out.println("postFlush - update");
			}	
	 
			for (Iterator it = deletes.iterator(); it.hasNext();) {
				Order order = (Order)it.next();
			    System.out.println("postFlush - delete");
			} 
	  	}
			finally {
			inserts.clear();
			updates.clear();
			deletes.clear();
		}
	  	}

}
