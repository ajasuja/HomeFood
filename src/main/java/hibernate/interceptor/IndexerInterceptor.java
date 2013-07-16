package hibernate.interceptor;

import hibernate.crud.ICrudOperations;
import hibernate.crud.impl.FoodEntryCrudOperationsDBAndIndex;
import hibernate.crud.impl.FoodEntryDeadCrudOperationsDB;
import index.Indexer;
import index.impl.ElasticIndexer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import bos.FoodEntry;
import bos.FoodEntryDead;

public class IndexerInterceptor extends EmptyInterceptor implements Interceptor {

	private static final long serialVersionUID = -1390085534383913782L;

	/**
	 * Set of all {@link FoodEntry} which got inserted in this session.
	 */
	private Set<FoodEntry> insertedFoodEntrySet = new HashSet<FoodEntry>();
	/**
	 * Set of all {@link FoodEntry} which got updated in this session
	 */
	private Set<FoodEntry> updatedFoodEntrySet = new HashSet<FoodEntry>();
	/**
	 * Set of all {@link FoodEntry} which got deleted in this session.
	 */
	private Set<FoodEntry> deletedFoodEntrySet = new HashSet<FoodEntry>();
	
	/**
	 * Set of all entries which reached the maximum number of orders available
	 * @see FoodEntry#getNumOfPersons() 
	 */
	private Set<FoodEntry> noOrderLeftFoodEntrySet = new HashSet<FoodEntry>();
 
	private FoodEntryDead adaptFoodEntryLiveToDead(FoodEntry foodEntry) {
		if (foodEntry == null) {
			System.out.println(this.getClass().getSimpleName() + " >>> Food Entry is null");
		}
		FoodEntryDead foodEntryDead = new FoodEntryDead();
		foodEntryDead.setFoodId(foodEntry.getFoodId());
		foodEntryDead.setCookId(foodEntry.getCookId());
		foodEntryDead.setFoodName(foodEntry.getFoodName());
		foodEntryDead.setFoodDescription(foodEntry.getFoodDescription());
		foodEntryDead.setNumOfPersons(foodEntry.getNumOfPersons());
		foodEntryDead.setPricePerPerson(foodEntry.getPricePerPerson());
		foodEntryDead.setCurrencyType(foodEntry.getCurrencyType());
		foodEntryDead.setEntryCreatedTime(foodEntry.getEntryCreatedTime());
		foodEntryDead.setOrderTimeDeadline(foodEntry.getOrderTimeDeadline());
		foodEntryDead.setChangeTime(foodEntry.getChangeTime());
		return foodEntryDead;
	}

	/**
	 * Called before an object is deleted.<br><br>
	 * 
	 * 1. Updates the set of deleted Food Entries {@link #deletedFoodEntrySet} in this session.<br> 
	 * 
	 * @see org.hibernate.EmptyInterceptor#onDelete(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state
			, String[] propertyNames, Type[] types) {
		
		if (entity instanceof FoodEntry) {
			FoodEntry foodEntry = (FoodEntry) entity;
			deletedFoodEntrySet.add(foodEntry);
		}
	   }

	/**
	 * 
	 * 1. Updates the set of updated Food Entries {@link #updatedFoodEntrySet} in this session.<br> 
	 * 
	 * @see org.hibernate.EmptyInterceptor#onFlushDirty(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override	
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState
			   , Object[] previousState, String[] propertyNames, Type[] types) {
	       
		   if ( entity instanceof FoodEntry ) {
		       	FoodEntry foodEntry = (FoodEntry) entity;
		       	updatedFoodEntrySet.add(foodEntry);
				return true; 
	       }
	       return false;
	   }

	 /**
	  * Called before an object is saved. The interceptor may modify the state.<br><br>
	  * 
	  *  1. Updates the set of inserted Food Entries {@link #insertedFoodEntrySet} in this session.<br> 
	  *  
	  * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object, java.io.Serializable, java.lang.Object[], java.lang.String[], org.hibernate.type.Type[])
	  */
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state
			, String[] propertyNames, Type[] types) {
		
	       if ( entity instanceof FoodEntry ) {
	    	   FoodEntry foodEntry = (FoodEntry) entity;
	    	   insertedFoodEntrySet.add(foodEntry);
	    	   return true; 
	       }
	       return false;
	   }

	 /**
	  * Flushes in-memory state into database.<br><br>
	  * 
	  * 1. After flushing state, it send the entry to index corresponding to insert/update/delete.<br>
	  * 
	  * @see org.hibernate.EmptyInterceptor#postFlush(java.util.Iterator)
	  */
	@Override
	public void postFlush(@SuppressWarnings("rawtypes") Iterator iterator) {
	  	try{
			for (FoodEntry foodEntry : insertedFoodEntrySet) {
				Indexer indexer = new ElasticIndexer();
				indexer.addFoodEntryToIndex(foodEntry);
			}	
	 
			for (FoodEntry foodEntry : updatedFoodEntrySet) {
				if (foodEntry.getNumOfPersons() == 0) {
					noOrderLeftFoodEntrySet.add(foodEntry);
				}
				Indexer indexer = new ElasticIndexer();
				indexer.addFoodEntryToIndex(foodEntry);				
			}	
	 
			for (FoodEntry foodEntry : deletedFoodEntrySet) {
				Indexer indexer = new ElasticIndexer();
				indexer.deleteFoodEntryFromIndex(foodEntry.getFoodId()+"");								
			} 
	  	}
			finally {
			insertedFoodEntrySet.clear();
			updatedFoodEntrySet.clear();
			deletedFoodEntrySet.clear();
		}
	}
	   
	/**
	 * Method to perform all the actions after the completion of transaction on {@link FoodEntry}.<br><br>
	 * 
	 * 1. Move all foodEntries captured in {@link #noOrderLeftFoodEntrySet} from Live to Dead.<br> 
	 *   
	 * @see org.hibernate.EmptyInterceptor#afterTransactionCompletion(org.hibernate.Transaction)
	 *  
	 */
	@Override
	public void afterTransactionCompletion(Transaction tx) {
		for (FoodEntry foodEntry : noOrderLeftFoodEntrySet) {
			ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDBAndIndex();
			crudOperations.delete(foodEntry.getFoodId());
			ICrudOperations<FoodEntryDead> crudOperationsForFoodEntryDead = new FoodEntryDeadCrudOperationsDB();
			crudOperationsForFoodEntryDead.insert(adaptFoodEntryLiveToDead(foodEntry));
			System.out.println("FoodEntry [" +foodEntry.getFoodId() + "] moved from live to dead");
		}
	}
 
}
