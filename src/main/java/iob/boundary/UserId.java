package iob.boundary;

import java.util.Objects;

public class UserId {
	
	

	private String domain;
	private String email;
	
	public UserId() {}

	public UserId(String domain, String email) {
		super();
		this.domain = domain;
		this.email = email;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserId [domain=" + domain + ", email=" + email + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(domain, email);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserId other = (UserId) obj;
		return Objects.equals(domain, other.domain) && Objects.equals(email, other.email);
	}
	

}
