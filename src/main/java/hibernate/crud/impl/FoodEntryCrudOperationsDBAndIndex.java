package hibernate.crud.impl;

import java.util.Date;
import java.util.List;


import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import cron.job.DeleteFoodEntryFromDBJob;
import cron.trigger.DeleteFoodEntryFromDBTrigger;


import bos.FoodEntry;
import hibernate.crud.ICrudOperations;
import hibernate.init.HibernateUtil;
import hibernate.interceptor.IndexerInterceptor;

public class FoodEntryCrudOperationsDBAndIndex implements ICrudOperations<FoodEntry> {

	public static String DELETE_ENTRY_DB_JOB_NAME = "DeleteEntryDBJob";
	private SessionFactory sessionFactory = HibernateUtil.SINGLETON.getSessionFactory();
		
	public long insert(FoodEntry foodEntry) {
		SessionBuilder sessionBuilder = sessionFactory.withOptions();
		Session session = sessionBuilder.interceptor(new IndexerInterceptor()).openSession();
		Transaction transaction = session.beginTransaction();		
		session.save(foodEntry);
//		session.flush();
		transaction.commit();//This will again call the session flush which you have called above
		long insertedFoodId = foodEntry.getFoodId();
		/*Code for scheduling job to delete dead food entry from DB and Index*/
		try {
//			SingleScheduler singleScheduler = SingleScheduler.SINGLETON.getSchedulerInstance();
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
	    	scheduler.start();
	    	Date orderDeadlineDate = foodEntry.getOrderTimeDeadline();
			JobDetail job = JobBuilder.newJob(DeleteFoodEntryFromDBJob.class).withIdentity(DELETE_ENTRY_DB_JOB_NAME + "_" + orderDeadlineDate.getTime()).build();
//			singleScheduler.startScheduler();
			scheduler.scheduleJob(job, new DeleteFoodEntryFromDBTrigger().getTriggerBasedOnDate(orderDeadlineDate));
//			Thread.sleep(20L*1000);			
//			singleScheduler.scheduleJob(job, new DeleteFoodEntryFromDBTrigger().getTriggerBasedOnDate(orderDeadlineDate));
//			Thread.sleep(15L*1000);
		} catch (SchedulerException e) {
			e.printStackTrace();
		} 
		session.close();
		return insertedFoodId;
	}

	public void update(FoodEntry foodEntry) {
		SessionBuilder sessionBuilder = sessionFactory.withOptions();
		Session session = sessionBuilder.interceptor(new IndexerInterceptor()).openSession();
		Transaction transaction = session.beginTransaction();		
		session.update(foodEntry);
		transaction.commit();
		session.close();
	}

	public void delete(long foodId) {
		SessionBuilder sessionBuilder = sessionFactory.withOptions();
		Session session = sessionBuilder.interceptor(new IndexerInterceptor()).openSession();
		Transaction  transaction = session.beginTransaction();
		FoodEntry foodEntry = (FoodEntry) session.get(FoodEntry.class, foodId);
		if (foodEntry == null) {
			System.out.println(foodId + " >>> Food Id to be deleted is not present in Table");
		}
		session.delete(foodEntry);
		transaction.commit();
		session.close();
	}

	public void read(String tableName) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Read is not supported till now");
		
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
