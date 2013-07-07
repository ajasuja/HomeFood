package serialize.impl;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import serialize.PojoToJson;

import bos.FoodEntry;

public class FoodEntryToJson implements PojoToJson<FoodEntry>{

	public String getJsonFromPojo(FoodEntry foodEntry) {
		ObjectMapper mapper = new ObjectMapper();
        ObjectWriter jsonObjectWriter = mapper.writerWithDefaultPrettyPrinter();
		String jsonStringValue = null;
		try {
			jsonStringValue =  jsonObjectWriter.writeValueAsString(foodEntry);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonStringValue;
	}

}
