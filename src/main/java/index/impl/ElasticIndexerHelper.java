package index.impl;

import org.elasticsearch.client.Client;

public class ElasticIndexerHelper {


	private static String INDEXNAME = "homefood";
	private static String INDEXTYPE = "food";
	
	public static void close() {
		ElasticClientFactory.closeNode();
	}
	public static Client getElaticClient(ElasticClientType clientType ) {
		return ElasticClientFactory.getClientInstance(clientType);
	}
	
	public static String getIndexName() {
		return INDEXNAME;
	}
	
	public static String getIndexType() {
		return INDEXTYPE;
	}
}
