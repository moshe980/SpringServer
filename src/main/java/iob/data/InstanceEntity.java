package iob.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "INSTANCES")
public class InstanceEntity {

	private String instanceId;
	private String type;
	private String name;
	private boolean active;
	private Date createdTimestamp;
	private String createdBy;
	private LocationEntity location;
	private Map<String, Object> instanceAttributes;
	
	@DBRef(lazy = true)
	private Set<InstanceEntity> parents;
	
	@DBRef(lazy = true)
	private Set<InstanceEntity> children;

	public InstanceEntity() {
		this.parents=new HashSet<>();
		this.children=new HashSet<>();
	}

	@MongoId
	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
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
	
	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Embedded
	public LocationEntity getLocation() {
		return location;
	}
	
	@Embedded
	public void setLocation(LocationEntity location) {
		this.location = location;
	}
	
	// store this value as a very long string
	@Lob
	@Convert(converter = MapToStringConverter.class)
	public Map<String, Object> getInstanceAttributes() {
		return instanceAttributes;
	}

	public void setInstanceAttributes(Map<String, Object> instanceAttributes) {
		this.instanceAttributes = instanceAttributes;
	}
	
   // @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
   // @JoinTable(name = "children_parents",
	//    joinColumns = {@JoinColumn(name="child_id", referencedColumnName = "instanceId")},
	//    inverseJoinColumns = {@JoinColumn(name="parent_id", referencedColumnName = "instanceId")})
	public Set<InstanceEntity> getParents() {
		return parents;
	}

	public void setParents(Set<InstanceEntity> parents) {
		this.parents = parents;
	}
	
    //@ManyToMany(fetch = FetchType.LAZY,mappedBy = "parents")
	public Set<InstanceEntity> getChildren() {
		return children;
	}

	public void setChildren(Set<InstanceEntity> children) {
		this.children = children;
	}

    public void addParent(InstanceEntity parent) {
        this.parents.add(parent);
        parent.children.add(this);
    }

    public void addChild(InstanceEntity child) {
        this.children.add(child);
        child.parents.add(this);
    }
    
    public void removeParent(InstanceEntity parent) {
    	this.parents.remove(parent);
    	parent.children.remove(this);
    }
    
    public void removeChild(InstanceEntity child) {
        this.children.remove(child);
        child.parents.remove(this);
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
		InstanceEntity other = (InstanceEntity) obj;
		return Objects.equals(instanceId, other.instanceId);
	}

	@Override
	public String toString() {
		return "InstanceEntity [instanceId=" + instanceId + ", type=" + type + ", name=" + name + ", active=" + active
				+ ", createdTimestamp=" + createdTimestamp + ", createdBy=" + createdBy + ", location=" + location
				+ ", instanceAttributes=" + instanceAttributes + ", parents=" + parents + ", children=" + children
				+ "]";
	}


	

	
	
	
	
	

}
