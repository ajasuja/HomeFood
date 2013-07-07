package hibernate.crud;

public interface IReadOpeations {

	public int numberOfEntries(String tableName);
	
	public void read(String tableName);
	
}
