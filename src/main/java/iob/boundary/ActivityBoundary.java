package iob.boundary;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class ActivityBoundary {
	
	private ActivityId activityId;
	private String type;
	private Instance instance;
	private Date createdTimestamp;
	private CreatedBy invokedBy;
	private Map<String, Object> activityAttributes;
	

	public ActivityBoundary() {}


	public ActivityBoundary(ActivityId activityId, String type, Instance instance, Date createdTimestamp,
			CreatedBy invokedBy, Map<String, Object> activityAttributes) {
		super();
		this.setActivityId(activityId);
		this.setType(type);
		this.setInstance(instance);
		this.setCreatedTimestamp(createdTimestamp);
		this.setInvokedBy(invokedBy);
		this.setActivityAttributes(activityAttributes);
	}


	public ActivityId getActivityId() {
		return activityId;
	}


	public void setActivityId(ActivityId activityId) {
		this.activityId = activityId;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public Instance getInstance() {
		return instance;
	}


	public void setInstance(Instance instance) {
		this.instance = instance;
	}


	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}


	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}


	public CreatedBy getInvokedBy() {
		return invokedBy;
	}


	public void setInvokedBy(CreatedBy invokedBy) {
		this.invokedBy = invokedBy;
	}




	public Map<String, Object> getActivityAttributes() {
		return activityAttributes;
	}


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
		ActivityBoundary other = (ActivityBoundary) obj;
		return Objects.equals(activityAttributes, other.activityAttributes)
				&& Objects.equals(activityId, other.activityId)
				&& Objects.equals(createdTimestamp, other.createdTimestamp) && Objects.equals(instance, other.instance)
				&& Objects.equals(invokedBy, other.invokedBy) && Objects.equals(type, other.type);
	}


	@Override
	public String toString() {
		return "ActivityBoundary [activityId=" + activityId + ", type=" + type + ", instance=" + instance
				+ ", createdTimestamp=" + createdTimestamp + ", invokedBy=" + invokedBy +
				", activityAttributes=" + activityAttributes + "]";
	}
	
	
	

}