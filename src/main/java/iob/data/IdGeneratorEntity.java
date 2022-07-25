package iob.data;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "ID_GENERATOR_HELPER_COLLECTION")
public class IdGeneratorEntity {
	private String id;

	public IdGeneratorEntity() {}
	
	@MongoId
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

}
