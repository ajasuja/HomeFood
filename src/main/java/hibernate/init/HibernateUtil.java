package hibernate.init;

import hibernate.interceptor.IndexerInterceptor;
import hibernate.interceptor.OrderInterceptor;
import index.impl.ElasticClientType;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public enum HibernateUtil {
	
	SINGLETON;
	
	private static SessionFactory sessionFactory;
	private static ServiceRegistry serviceRegistry;

//	public static SessionFactory getSessionFactoryWithInterceptor(ConfigurInterceptor interceptor) {
//		String hibernateConfigFileName = ENVIRONMENT + "_hibernate.cfg.xml";
//		Configuration conf = new Configuration();
//		conf.configure(hibernateConfigFileName).setInterceptor(interceptor);
//		serviceRegistry = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
//		try {
//			sessionFactory = conf.buildSessionFactory(serviceRegistry);
//		} catch (Exception e) {
//			System.err.println("Initial SessionFactory creation failed." + e);
//			throw new ExceptionInInitializerError(e);
//		}
//		return sessionFactory;
//	}
	
	public SessionFactory getSessionFactory() {
		String environment = System.getProperty("env");
		ElasticClientType clientType = ElasticClientType.getClientType(environment);
		Configuration conf = ConfigurationFactory.SINGLETON.getConfigurationWithInterceptor(clientType, new IndexerInterceptor(clientType));
		if (sessionFactory == null) {
			serviceRegistry = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
			try {
					sessionFactory = conf.buildSessionFactory(serviceRegistry);
			} catch (Exception e) {
				System.err.println("Initial SessionFactory creation failed." + e);
				throw new ExceptionInInitializerError(e);
			}
		}
		return sessionFactory;
	}

	public static void closeSessionFactory() {
		sessionFactory.close();
	}
}
