package hibernate.crud.impl;

import index.impl.ElasticClientType;
import hibernate.init.ConfigurationFactory;
import hibernate.init.HibernateUtil;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class AbstractOperations {

	protected SessionFactory sessionFactory;
	
	public AbstractOperations(ElasticClientType clientType) {
		Configuration hibernateConfig = ConfigurationFactory.SINGLETON.getConfiguration(clientType);
		sessionFactory = HibernateUtil.getSessionFactory(hibernateConfig);
	}

}
