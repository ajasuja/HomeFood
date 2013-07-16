package bos;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

@Entity
@Table
@Audited
public class FoodEntryDead {
	
	@Id
	@Column(name = "FOOD_ID", unique = true, nullable = false)
	public long getFoodId() {
		return foodId;
	}

	public void setFoodId(long foodId) {
		this.foodId = foodId;
	}

	@Column(name = "COOK_ID", nullable = false)
	public long getCookId() {
		return cookId;
	}

	public void setCookId(long cookId) {
		this.cookId = cookId;
	}

	@Column(name = "FOOD_NAME", nullable = false)
	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	@Column(name = "FOOD_DESC", nullable = false)
	public String getFoodDescription() {
		return foodDescription;
	}

	public void setFoodDescription(String foodDescription) {
		this.foodDescription = foodDescription;
	}

	@Column(name = "PRICE_PER_PERSON", nullable = false)	
	public double getPricePerPerson() {
		return pricePerPerson;
	}

	public void setPricePerPerson(double pricePerPerson) {
		this.pricePerPerson = pricePerPerson;
	}

	@Column(name = "NUM_OF_PERSONS", nullable = false)
	public int getNumOfPersons() {
		return numOfPersons;
	}

	public void setNumOfPersons(int numOfPersons) {
		this.numOfPersons = numOfPersons;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "CURRENCY_TYPE", nullable = false)
	public CurrencyType getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(CurrencyType currencyType) {
		this.currencyType = currencyType;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ORDER_TIME_DEADLINE", nullable = false)
	public Date getOrderTimeDeadline() {
		return orderTimeDeadline;
	}

	public void setOrderTimeDeadline(Date orderTimeDeadline) {
		this.orderTimeDeadline = orderTimeDeadline;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CHANGE_TIME", nullable = false)
	public Date getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FOOD_ENTRY_CREATED_TIME", nullable = false)
	public Date getEntryCreatedTime() {
		return entryCreatedTime;
	}

	public void setEntryCreatedTime(Date entryCreatedTime) {
		this.entryCreatedTime = entryCreatedTime;
	}

	
	@PrePersist
    protected void onCreate() {
		changeTime = new Date();
    }
	
	private long foodId;	
	private long cookId;
	private String foodName;
	private String foodDescription;
	private double pricePerPerson;
	private int numOfPersons;
	private CurrencyType currencyType;
	private Date entryCreatedTime;
	private Date orderTimeDeadline;
	private Date changeTime;	

}
