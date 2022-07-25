package iob;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;

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
import iob.boundary.ActivityId;
import iob.boundary.CreatedBy;
import iob.boundary.Instance;
import iob.boundary.InstanceBoundary;
import iob.boundary.InstanceId;
import iob.boundary.Location;
import iob.boundary.NewUserBoundary;
import iob.boundary.UserBoundary;
import iob.boundary.UserId;
import iob.data.ActivityEntity;
import iob.data.UserRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ActiviteTestKit  {

	private int port;
	// setup helper object to invoke HTTP requests
	private RestTemplate client;
	// setup a String used to represent the URL used to access the server
	private String url;
	private String instanceURL;
	private String deleteActivitiesUrl,deleteUsersURL,userUrl,deleteInstancesUrl;
	private String adminURL;
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
		this.url = "http://localhost:" + this.port +"/iob/activities" ;
		this.adminURL = "http://localhost:" + this.port + "/iob/admin";
		this.instanceURL="http://localhost:" + this.port +"/iob/instances/{userDomain}/{userEmail}";
		this.deleteUsersURL = "http://localhost:" + this.port + "/iob/admin/users/{userDomain}/{userEmail}";
		this.deleteActivitiesUrl = "http://localhost:" + this.port + "/iob/admin/activities/{userDomain}/{userEmail}";
		this.deleteInstancesUrl = "http://localhost:" + this.port + "/iob/admin/instances/{userDomain}/{userEmail}";
		this.userUrl="http://localhost:" + this.port +"/iob/users";
		this.client = new RestTemplate();
		this.managerEmail = "testUserManager@test.com";
		this.playerEmail = "testUserPlayer@test.com";
		this.adminEmail = "testUserAdmin@test.com";
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
		.delete(this.deleteActivitiesUrl,userDomain,adminEmail);
	
		this.client
		.delete(this.deleteInstancesUrl,userDomain,adminEmail);

		this.client
		.delete(this.deleteUsersURL,userDomain,adminEmail);

	}
	
	//GIVEN
		//the server is up
		//DB contain at least one instanceEntity

	//WHEN
		//I invoke the POST operation using /iob/activities
		//Inserting activity boundary(with null activity id)
	//THEN
		//the respond status is 200(OK) and we get an object
	@Test
	void testInvokeActivity() {
		
		//create instance
        Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put("rating", "0");
        instanceAttributes.put("ratingAmount", "0");
        instanceAttributes.put("ratingSum", "0");

		InstanceBoundary instanceBoundaryNoID=
				new InstanceBoundary(null
						, "movie"
						, "Batman"
						, true
						, new Date()
						, null
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		InstanceBoundary instanceBoundaryWithID=this.client.postForObject(this.instanceURL, instanceBoundaryNoID, InstanceBoundary.class,userDomain,managerEmail);

        Map<String, Object> activityAttributes = new HashMap<>();
        activityAttributes.put("rating", "4");

		//Invoke Activity
		ActivityBoundary activityBoundary = new ActivityBoundary(
				null,
				"rateMovie",
				new Instance(instanceBoundaryWithID.getInstanceId()),
				new Date(),
				new CreatedBy(new UserId(userDomain, playerEmail)),
				activityAttributes);
		
		ActivityBoundary object = this.client.postForObject(this.url, activityBoundary, ActivityBoundary.class);
		
		ActivityBoundary[] array = this.client.getForObject(adminURL+"/activities/{userDomain}/{userEmail}", ActivityBoundary[].class, userDomain,adminEmail);
		
		assertThat(array[0]).isNotNull().isEqualTo(object);
	}
	

	


	
}
