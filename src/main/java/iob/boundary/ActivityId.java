package iob.boundary;

import java.util.Objects;

public class ActivityId {
	
	private String domain;
	private String id;
	
	public ActivityId() {}
	
	public ActivityId(String domain, String id) {this.domain=domain; this.id = id;}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

	@Override
	public int hashCode() {
		return Objects.hash(domain, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivityId other = (ActivityId) obj;
		return Objects.equals(domain, other.domain) && Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "ActivityId [domain=" + domain + ", id=" + id + "]";
	}
	
	
}