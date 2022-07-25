package iob;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import iob.boundary.ActivityBoundary;
import iob.boundary.CreatedBy;
import iob.boundary.Instance;
import iob.boundary.InstanceBoundary;
import iob.boundary.InstanceId;
import iob.boundary.Location;
import iob.boundary.NewUserBoundary;
import iob.boundary.UserBoundary;
import iob.boundary.UserId;
import iob.data.UserRole;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminTestKit {

	private int port;
	// setup helper object to invoke HTTP requests
	private RestTemplate client;
	// setup a String used to represent the URL used to access the server
	private String url;
	private String instanceURL;
	private String activityURL;
	private String userUrl;
	private String userDomain,managerEmail,playerEmail,adminEmail;

	// get random port used by server
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@Value("${spring.application.name:2022a.demo}")
	public void setDomain(String domain)
	{
		this.userDomain=domain;
	}

	@PostConstruct
	public void initTestCase() {
		this.url = "http://localhost:" + this.port + "/iob/admin" ;
		this.activityURL="http://localhost:" + this.port + "/iob/activities" ;
		this.instanceURL="http://localhost:" + this.port + "/iob/instances/{userDomain}/{userEmail}" ;
		this.client = new RestTemplate();
		this.managerEmail = "testUserManager@test.com";
		this.playerEmail = "testUserPlayer@test.com";
		this.adminEmail = "testUserAdmin@test.com";
		this.userUrl="http://localhost:" + this.port +"/iob/users";

	}
	
	@BeforeEach 
	public void setUp() {
		//create Manager user
		this.client.postForObject(this.userUrl, new NewUserBoundary(
				managerEmail,
				UserRole.MANAGER.name(),
				"managerName",
				"M"),
				NewUserBoundary.class);
		
		//create Player user
		this.client.postForObject(this.userUrl, new NewUserBoundary(
				playerEmail,
				UserRole.PLAYER.name(),
				"playerName",
				"P"),
				NewUserBoundary.class);
		
		//create Admin user
		this.client.postForObject(this.userUrl, new NewUserBoundary(
				adminEmail,
				UserRole.ADMIN.name(),
				"adminName",
				"A"),
				NewUserBoundary.class);

	}

	@AfterEach 
	public void tearDown() {

		this.client
		.delete(this.url + "/activities/{userDomain}/{userEmail}",userDomain,adminEmail);

		this.client
		.delete(this.url + "/instances/{userDomain}/{userEmail}",userDomain,adminEmail);
		
		this.client
		.delete(this.url + "/users/{userDomain}/{userEmail}",userDomain,adminEmail);

	}

	//GIVEN
		//the server is up
		//and the DB contain users;
	//WHEN
		//I invoke the GET request through the URL: /iob/admin/users/{userDomain}/{userEmail}
	//THEN
		//the respond status is 200(OK) and we get Array of User Boundaries.
	@Test
	void testExportAllUsers() {
		
		UserBoundary[] array = this.client.getForObject(this.url + "/users/{userDomain}/{userEmail}", UserBoundary[].class, userDomain, adminEmail);
		
		assertThat(array).hasSize(3);
		
	}
	
	
	//GIVEN
		//the server is up
		//and the DB contain activities;
	//WHEN
		//I invoke the GET request through the URL: /iob/admin/activities/{userDomain}/{userEmail}
	//THEN
		//the respond status is 200(OK) and we get Array of Activity Boundaries.
	@Test
	void testGetAllActivities() {
		
        Map<String, Object> instanceAttributes1 = new HashMap<>();
        instanceAttributes1.put("rating", "0");
        instanceAttributes1.put("ratingAmount", "0");
        instanceAttributes1.put("ratingSum", "0");
        
        InstanceBoundary instanceBoundaryNoID1=
				new InstanceBoundary(null
						, "movie"
						, "Batman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes1);
        
        InstanceBoundary instanceBoundaryNoID2=
				new InstanceBoundary(null
						, "movie"
						, "Superman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes1);
		
		
		
		//create a instance
		InstanceBoundary instanceBoundary1=this.client.postForObject(this.instanceURL, instanceBoundaryNoID1, InstanceBoundary.class,userDomain,managerEmail);	
		InstanceBoundary instanceBoundary2=this.client.postForObject(this.instanceURL, instanceBoundaryNoID2, InstanceBoundary.class,userDomain,managerEmail);	

        Map<String, Object> activityAttributes1 = new HashMap<>();
        activityAttributes1.put("rating", "5");

        
		ActivityBoundary activityBoundary = new ActivityBoundary(
				null,
				"rateMovie",
				new Instance(instanceBoundary1.getInstanceId()),
				new Date(),
				new CreatedBy(new UserId(userDomain, playerEmail)),
				activityAttributes1);

        Map<String, Object> activityAttributes2 = new HashMap<>();
        activityAttributes2.put("key1", "can be set to any value you wish2");

		ActivityBoundary activityBoundary2 = new ActivityBoundary(
				null,
				"rateMovie",
				new Instance(instanceBoundary2.getInstanceId()),
				new Date(),
				new CreatedBy(new UserId(userDomain, playerEmail)),
				activityAttributes1);
		
		this.client.postForObject(this.activityURL, activityBoundary, ActivityBoundary.class);
		this.client.postForObject(this.activityURL, activityBoundary2, ActivityBoundary.class);
		
		ActivityBoundary[] array = this.client.getForObject(url + "/activities/{userDomain}/{userEmail}", ActivityBoundary[].class, userDomain,adminEmail);
		
		assertThat(array).hasSize(2);
		
		
	}
	
	//GIVEN
		//the server is up
		//DB contain users
	//WHEN
		//I invoke the DELETE request through the URL: /iob/admin/users/{userDomain}/{userEmail}
	//THEN
		//the respond status is 200(OK) and The application remove all users in the DB
	@Test
	void testDeleteAllUsers() {
		
		NewUserBoundary newUserBoundary1=new NewUserBoundary(playerEmail, UserRole.PLAYER.name(), "testUser", "J");
		
		this.client.postForObject(this.userUrl, newUserBoundary1, UserBoundary.class);

		NewUserBoundary newUserBoundary2=new NewUserBoundary(adminEmail, UserRole.ADMIN.name(), "testUser2", "H");
		
		this.client.postForObject(this.userUrl, newUserBoundary2, UserBoundary.class);
		
		//Delete all
		this.client.delete(this.url + "/users/{userDomain}/{userEmail}", userDomain,adminEmail);
		
		try {
			UserBoundary[] array = this.client.getForObject(this.url + "/users/{userDomain}/{userEmail}", UserBoundary[].class, userDomain, adminEmail);

		} catch (Exception e) {
			assertTrue(e.getMessage().contains("Could not find user with email:"));
		}

		//create Admin user
		this.client.postForObject(this.userUrl, new NewUserBoundary(
				adminEmail,
				UserRole.ADMIN.name(),
				"adminName",
				"A"),
				NewUserBoundary.class);

	}
	
	//GIVEN
		//the server is up
		//DB contain users instances
	//WHEN
		//I invoke the DELETE request through the URL: /iob/admin/instances/{userDomain}/{userEmail}
	//THEN
		//the respond status is 200(OK) and The application remove all instances in the DB
	@Test
	void testdeleteAllInstances() {
		
        Map<String, Object> instanceAttributes1 = new HashMap<>();
        instanceAttributes1.put("origin", "USA");
        instanceAttributes1.put("Language", "English");
        instanceAttributes1.put("testFunc", "testdeleteAllInstances");

		InstanceBoundary instanceBoundaryNoID1=
				new InstanceBoundary(null
						, "Movie"
						, "Batman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,playerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes1);
		
		this.client.postForObject(this.instanceURL, instanceBoundaryNoID1, InstanceBoundary.class,userDomain,managerEmail);

        Map<String, Object> instanceAttributes2 = new HashMap<>();
        instanceAttributes2.put("origin", "Japan");
        instanceAttributes2.put("Language", "English");
        instanceAttributes2.put("testFunc", "testdeleteAllInstances");

		InstanceBoundary instanceBoundaryNoID2=
				new InstanceBoundary(null
						, "Series"
						, "Pokemon"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,playerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes2);
		
		this.client.postForObject(this.instanceURL, instanceBoundaryNoID2, InstanceBoundary.class,userDomain,managerEmail);

		//Delete all
		this.client.delete(this.url + "/instances/{userDomain}/{userEmail}",userDomain,adminEmail);
		
		InstanceBoundary[] array = this.client.getForObject(this.instanceURL, InstanceBoundary[].class,userDomain,managerEmail);
		
		assertThat(array)
		.isEmpty();
		
	}
	
	//GIVEN
		//the server is up
		//DB contain users activities
	//WHEN
		//I invoke the DELETE request through the URL: /iob/admin/activities/{userDomain}/{userEmail}
	//THEN
		//the respond status is 200(OK) and The application remove all activities in the DB.

	@Test
	void testDeleteAllActivities() {
		
        Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put("rating", "0");
        instanceAttributes.put("ratingAmount", "0");
        instanceAttributes.put("ratingSum", "0");

		
        InstanceBoundary instanceBoundaryNoID1=
				new InstanceBoundary(null
						, "movie"
						, "Batman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
        
        InstanceBoundary instanceBoundaryNoID2=
				new InstanceBoundary(null
						, "movie"
						, "Superman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		//create a instance
		InstanceBoundary instanceBoundary1=this.client.postForObject(this.instanceURL, instanceBoundaryNoID1, InstanceBoundary.class,userDomain,managerEmail);	
		InstanceBoundary instanceBoundary2=this.client.postForObject(this.instanceURL, instanceBoundaryNoID2, InstanceBoundary.class,userDomain,managerEmail);	

		
        Map<String, Object> activityAttributes1 = new HashMap<>();
        activityAttributes1.put("rating", "5");

		ActivityBoundary activityBoundary = new ActivityBoundary(
				null,
				"rateMovie",
				new Instance(instanceBoundary1.getInstanceId()),
				new Date(),
				new CreatedBy(new UserId(userDomain, playerEmail)),
				activityAttributes1);

		this.client.postForObject(this.activityURL , activityBoundary, ActivityBoundary.class);

		ActivityBoundary activityBoundary2 = new ActivityBoundary(
				null,
				"rateMovie",
				new Instance(instanceBoundary2.getInstanceId()),
				new Date(),
				new CreatedBy(new UserId(userDomain, playerEmail)),
				activityAttributes1);

		
		this.client.postForObject(this.activityURL, activityBoundary2, ActivityBoundary.class);
		
		//Delete all
		this.client.delete(this.url + "/activities/{userDomain}/{userEmail}", userDomain,adminEmail);
		
		ActivityBoundary[] array = this.client.getForObject(this.url + "/activities/{userDomain}/{userEmail}", ActivityBoundary[].class, userDomain,adminEmail);
		
		assertThat(array)
		.isEmpty();
		
	}
}
