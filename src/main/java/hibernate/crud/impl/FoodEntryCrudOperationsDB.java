package hibernate.crud.impl;

import java.util.List;

import hibernate.crud.ICrudOperations;
import hibernate.init.HibernateUtil;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import bos.FoodEntry;

public class FoodEntryCrudOperationsDB implements ICrudOperations<FoodEntry> {
	
	private SessionFactory sessionFactory = HibernateUtil.SINGLETON.getSessionFactory();
	
	public long insert(FoodEntry foodEntry) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();		
		session.save(foodEntry);
		session.flush();
		long insertedFoodId = foodEntry.getFoodId();
		transaction.commit();		
		session.close();
		return insertedFoodId;
	}

	public void update(FoodEntry foodEntry) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();		
		session.update(foodEntry);
		transaction.commit();
		session.close();
	}

	public void delete(long foodId) {
		Session session = sessionFactory.openSession();
		Transaction  transaction = session.beginTransaction();
		FoodEntry foodEntry = (FoodEntry) session.get(FoodEntry.class, foodId);
		session.delete(foodEntry);
		transaction.commit();
		session.close();
	}
	
//TODO To move these read functions one level up for common use	
	public void read(String tableName) {
		Session session = sessionFactory.openSession();
		List<FoodEntry> foodEntries = session.createQuery("from " + tableName).list();
		if (foodEntries.isEmpty()) {
			System.out.println("No Rows Found in table : " + tableName);
		}
		session.close();
		for (FoodEntry foodEntry : foodEntries) {
			System.out.println(foodEntry.getFoodId() +"-->" + foodEntry.getFoodName());
		}
	}

	public int numberOfRows(String tableName) {
		Session session = sessionFactory.openSession();
		List<FoodEntry> foodEntries = session.createQuery("from " + tableName).list();
		session.close();
		return foodEntries.size();
	}

	public FoodEntry getPojo(String tableName, long id) {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from " + tableName + " where foodId = " + id);
		FoodEntry foodEntry = (FoodEntry) query.uniqueResult();
		return foodEntry;
	}

	public void deleteAllRows(String tableName) {
		Session session = sessionFactory.openSession();
		Transaction  transaction = session.beginTransaction();
		String hql = String.format("delete from %s", tableName);
	    Query query = session.createQuery(hql);
	    query.executeUpdate();
	    transaction.commit();
	    session.close();
	}
}
