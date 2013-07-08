package index.impl;

public enum ElasticClientType {
	
	TEST("test"),
	DEV("dev"),
	QA("qa"),
	PROD("prod");
	
	private String environment;
	
	private ElasticClientType(String environment) {
		this.environment = environment;
	}
	
	public String getEnvironment() {
		return this.environment;
	}
	
	public static ElasticClientType getClientType(String environment) {
		ElasticClientType matchedClientType = null;
		for (ElasticClientType clientType : ElasticClientType.values()) {
			if (clientType.getEnvironment().equals(environment)) {
				matchedClientType = clientType;
			}
		}
		return matchedClientType;
	}
}
