package hibernate.interceptor;

import index.Indexer;
import index.impl.ElasticIndexer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

import bos.FoodEntry;

public class IndexerInterceptor extends EmptyInterceptor implements Interceptor{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1390085534383913782L;

	private Set<FoodEntry> inserts = new HashSet<FoodEntry>();
	private Set<FoodEntry> updates = new HashSet<FoodEntry>();
	private Set<FoodEntry> deletes = new HashSet<FoodEntry>();
 
	
	public void onDelete(Object entity,
	                     Serializable id,
	                     Object[] state,
	                     String[] propertyNames,
	                     Type[] types) {
		if (entity instanceof FoodEntry) {
			FoodEntry foodEntry = (FoodEntry) entity;
			System.out.println("Delete Operation : " + foodEntry.getFoodId());
			deletes.add(foodEntry);
		}
	   }

	   // This method is called when FoodEntry object gets updated.
	   public boolean onFlushDirty(Object entity,
	                     Serializable id,
	                     Object[] currentState,
	                     Object[] previousState,
	                     String[] propertyNames,
	                     Type[] types) {
	       if ( entity instanceof FoodEntry ) {
		       	FoodEntry foodEntry = (FoodEntry) entity;
		       	System.out.println("Update Operation" + foodEntry.getFoodId());
		       	updates.add(foodEntry);
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
	       if ( entity instanceof FoodEntry ) {
	    	   FoodEntry foodEntry = (FoodEntry) entity;
	    	   System.out.println(this.getClass().getSimpleName() +">>>>"+"Create/Insert Operation : " + foodEntry.getFoodId());
	    	   inserts.add(foodEntry);
	    	   return true; 
	       }
	       return false;
	   }
	   //called before commit into database
	   public void preFlush(Iterator iterator) {
		   System.out.println(this.getClass().getSimpleName() + " >>>> Nothing is done in preflush operation");
	   }
	   //called after committed into database
	   public void postFlush(Iterator iterator) {
	      System.out.println(this.getClass().getSimpleName() + " >>>>>> postFlush");
	  	try{
			for (FoodEntry foodEntry : inserts) {
				Indexer indexer = new ElasticIndexer();
				indexer.addFoodEntryToIndex(foodEntry);
			    System.out.println("postFlush Insert is taken care for food id : " + foodEntry.getFoodId());	
			}	
	 
			for (FoodEntry foodEntry : updates) {
				Indexer indexer = new ElasticIndexer();
				indexer.addFoodEntryToIndex(foodEntry);				
			    System.out.println("postFlush Update is taken care for food id : " + foodEntry.getFoodId());	
			}	
	 
			for (FoodEntry foodEntry : deletes) {
				Indexer indexer = new ElasticIndexer();
				indexer.deleteFoodEntryFromIndex(foodEntry.getFoodId()+"");								
			    System.out.println("postFlush Delete is taken care for food id : " + foodEntry.getFoodId());	
			} 
	  	}
			finally {
			inserts.clear();
			updates.clear();
			deletes.clear();
		}
	  	}
}
