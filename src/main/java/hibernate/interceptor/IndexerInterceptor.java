package hibernate.interceptor;

import index.Indexer;
import index.impl.ElasticClientType;
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

	private ElasticClientType clientType;
	private Object entity;
	
	private Set<FoodEntry> inserts = new HashSet<FoodEntry>();
	private Set<FoodEntry> updates = new HashSet<FoodEntry>();
	private Set<FoodEntry> deletes = new HashSet<FoodEntry>();
 
	
	public IndexerInterceptor(ElasticClientType clientType) {
		this.clientType = clientType;
	}
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
		   this.entity = entity;
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
		   System.out.println("Preflush");
		   if(entity instanceof FoodEntry) {
			   FoodEntry foodEntry = (FoodEntry) entity;
			   System.out.println("Preflush : " + foodEntry.getFoodId());
		   }
	   }
	   //called after committed into database
	   public void postFlush(Iterator iterator) {
	      System.out.println("postFlush");
	  	try{
			for (Iterator it = inserts.iterator(); it.hasNext();) {
				FoodEntry foodEntry = (FoodEntry)it.next();
				Indexer indexer = new ElasticIndexer(clientType);
				indexer.addFoodEntryToIndex(foodEntry);
			    System.out.println("postFlush - insert");	
			}	
	 
			for (Iterator it = updates.iterator(); it.hasNext();) {
				FoodEntry foodEntry = (FoodEntry)it.next();
				Indexer indexer = new ElasticIndexer(clientType);
				indexer.addFoodEntryToIndex(foodEntry);				
			    System.out.println("postFlush - update");
			}	
	 
			for (Iterator it = deletes.iterator(); it.hasNext();) {
				FoodEntry foodEntry = (FoodEntry) it.next();
				Indexer indexer = new ElasticIndexer(clientType);
				indexer.deleteFoodEntryFromIndex(foodEntry.getFoodId()+"");								
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
