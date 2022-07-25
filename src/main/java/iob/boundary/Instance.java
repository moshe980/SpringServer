package iob.boundary;

import java.util.Objects;

public class Instance {
	
	private InstanceId instanceId;
	
	public Instance() {}
	
	public Instance(InstanceId instanceId) {
		this.instanceId = instanceId;
	}
	public InstanceId getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(InstanceId instanceId) {
		this.instanceId = instanceId;
	}

	
	
	@Override
	public int hashCode() {
		return Objects.hash(instanceId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instance other = (Instance) obj;
		return Objects.equals(instanceId, other.instanceId);
	}

	@Override
	public String toString() {
		return "Instance [instanceId=" + instanceId + "]";
	}
	
	

}
