package index;


import junit.framework.Assert;
import index.Indexer;
import index.impl.ElasticClientType;
import index.impl.ElasticIndexer;

import org.junit.Test;

import query.ElasticSearcher;
import query.Query;

import data.FoodEntryBuilder;

import bos.FoodEntry;

public class ElasticIndexerTest {

	@Test
	public void testAddFoodEntryToIndex() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithId();
		Indexer indexer = new ElasticIndexer(ElasticClientType.TEST);
		indexer.addFoodEntryToIndex(foodEntry);
		Query searcher = new ElasticSearcher(ElasticClientType.TEST);
		Assert.assertNotNull(searcher.getDocumentById(foodEntry.getFoodId()+""));
		indexer.closeNodeAfterTest();
	}
	
	@Test
	public void testAddAndUpdateFoodEntryToIndex() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithId();
		Indexer indexer = new ElasticIndexer(ElasticClientType.TEST);
		indexer.addFoodEntryToIndex(foodEntry);
		Query searcher = new ElasticSearcher(ElasticClientType.TEST);
		Assert.assertNotNull(searcher.getDocumentById(foodEntry.getFoodId()+""));
		String updatedFoodName = "Shahi Paneer";
		foodEntry.setFoodName(updatedFoodName);
		Assert.assertEquals("Before Update it doesn't contain updated name"
				, false, searcher.getDocumentById(foodEntry.getFoodId()+"").contains(updatedFoodName));
		indexer.addFoodEntryToIndex(foodEntry);
		Assert.assertNotNull(searcher.getDocumentById(foodEntry.getFoodId()+""));
		Assert.assertEquals("After Update it must contain updated name"
				, true, searcher.getDocumentById(foodEntry.getFoodId()+"").contains(updatedFoodName));
		indexer.closeNodeAfterTest();
	}

	@Test
	public void testAddAndDeleteFoodEntry() {
		FoodEntry foodEntry = FoodEntryBuilder.SINGLETON.buildDefaultFoodEntryWithId();
		Indexer indexer = new ElasticIndexer(ElasticClientType.TEST);
		indexer.addFoodEntryToIndex(foodEntry);
		Query searcher = new ElasticSearcher(ElasticClientType.TEST);
		Assert.assertNotNull(searcher.getDocumentById(foodEntry.getFoodId()+""));
		indexer.deleteFoodEntryFromIndex(foodEntry.getFoodId() + "");
		Assert.assertNull(searcher.getDocumentById(foodEntry.getFoodId()+""));
		indexer.closeNodeAfterTest();
	}
}
