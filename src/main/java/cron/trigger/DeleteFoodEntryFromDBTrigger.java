package cron.trigger;

import java.util.Date;

import org.quartz.Trigger;

import static org.quartz.TriggerBuilder.*;
public class DeleteFoodEntryFromDBTrigger {

	private static String DELETE_ENTRY_DB_TIGGER_NAME = "DeleteEntryDBTrigger";

	public Trigger getTriggerBasedOnDate(Date date) {
		return newTrigger()
				.withIdentity(DELETE_ENTRY_DB_TIGGER_NAME+"_"+date.getTime())
				.startAt(date)
				.build();
		
	}
}
