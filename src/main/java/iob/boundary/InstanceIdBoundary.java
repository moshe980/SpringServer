package iob.boundary;

public class InstanceIdBoundary {
	private String domain;
	private String id;
	
	
	public InstanceIdBoundary() {}

	public InstanceIdBoundary(String domain, String id) {
		super();
		this.domain = domain;
		this.id = id;
	}



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
	
	

}
