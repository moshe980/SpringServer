package iob.logic;

import java.util.List;

import iob.boundary.ActivityBoundary;

public interface ActivitiesServiceExtended extends ActivitiesService {
	
	public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail,int size,int page);

}
