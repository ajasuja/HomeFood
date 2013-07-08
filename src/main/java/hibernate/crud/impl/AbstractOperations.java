package hibernate.crud.impl;

import org.hibernate.SessionFactory;

public class AbstractOperations {

	protected SessionFactory sessionFactory;
	
	public AbstractOperations(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
