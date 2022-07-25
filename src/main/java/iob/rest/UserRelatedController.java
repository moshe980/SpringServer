package iob.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import iob.boundary.NewUserBoundary;
import iob.boundary.UserBoundary;
import iob.boundary.UserId;
import iob.logic.UsersService;

@RestController
public class UserRelatedController {
	
	private UsersService userService;
	
	@Autowired
	public UserRelatedController(UsersService userService) {
		this.userService = userService;
	}
	
	//POST request, path="/iob/users"
	//Accept: NewUserBoundary
	//Return: UserBoundary
	@RequestMapping(
		path = "/iob/users",
		method = RequestMethod.POST,
		produces = MediaType.APPLICATION_JSON_VALUE,
		consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createNewUser (
			@RequestBody NewUserBoundary newUser){
		
		UserBoundary userBoundary=new UserBoundary(new UserId(null, newUser.getEmail())
				,newUser.getRole().toUpperCase(),newUser.getUsername(),newUser.getAvatar());

		return userService.createUser(userBoundary);
	}
	
	//GET request, path="/iob/users/login/{userDomain}/{userEmail}"
	//Accept: None
	//Return: UserBoundary
	@RequestMapping(
			path="/iob/users/login/{userDomain}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
		public UserBoundary loginAndRetrieveUser(
				@PathVariable("userDomain") String userDomain,
				@PathVariable("userEmail") String userEmail){
		
			return userService.login(userDomain, userEmail);
		}

	
	//PUT request, path="/iob/users/{userDomain}/{userEmail}"
	//Accept: UserBoundary
	//Return: None
	@RequestMapping(
		path = "/iob/users/{userDomain}/{userEmail}",
		method = RequestMethod.PUT,
		consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUser (
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@RequestBody UserBoundary user){
		
		userService.updateUser(userDomain, userEmail, user);
	}
}
