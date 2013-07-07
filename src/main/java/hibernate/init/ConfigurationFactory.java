package hibernate.init;

import index.impl.ElasticClientType;

import org.hibernate.Interceptor;
import org.hibernate.cfg.Configuration;

public enum ConfigurationFactory {
	
	SINGLETON;

	public Configuration getConfiguration(ElasticClientType clientType) {
		Configuration conf = new Configuration();
		String hibernateConfigFileName = clientType.getEnvironment() + "_hibernate.cfg.xml";
		conf.configure(hibernateConfigFileName);
		return conf;
	}
	
	public Configuration getConfigurationWithInterceptor(ElasticClientType clientType, Interceptor interceptor) {
		Configuration conf = new Configuration();
		String hibernateConfigFileName = clientType.getEnvironment() + "_hibernate.cfg.xml";
		conf.configure(hibernateConfigFileName);
		conf.setInterceptor(interceptor);
		return conf;
		
	}
}
