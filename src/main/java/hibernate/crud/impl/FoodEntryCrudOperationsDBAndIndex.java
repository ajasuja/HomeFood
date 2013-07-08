package hibernate.crud.impl;

import java.util.List;

import index.impl.ElasticClientType;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import bos.FoodEntry;
import hibernate.crud.ICrudOperations;
import hibernate.init.ConfigurationFactory;
import hibernate.init.HibernateUtil;
import hibernate.interceptor.IndexerInterceptor;

public class FoodEntryCrudOperationsDBAndIndex implements ICrudOperations<FoodEntry> {

	private SessionFactory sessionFactory = HibernateUtil.SINGLETON.getSessionFactory();
	
//	public FoodEntryCrudOperationsDBAndIndex(ElasticClientType clientType) {
//		Configuration hibernateConfig = ConfigurationFactory.SINGLETON
//				.getConfigurationWithInterceptor(clientType , new IndexerInterceptor(clientType));
//		sessionFactory = HibernateUtil.getSessionFactory(hibernateConfig);
//	}
	
	public long insert(FoodEntry foodEntry) {
//		Configuration hibernateConfig = ConfigurationFactory.SINGLETON
//				.getConfigurationWithInterceptor(environment , new IndexerInterceptor());
//		SessionFactory sessionFactory = HibernateUtil.getSessionFactory(hibernateConfig);
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
//		Configuration hibernateConfig = ConfigurationFactory.SINGLETON
//				.getConfigurationWithInterceptor(environment , new IndexerInterceptor());
//		SessionFactory sessionFactory = HibernateUtil.getSessionFactory(hibernateConfig);
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();		
		session.update(foodEntry);
		transaction.commit();
		session.close();
	}

	public void delete(long foodId) {
//		Configuration hibernateConfig = ConfigurationFactory.SINGLETON
//				.getConfigurationWithInterceptor(environment , new IndexerInterceptor());
//		SessionFactory sessionFactory = HibernateUtil.getSessionFactory(hibernateConfig);
		Session session = sessionFactory.openSession();
		Transaction  transaction = session.beginTransaction();
		FoodEntry foodEntry = (FoodEntry) session.get(FoodEntry.class, foodId);
		session.delete(foodEntry);
		transaction.commit();
		session.close();
	}

	public void read(String tableName) {
		// TODO Auto-generated method stub
		
	}

	public int numberOfRows(String tableName) {
		// TODO Auto-generated method stub
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
		Transaction transaction = session.beginTransaction();
		String hql = String.format("delete from %s", tableName);
	    Query query = session.createQuery(hql);
	    query.executeUpdate();
	    transaction.commit();
	    session.close();
	}

}
