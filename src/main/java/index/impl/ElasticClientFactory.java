package index.impl;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class ElasticClientFactory {
	
	private static String CLUSTERNAME_PROPERTY = "cluster.name";
	private static String CLUSTERNAME = "homefood";
	private static String TEST_CLUSTERNAME = "test_homefood";
	
	private static String GATEWAY_TYPE = "gateway.type";
	private static String NONE_GATEWAYTYPE = "none";
	
	private static String INDEX_STORE_TYPE = "index.store.type";
	private static String IN_MEMORY_INDEX = "memory";
	
	
	private static Node node;
	
	public static void closeNode() {
		if(node == null) {
			return;
		}
		node.close();
	}
	
	public static void startNode(){
		node.start();
	}
	
	public static void stopNode() {
		node.stop();
	}

	//TODO Read from a config/properties file based on maven build parameter
	private static Settings getDevSettings() {
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put(CLUSTERNAME_PROPERTY, CLUSTERNAME).build();
		return settings;
	}

	//TODO Read from a config/properties file based on maven build parameter
	private static Settings getTestSettings() {
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put(CLUSTERNAME_PROPERTY, TEST_CLUSTERNAME)
		        .put(GATEWAY_TYPE, NONE_GATEWAYTYPE)
		        .put(INDEX_STORE_TYPE, IN_MEMORY_INDEX)
		        .build();
		return settings;		
	}
	public static Client getClientInstance(ElasticClientType clientType) {
		String HOSTNAME;
		int PORT;
		switch(clientType) {
		case TEST:
			if(node == null) {
				node = new NodeBuilder().settings(getTestSettings()).node();
			}
			return node.client();
			
		case DEV:
			HOSTNAME = "localhost";
			PORT = 9300;
			return new TransportClient(getDevSettings())
						.addTransportAddress(new InetSocketTransportAddress(HOSTNAME, PORT));
			
		case QA:
			throw new UnsupportedOperationException("QA type environment is not supported yet");
			
		case PROD: 
			throw new UnsupportedOperationException("PROD type environment is not supported yet");
			
		default:
			if(node == null) { 
				node = new NodeBuilder().settings(getTestSettings()).node();
			}
			return node.client();
					
		}
	}
}
