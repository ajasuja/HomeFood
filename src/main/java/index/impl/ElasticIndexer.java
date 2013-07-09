package index.impl;

import index.Indexer;

import java.util.List;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;

import static index.impl.ElasticIndexerHelper.*;
import serialize.impl.FoodEntryToJson;

import bos.FoodEntry;

public class ElasticIndexer implements Indexer {
	
		
	public void closeNodeAfterTest() {
		close();
	}
	
	public void addFoodEntryToIndex(FoodEntry foodEntry) {
		String indexId = foodEntry.getFoodId()+"";
		IndexResponse indexResponse = 
				getElaticClient().prepareIndex(getIndexName(), getIndexType(), indexId)
				.setSource(new FoodEntryToJson().getJsonFromPojo(foodEntry))
				.execute()
				.actionGet();
		System.out.println(this.getClass().getSimpleName() + " >>>> "
				+"Id : " + indexResponse.getId().toString() + ",Version : " + indexResponse.getVersion());
	}

	public void addFoodEntriesToIndex(List<FoodEntry> foodEntries) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Batch indexing not supported yet");
		
	}
	public void deleteFoodEntryFromIndex(String foodId) {
		DeleteResponse deleteResponse = getElaticClient().prepareDelete(getIndexName(), getIndexType(), foodId)
		        .execute()
		        .actionGet();
		System.out.println("Version after delete : " + deleteResponse.getVersion());
	}
}
