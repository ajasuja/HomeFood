package query;

import static index.impl.ElasticIndexerHelper.getElaticClient;
import static index.impl.ElasticIndexerHelper.getIndexName;
import static index.impl.ElasticIndexerHelper.getIndexType;
import index.impl.ElasticClientType;
import index.impl.ElasticIndexerHelper;

import org.elasticsearch.action.get.GetResponse;

public class ElasticSearcher implements Query{
	
	private ElasticClientType clientType;
	
	public ElasticSearcher(ElasticClientType clientType) {
		this.clientType = clientType;
	}

	
	public String getDocumentById(String id) {
		GetResponse getResponse = getElaticClient(this.clientType).prepareGet(getIndexName(), getIndexType(), id)
		        .execute()
		        .actionGet();
		return getResponse.getSourceAsString();
	}


	public void closeNodeAfterTest() {
		ElasticIndexerHelper.close();
	}

}
