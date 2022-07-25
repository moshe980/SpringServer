package iob.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import iob.boundary.ActivityBoundary;
import iob.boundary.UserBoundary;
import iob.logic.ActivitiesService;
import iob.logic.ActivitiesServiceExtended;
import iob.logic.InstancesServiceExtended;
import iob.logic.InstancesService;
import iob.logic.UsersService;
import iob.logic.UsersServiceExtended;


@RestController
public class AdminController {
	
	private ActivitiesServiceExtended activityService;
	private UsersServiceExtended userService;
	private InstancesServiceExtended instanceService;
	
	@Autowired
	public AdminController(ActivitiesServiceExtended activityService, UsersServiceExtended userService, InstancesServiceExtended instanceService)
	{
		this.activityService = activityService;
		this.userService = userService;
		this.instanceService = instanceService;
	}
	

	//DELETE request, path="iob/admin/users/{userDomain}/{userEmail}"
	//Accept: None
	//Return: None
	@RequestMapping(
			method = RequestMethod.DELETE,
			path = "/iob/admin/users/{userDomain}/{userEmail}")
	public void deleteAllUsersInTheDomain (
			@PathVariable("userDomain") String userDomain, 
			@PathVariable("userEmail") String userEmail){
		userService.deleteAllUsers(userDomain, userEmail);
	}

	//DELETE request, path="/iob/admin/instances/{userDomain}/{userEmail}"
	//Accept: None
	//Return: None
	@RequestMapping(
			method = RequestMethod.DELETE,
			path = "/iob/admin/instances/{userDomain}/{userEmail}")
	public void deleteAllInstancesInTheDomain (
			@PathVariable("userDomain") String userDomain, 
			@PathVariable("userEmail") String userEmail){
		instanceService.deleteAllInstances(userDomain, userEmail);		
	}

	//DELETE request, path="/iob/admin/activities/{userDomain}/{userEmail}"
	//Accept: None
	//Return: None
	@RequestMapping(
			method = RequestMethod.DELETE,
			path = "/iob/admin/activities/{userDomain}/{userEmail}")
	public void deleteAllActivitiesInTheDomain (
			@PathVariable("userDomain") String userDomain, 
			@PathVariable("userEmail") String userEmail){
		activityService.deleteAllActivities(userDomain, userEmail);
	}


	//GET request, path="/iob/admin/users/{userDomain}/{userEmail}"
	//Accept: None
	//Return: Array of UserBoundary
	@RequestMapping(
			path="/iob/admin/users/{userDomain}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] exportAllUsers (
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
	        @RequestParam(name = "page", required = false, defaultValue = "0") int page) {	
		
		return userService.getAllUsers(userDomain, userEmail,size,page).toArray(new UserBoundary[0]);
	}

	//GET request, path="//iob/admin/activities/{userDomain}/{userEmail}"
	//Accept: None
	//Return: Array of UserBoundary
	@RequestMapping(
			path="/iob/admin/activities/{userDomain}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ActivityBoundary[] exportAllActivities (
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
	        @RequestParam(name = "page", required = false, defaultValue = "0") int page) {	
				
		return activityService.getAllActivities(userDomain, userEmail,size,page).toArray(new ActivityBoundary[0]);
	}

}
