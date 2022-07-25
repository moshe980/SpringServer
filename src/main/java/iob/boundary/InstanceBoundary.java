package iob.boundary;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class InstanceBoundary {
	
	private InstanceId instanceId;
	private String type;
	private String name;
	private Boolean active;
	private Date createdTimestamp;
	private CreatedBy createdBy;
	private Location location;
	private Map<String, Object> instanceAttributes;
	

	public InstanceBoundary() {}


	public InstanceBoundary(InstanceId instanceId, String type, String name, Boolean active, Date createdTimestamp,
			CreatedBy createdBy, Location location, Map<String, Object> instanceAttributes) {
		super();
		this.instanceId = instanceId;
		this.type = type;
		this.name = name;
		this.active = active;
		this.createdTimestamp = createdTimestamp;
		this.createdBy = createdBy;
		this.location = location;
		this.instanceAttributes = instanceAttributes;
	}


	public InstanceId getInstanceId() {
		return instanceId;
	}


	public void setInstanceId(InstanceId instanceId) {
		this.instanceId = instanceId;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Boolean getActive() {
		return active;
	}


	public void setActive(Boolean active) {
		this.active = active;
	}


	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}


	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}


	public CreatedBy getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(CreatedBy createdBy) {
		this.createdBy = createdBy;
	}


	public Location getLocation() {
		return location;
	}


	public void setLocation(Location location) {
		this.location = location;
	}


	public Map<String, Object> getInstanceAttributes() {
		return instanceAttributes;
	}


	public void setInstanceAttributes(Map<String, Object> instanceAttributes) {
		this.instanceAttributes = instanceAttributes;
	}


	@Override
	public String toString() {
		return "InstanceBoundary [instanceId=" + instanceId + ", type=" + type + ", name=" + name + ", active=" + active
				+ ", createdTimestamp=" + createdTimestamp + ", createdBy=" + createdBy + ", location=" + location
				+ ", instanceAttributes=" + instanceAttributes + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(active, createdBy, createdTimestamp, instanceAttributes, instanceId, location, name, type);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstanceBoundary other = (InstanceBoundary) obj;
		return Objects.equals(active, other.active) && Objects.equals(createdBy, other.createdBy)
				&& Objects.equals(createdTimestamp, other.createdTimestamp)
				&& Objects.equals(instanceAttributes, other.instanceAttributes)
				&& Objects.equals(instanceId, other.instanceId) && Objects.equals(location, other.location)
				&& Objects.equals(name, other.name) && Objects.equals(type, other.type);
	}

	

	
	
	
}
