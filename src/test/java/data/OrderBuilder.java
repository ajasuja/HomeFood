package data;

import java.util.Date;

import bos.Order;

public enum OrderBuilder {

	SINGLETON;
	
	public long ORDER_ID = 1L;
	public long FOOD_ID = 1L;	
	public long COOK_ID = 1001L;
	public int NUM_OF_PERSONS = 5;
	public String ORDER_NOTE = "Make it medium spicy";
		
	public Order buildDefaultOrder() {
		Order order = new Order();
//		order.setOrderId(ORDER_ID);
		order.setFoodId(FOOD_ID);
		order.setCookId(COOK_ID);
		order.setNumOfPersons(NUM_OF_PERSONS);
		order.setOrderTime(new Date());
		order.setOrderNote(ORDER_NOTE);
		return order;
	}

}
