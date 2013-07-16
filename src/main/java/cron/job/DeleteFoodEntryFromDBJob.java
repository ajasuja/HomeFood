package cron.job;

import hibernate.crud.ICrudOperations;
import hibernate.crud.impl.FoodEntryCrudOperationsDBAndIndex;
import hibernate.crud.impl.FoodEntryDeadCrudOperationsDB;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import bos.FoodEntry;
import bos.FoodEntryDead;

public class DeleteFoodEntryFromDBJob implements Job {

	private FoodEntryDead adaptFoodEntryLiveToDead(FoodEntry foodEntry) {
		if (foodEntry == null) {
			System.out.println(this.getClass().getSimpleName() + " >>> Food Entry is null");
		}
		FoodEntryDead foodEntryDead = new FoodEntryDead();
		foodEntryDead.setFoodId(foodEntry.getFoodId());
		foodEntryDead.setCookId(foodEntry.getCookId());
		foodEntryDead.setFoodName(foodEntry.getFoodName());
		foodEntryDead.setFoodDescription(foodEntry.getFoodDescription());
		foodEntryDead.setNumOfPersons(foodEntry.getNumOfPersons());
		foodEntryDead.setPricePerPerson(foodEntry.getPricePerPerson());
		foodEntryDead.setCurrencyType(foodEntry.getCurrencyType());
		foodEntryDead.setEntryCreatedTime(foodEntry.getEntryCreatedTime());
		foodEntryDead.setOrderTimeDeadline(foodEntry.getOrderTimeDeadline());
		foodEntryDead.setChangeTime(foodEntry.getChangeTime());
		return foodEntryDead;
	}
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ICrudOperations<FoodEntry> crudOperations = new FoodEntryCrudOperationsDBAndIndex();
		FoodEntry foodEntry = crudOperations.getPojo("FoodEntry", 1L);
		crudOperations.delete(1L);
		ICrudOperations<FoodEntryDead> crudOperationsForFoodEntryDead = new FoodEntryDeadCrudOperationsDB();
		long id = crudOperationsForFoodEntryDead.insert(adaptFoodEntryLiveToDead(foodEntry));
	}

}
