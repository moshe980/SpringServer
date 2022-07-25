package iob.logic;

import iob.boundary.ActivityBoundary;

import java.util.List;

public interface ActivitiesService {
	
	public Object invokeActivity(ActivityBoundary activity);
	
	@Deprecated
	public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail);
	
	public void deleteAllActivities(String adminDomain, String adminEmail);
	
	
}
