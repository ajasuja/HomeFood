package hibernate.crud.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import bos.FoodEntry;
import bos.FoodEntryDead;
import hibernate.crud.ICrudOperations;
import hibernate.init.HibernateUtil;

public class FoodEntryDeadCrudOperationsDB implements ICrudOperations<FoodEntryDead>{

	private SessionFactory sessionFactory = HibernateUtil.SINGLETON.getSessionFactory();

	public long insert(FoodEntryDead foodEntryDead) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();		
		session.save(foodEntryDead);
		transaction.commit();		
		long insertedFoodId = foodEntryDead.getFoodId();
		session.close();
		return insertedFoodId;
	}

	public void update(FoodEntryDead pojo) {
		throw new UnsupportedOperationException("Not supported yet");
	}

	public void delete(long id) {
		throw new UnsupportedOperationException("Not supported yet");
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

	public void read(String tableName) {
		throw new UnsupportedOperationException("Not supported yet");
	}

	public int numberOfRows(String tableName) {
		Session session = sessionFactory.openSession();
		List<FoodEntry> foodEntries = session.createQuery("from " + tableName).list();
		session.close();
		return foodEntries.size();
	}

	public FoodEntryDead getPojo(String tableName, long id) {
		throw new UnsupportedOperationException("Not supported yet");
	}

}
