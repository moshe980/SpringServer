package iob.rest;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import iob.boundary.CreatedBy;
import iob.boundary.InstanceBoundary;
import iob.boundary.InstanceId;
import iob.boundary.InstanceIdBoundary;
import iob.boundary.Location;
import iob.boundary.UserId;
import iob.logic.InstancesServiceExtended;
import iob.logic.InstancesService;


@RestController
public class InstanceController {
	private InstancesServiceExtended service;
	
	@Autowired
	public InstanceController(InstancesServiceExtended service) {
		this.service=service;
	}

	//POST request, path="/iob/instances/{userDomain}/{userEmail}"
	//Accept: InstanceBoundary (with no instanceId)
	//Return: InstanceBoundary
	@RequestMapping(
		path = "/iob/instances/{userDomain}/{userEmail}",
		method = RequestMethod.POST,
		produces = MediaType.APPLICATION_JSON_VALUE,
		consumes = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary createInstance (
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@RequestBody InstanceBoundary instance){
		
		return service.createInstance(userDomain, userEmail, instance);

	}
	
	//PUT request, path="/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}"
	//Accept: InstanceBoundary
	//Return: None
	@RequestMapping(
		path = "/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}",
		method = RequestMethod.PUT,
		consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateInstance (
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("instanceDomain") String instanceDomain,
			@PathVariable("instanceId") String instanceId,
			@RequestBody InstanceBoundary instance){
		this.service.updateInstance(userDomain, userEmail, instanceDomain, instanceId, instance);

	}
	
	//GET request, path="/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}"
	//Accept: None
	//Return: InstanceBoundary
	@RequestMapping(
			path="/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
		public InstanceBoundary retrieveInstance (
				@PathVariable("userDomain") String userDomain,
				@PathVariable("userEmail") String userEmail,
				@PathVariable("instanceDomain") String instanceDomain,
				@PathVariable("instanceId") String instanceId){
		
			return this.service.getSpecificInstance(userDomain, userEmail, instanceDomain, instanceId);
		}
		
	//GET request, path="/iob/instances/{userDomain}/{userEmail}"
	//Accept: None
	//Return: Array of InstanceBoundary
	@CrossOrigin(origins = "http://localhost:3000")
	@RequestMapping(
			
			path="/iob/instances/{userDomain}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
		public InstanceBoundary[] getAllInstance (
				@PathVariable("userDomain") String userDomain,
				@PathVariable("userEmail") String userEmail,
		        @RequestParam(name = "size", required = false, defaultValue = "10") int size,
		        @RequestParam(name = "page", required = false, defaultValue = "0") int page) {

			return this.service.getAllInstances(userDomain, userEmail,size,page).toArray(new InstanceBoundary[0]);
		}
	
	//GET request, path="/iob/instances/{userDomain}/{userEmail}/search/byName/{name}"
	//Accept: None
	//Return: Array of InstanceBoundary
	@CrossOrigin(origins = "http://localhost:3000")
	@RequestMapping(
			path="/iob/instances/{userDomain}/{userEmail}/search/byName/{name}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
		public InstanceBoundary[] searchInstancesByName (
				@PathVariable("userDomain") String userDomain,
				@PathVariable("userEmail") String userEmail,
				@PathVariable("name") String name,
				@RequestParam(name = "size", required = false, defaultValue = "10") int size,
		        @RequestParam(name = "page", required = false, defaultValue = "0") int page){
		
			return this.service.getAllInstanceByName(userDomain, userEmail, name, size,page).toArray(new InstanceBoundary[0]);
		}

	//GET request, path="/iob/instances/{userDomain}/{userEmail}/search/byType/{type}"
	//Accept: None
	//Return: Array of InstanceBoundary
	@RequestMapping(
			path="/iob/instances/{userDomain}/{userEmail}/search/byType/{type}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
		public InstanceBoundary[] searchInstancesByType (
				@PathVariable("userDomain") String userDomain,
				@PathVariable("userEmail") String userEmail,
				@PathVariable("type") String type,
				@RequestParam(name = "size", required = false, defaultValue = "10") int size,
		        @RequestParam(name = "page", required = false, defaultValue = "0") int page){
		
			return this.service.getAllInstanceByType(userDomain, userEmail, type, size,page).toArray(new InstanceBoundary[0]);
		}
	
	//GET request, path="/iob/instances/{userDomain}/{userEmail}/search/near/{lat}/{lng}/{distance}"
	//Accept: None
	//Return: Array of InstanceBoundary
	@RequestMapping(
			path="/iob/instances/{userDomain}/{userEmail}/search/near/{lat}/{lng}/{distance}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
		public InstanceBoundary[] searchInstancesByLocation (
				@PathVariable("userDomain") String userDomain,
				@PathVariable("userEmail") String userEmail,
				@PathVariable("lat") double lat,
				@PathVariable("lng") double lng,
				@PathVariable("distance") double distance,
				@RequestParam(name = "size", required = false, defaultValue = "10") int size,
		        @RequestParam(name = "page", required = false, defaultValue = "0") int page){
		
			return this.service.getAllInstanceByLocation(userDomain, userEmail, lat,lng,distance, size,page).toArray(new InstanceBoundary[0]);
		}
	
	//GET request, path="/iob/instances/{userDomain}/{userEmail}/search/created/{creationWindow}"
	//Accept: None
	//Return: Array of InstanceBoundary
	@RequestMapping(
			path="/iob/instances/{userDomain}/{userEmail}/search/created/{creationWindow}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
		public InstanceBoundary[] searchInstancesByCreation (
				@PathVariable("userDomain") String userDomain,
				@PathVariable("userEmail") String userEmail,
				@PathVariable("creationWindow") String creationWindow,
				@RequestParam(name = "size", required = false, defaultValue = "10") int size,
		        @RequestParam(name = "page", required = false, defaultValue = "0") int page){
		
			return this.service.getAllInstanceByTimeCreation(userDomain, userEmail, creationWindow, size,page).toArray(new InstanceBoundary[0]);
		}
	//PUT request, path="/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/children"
	//Accept: InstanceIdBoundary
	//Return: None
	@RequestMapping(
			path="/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/children",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
		public void bindChildtoInstance (
				@PathVariable("userDomain") String userDomain,
				@PathVariable("userEmail") String userEmail,
				@PathVariable("instanceDomain") String instanceDomain,
				@PathVariable("instanceId") String instanceId,
				@RequestBody InstanceIdBoundary instanceIdBoundary) {
		
			this.service.bindChildToInstance(userDomain,userEmail,instanceDomain,instanceId,instanceIdBoundary);

		}
	
	//GET request, path="/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/children"
	//Accept: None
	//Return: Array of Instance Boundary
	@CrossOrigin(origins = "http://localhost:3000")
	@RequestMapping(
			path="/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/children",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
		public InstanceBoundary[] getAllInstanceChildren (
				@PathVariable("userDomain") String userDomain,
				@PathVariable("userEmail") String userEmail,
				@PathVariable("instanceDomain") String instanceDomain,
				@PathVariable("instanceId") String instanceId,
				@RequestParam(name = "size", required = false, defaultValue = "10") int size,
		        @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		
		return this.service.getAllInstanceChildren(userDomain,userEmail,instanceDomain,instanceId, size,page).toArray(new InstanceBoundary[0]);

		}
	
	//GET request, path="/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/parents"
	//Accept: None
	//Return: Array of Instance Boundary
	@RequestMapping(
			path="/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/parents",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
		public InstanceBoundary[] getAllInstanceParents (
				@PathVariable("userDomain") String userDomain,
				@PathVariable("userEmail") String userEmail,
				@PathVariable("instanceDomain") String instanceDomain,
				@PathVariable("instanceId") String instanceId,
				@RequestParam(name = "size", required = false, defaultValue = "10") int size,
		        @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		
			return this.service.getAllInstanceParents(userDomain,userEmail,instanceDomain,instanceId, size,page).toArray(new InstanceBoundary[0]);

		}
}
