package bos;

public class Cook {

	
	public long getCookId() {
		return cookId;
	}
	public void setCookId(long cookId) {
		this.cookId = cookId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	private long cookId;
	private String displayName;
	private String realName;
	private String address;
	
}
