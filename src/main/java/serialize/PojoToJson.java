package serialize;

public interface PojoToJson<T> {

	public String getJsonFromPojo(T pojo);
}
