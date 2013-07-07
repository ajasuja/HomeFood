package hibernate.init;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {
	
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
	
	public static SessionFactory getSessionFactory(Configuration conf) {
		serviceRegistry = new ServiceRegistryBuilder().applySettings(conf.getProperties()).buildServiceRegistry();
		try {
			sessionFactory = conf.buildSessionFactory(serviceRegistry);
		} catch (Exception e) {
			System.err.println("Initial SessionFactory creation failed." + e);
			throw new ExceptionInInitializerError(e);
		}
		return sessionFactory;
	}
	
}
