package iob.logic;

import java.util.List;
import java.util.Set;

import iob.boundary.InstanceBoundary;
import iob.boundary.InstanceIdBoundary;

public interface InstancesServiceExtended extends InstancesService {
	
	public void bindChildToInstance(String userDomain,String userEmail,String instanceDomain,String instanceId,InstanceIdBoundary instanceIdBoundary);

	public Set<InstanceBoundary> getAllInstanceChildren(String userDomain,String userEmail,String instanceDomain,String instanceId, int size, int page);

	public Set<InstanceBoundary> getAllInstanceParents(String userDomain,String userEmail,String instanceDomain,String instanceId, int size, int page);
	
	public List<InstanceBoundary> getAllInstances(String userDomain,String userEmail,int size, int page);

	public List<InstanceBoundary> getAllInstanceByName(String userDomain, String userEmail, String name, int size, int page);

	public List<InstanceBoundary> getAllInstanceByType(String userDomain, String userEmail, String type, int size, int page);

	public List<InstanceBoundary> getAllInstanceByLocation(String userDomain, String userEmail, double lat, double lng,
			double distance, int size, int page);

	public List<InstanceBoundary> getAllInstanceByTimeCreation(String userDomain, String userEmail, String creationWindow,int size, int page);

	public void removeBindChildToInstance(String userDomain,String userEmail,String instanceDomain,String instanceId,InstanceIdBoundary instanceIdBoundary);


}
