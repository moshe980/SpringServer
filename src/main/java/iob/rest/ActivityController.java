package iob.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import iob.boundary.ActivityBoundary;
import iob.logic.ActivitiesService;

@RestController
public class ActivityController {
	
	@Autowired
	private ActivitiesService activityService;
	
//	@Autowired
//	public ActivityController(ActivitiesService activityService) {
//		this.activityService=activityService;
//	}
	
	//POST request, path="/iob/activities"
	//Accept: Activity Boundary with null activityId
	//Return: Any JSON Object
	@RequestMapping(
			path = "/iob/activities",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public Object invokeActivity(@RequestBody ActivityBoundary activity) {

		return activityService.invokeActivity(activity);
	}
	
	
}

