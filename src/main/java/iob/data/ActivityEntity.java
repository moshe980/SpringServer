package iob.data;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "ACTIVITIES")
/* ACTIVITIES table:
 * ID                 | TYPE       	  | INSTANCE	   | CREATED_TIMESTAMP	| INVOKED_BY	| ACTIVITY_ATTRIBUTES 	|
 * VARCHAR(255) <PK>  | VARCHAR(255)  | VARCHAR(255)   | TIMESTAMP			| VARCHAR(255)	| CLOB					|
 */
public class ActivityEntity {
	
	private String activityId;
	private String type;
	private String instance;
	private Date createdTimestamp;
	private String invokedBy;
	private Map<String, Object> activityAttributes;

	public ActivityEntity() {}
	

	@MongoId
	public String getActivityId() {
		return activityId;
	}


	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

//	@Transient
	public String getInstance() {
		return instance;
	}

//	@Transient
	public void setInstance(String instance) {
		this.instance = instance;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}


	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

//	@Transient
	public String getInvokedBy() {
		return invokedBy;
	}

//	@Transient
	public void setInvokedBy(String invokedBy) {
		this.invokedBy = invokedBy;
	}

//	@Transient
	@Lob
	@Convert(converter = MapToStringConverter.class)
	public Map<String, Object> getActivityAttributes() {
		return activityAttributes;
	}

//	@Transient
	public void setActivityAttributes(Map<String, Object> activityAttributes) {
		this.activityAttributes = activityAttributes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(activityAttributes, activityId, createdTimestamp, instance, invokedBy, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivityEntity other = (ActivityEntity) obj;
		return Objects.equals(activityAttributes, other.activityAttributes)
				&& Objects.equals(activityId, other.activityId)
				&& Objects.equals(createdTimestamp, other.createdTimestamp) && Objects.equals(instance, other.instance)
				&& Objects.equals(invokedBy, other.invokedBy) && Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		return "ActivityEntity [activityId=" + activityId + ", type=" + type + ", instance=" + instance
				+ ", createdTimestamp=" + createdTimestamp + ", invokedBy=" + invokedBy + ", activityAttributes="
				+ activityAttributes + "]";
	}
	
	
	
	
}
