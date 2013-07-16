package bos;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

@Entity
@Table(name="OrderEntry")
@Audited
public class Order {

	@Id
	@GeneratedValue
	@Column(name = "ORDER_ID", unique = true, nullable = false)
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	@Column(name = "FOOD_ID", nullable = false)
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
	public void setCookId(long coodId) {
		this.cookId = coodId;
	}
	@Column(name = "NUM_OF_PERSONS", nullable = false)
	public int getNumOfPersons() {
		return numOfPersons;
	}
	public void setNumOfPersons(int numOfPersons) {
		this.numOfPersons = numOfPersons;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ORDER_TIME", nullable = false)
	public Date getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}
	
	@Column(name = "ORDER_NOTE", nullable = true)
	public String getOrderNote() {
		return orderNote;
	}
	public void setOrderNote(String orderNote) {
		this.orderNote = orderNote;
	}

	private long orderId;
	private long foodId;
	private long cookId;
	private int numOfPersons;
	private Date orderTime;
	private String orderNote;
}
