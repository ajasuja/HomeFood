package query;

public interface Query {

	public String getDocumentById(String id);
	
	//TODO remove it from here
	public void closeNodeAfterTest();

}
