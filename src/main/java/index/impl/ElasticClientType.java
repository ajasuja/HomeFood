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
}
