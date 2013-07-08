package hibernate.crud.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import bos.Order;
import hibernate.crud.ICrudOperations;
import hibernate.crud.OrderValidity;
import hibernate.init.HibernateUtil;

public class OrderCrudOperationsDB implements ICrudOperations<Order>{

	private SessionFactory sessionFactory = HibernateUtil.SINGLETON.getSessionFactory();

	private static long INVALID_ORDER = -1L;
	public long insert(Order order) {
		if(!OrderValidity.isValidOrder(order)) {
			return INVALID_ORDER;
		}
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();		
		session.save(order);
		session.flush();
		long insertedOrderId = order.getOrderId();
		transaction.commit();		
		session.close();
		return insertedOrderId;
	}

	public void update(Order order) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();		
		session.update(order);
		transaction.commit();
		session.close();
	}

	public void delete(long orderId) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Order order = (Order) session.get(Order.class, orderId);
		session.delete(order);
		transaction.commit();
		session.close();
	}

	public void read(String tableName) {
		Session session = sessionFactory.openSession();
		List<Order> orders = session.createQuery("from " + tableName).list();
		if (orders.isEmpty()) {
			System.out.println("No Rows Found in table : " + tableName);
		}
		session.close();
		for (Order order : orders) {
			System.out.println("Order ID : " + order.getOrderId() +" >>> " +
					"for Food ID : " + order.getFoodId());
		}
	}

	public int numberOfRows(String tableName) {
		Session session = sessionFactory.openSession();
		List<Order> orders = session.createQuery("from " + tableName).list();
		session.close();
		return orders.size();
	}

	public Order getPojo(String tableName, long id) {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from " + tableName + " where orderId = " + id);
		Order order = (Order) query.uniqueResult();
		return order;
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
