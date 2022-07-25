package iob;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.ws.Response;
import org.springframework.web.client.HttpClientErrorException;	
import org.springframework.web.client.HttpServerErrorException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;

import org.springframework.web.client.RestTemplate;

import iob.boundary.NewUserBoundary;
import iob.boundary.UserBoundary;
import iob.boundary.UserId;
import iob.data.UserRole;
import iob.logic.UserBadRequestException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserTestKit {

	private int port;
	// setup helper object to invoke HTTP requests
	private RestTemplate client;
	// setup a String used to represent the URL used to access the server
	private String url;
	private String adminURL,deleteUsersURL;
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
		this.url = "http://localhost:" + this.port +"/iob/users/" ;
		this.adminURL = "http://localhost:" + this.port + "/iob/admin/users/{userDomain}/{userEmail}";
		this.client = new RestTemplate();
		this.managerEmail = "testUserManager@test.com";
		this.playerEmail = "testUserPlayer@test.com";
		this.adminEmail = "testUserAdmin@test.com";
		this.deleteUsersURL = "http://localhost:" + this.port + "/iob/admin/users/{userDomain}/{userEmail}";

	}
	
	@BeforeEach 
	public void setUp() {
		//create Manager user
		this.client.postForObject(this.url, new NewUserBoundary(
				managerEmail,
				UserRole.MANAGER.name(),
				"managerName",
				"M"),
				NewUserBoundary.class);
		
		//create Player user
		this.client.postForObject(this.url, new NewUserBoundary(
				playerEmail,
				UserRole.PLAYER.name(),
				"playerName",
				"P"),
				NewUserBoundary.class);
		
		//create Admin user
		this.client.postForObject(this.url, new NewUserBoundary(
				adminEmail,
				UserRole.ADMIN.name(),
				"adminName",
				"A"),
				NewUserBoundary.class);

	}
	
	@AfterEach 
	public void tearDown() {
		
		this.client
		.delete(this.deleteUsersURL,userDomain,adminEmail);
	}
	
	//GIVEN
		//the server is up
	//WHEN
		//I invoke the POST request through the URL: /iob/users
		//Inserting new UserBoundary
	//THEN
		//the respond status is 200(OK) and we get user boundary
	@Test
	void testCreateUser() {
			
			NewUserBoundary newUserBoundary=new NewUserBoundary(playerEmail, UserRole.PLAYER.name(), "testUser", "J");
			
			UserBoundary userBoundary = this.client.postForObject(this.url, newUserBoundary, UserBoundary.class);
			
			
			assertThat(this.client
					.getForObject(this.url + "login/{userDomain}/{userEmail}" , UserBoundary.class,userBoundary.getUserId().getDomain(),playerEmail))
					.isNotNull()
					.isEqualTo(userBoundary);
		}
	
	@Test
	void testFailedCreateUser() {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
	    
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		NewUserBoundary newUserBoundary=new NewUserBoundary(null, UserRole.PLAYER.name(), "testUser", "J");
	    
	    HttpEntity<Object> entity = new HttpEntity<Object>(newUserBoundary,headers);
		
	
		Assertions.assertThrows( HttpClientErrorException.class, ()->{
			restTemplate.exchange(this.url, HttpMethod.POST ,entity ,NewUserBoundary.class);},"expected error code 400, user created instead"
				);
	    

		}

	@Test
	void testFailedToLogin() {
		boolean thrown = false;

		NewUserBoundary newUserBoundary=new NewUserBoundary(playerEmail, UserRole.PLAYER.name(), "testUser", "J");

		UserBoundary userBoundary = this.client.postForObject(this.url, newUserBoundary, UserBoundary.class);
		
		try {
		UserBoundary userBoundary2 = this.client
		.getForObject(this.url + "login/{userDomain}/{userEmail}" , UserBoundary.class,userBoundary.getUserId().getDomain(),"fakeEmail");
		
		}catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
			
			assertThat(httpClientOrServerExc.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
			thrown = true;
		}
		
		if (!thrown) {
			fail("no exception was thrown");
		}	

	}

	
	//GIVEN
		//the server is up
		//DB contain at least one instanceEntity
	//WHEN
		//I invoke the PUT request through the URL: /iob/users/{userDomain}/{userEmail}
		//And inserting UserBoundary to update in the BODY of the request
	//THEN
		//the respond status is 200(OK) and we get user boundary
	@Test
	void testUpdateUser() {
		
		NewUserBoundary newUserBoundary=new NewUserBoundary(playerEmail, UserRole.PLAYER.name(), "testUser", "J");
		
		UserBoundary userBoundary= this.client.postForObject(this.url, newUserBoundary, UserBoundary.class);

		//Update user:
		userBoundary.setUsername("updated user"); 
		userBoundary.setAvatar("updated avatar");
		
		this.client.put(this.url + "{userDomain}/{userEmail}", userBoundary, userBoundary.getUserId().getDomain(),playerEmail);
		
		
		assertThat(this.client
				.getForObject(this.url + "login/{userDomain}/{userEmail}" , UserBoundary.class,userBoundary.getUserId().getDomain(),playerEmail))
				.isNotNull()
				.isEqualTo(userBoundary);

	}
	
	@Test
	void testFailedUpdateUser() {
		boolean thrown = false;
		
		NewUserBoundary newUserBoundary=new NewUserBoundary(playerEmail, UserRole.PLAYER.name(), "testUser", "J");
		
		UserBoundary userBoundary= this.client.postForObject(this.url, newUserBoundary, UserBoundary.class);

		//Update user:
		userBoundary.setUsername("updated user"); 
		userBoundary.setAvatar("updated avatar");
		
		
		
		try {
			this.client.put(this.url + "{userDomain}/{userEmail}", userBoundary, userBoundary.getUserId().getDomain(),"fakeEmail");
			
			}catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
				
				assertThat(httpClientOrServerExc.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
				thrown = true;
			}
			
			if (!thrown) {
				fail("no exception was thrown");
			}	


	}
	
	
	
	
	
}
