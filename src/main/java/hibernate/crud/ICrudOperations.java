package hibernate.crud;

public interface ICrudOperations<T> {

	public void insert(T pojo);
	
	public void update(T pojo);
	
	public void delete(long id);
	
	public void read(String tableName);
	
	public int numberOfRows(String tableName);
	
	public T getPojo(String tableName, long id);
	
}
