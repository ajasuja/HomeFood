package index.impl;

import index.Indexer;

import java.util.List;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;

import static index.impl.ElasticIndexerHelper.*;
import serialize.impl.FoodEntryToJson;

import bos.FoodEntry;

public class ElasticIndexer implements Indexer {
	
	private ElasticClientType clientType;
	
	public ElasticIndexer(ElasticClientType clientType) {
		this.clientType = clientType;
	}
	
	public void closeNodeAfterTest() {
		close();
	}
	
	public void addFoodEntryToIndex(FoodEntry foodEntry) {
		String indexId = foodEntry.getFoodId()+"";
		IndexResponse indexResponse = 
				getElaticClient(this.clientType).prepareIndex(getIndexName(), getIndexType(), indexId)
				.setSource(new FoodEntryToJson().getJsonFromPojo(foodEntry))
				.execute()
				.actionGet();
		System.out.println(this.getClass().getSimpleName() + " >>>> "
				+"Id : " + indexResponse.getId().toString() + ",Version : " + indexResponse.getVersion());
		
//		GetResponse getResponse = getElaticClient(this.clientType).prepareGet(getIndexName(), getIndexType(), indexId)
//		        .execute()
//		        .actionGet();
//		System.out.println("Getting Document Version : " + getResponse.getVersion());
//		System.out.println(getResponse.getSourceAsString());
		
//		close();
	}

	public void addFoodEntriesToIndex(List<FoodEntry> foodEntries) {
		// TODO Auto-generated method stub
		
	}
	public void deleteFoodEntryFromIndex(String foodId) {
		// TODO Auto-generated method stub
		DeleteResponse deleteResponse = getElaticClient(this.clientType).prepareDelete(getIndexName(), getIndexType(), foodId)
		        .execute()
		        .actionGet();
		System.out.println("Version after delete : " + deleteResponse.getVersion());
//		GetResponse getResponse = getElaticClient(this.clientType).prepareGet(getIndexName(), getIndexType(), foodId)
//		        .execute()
//		        .actionGet();
//		if(getResponse.getSourceAsString() == null) {
//			System.out.println("No document matched for id : " + foodId);
//		} else {
//			System.out.println(getResponse.getSourceAsString());
//		}
//		
//		close();
	}
}
