package iob;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import iob.boundary.CreatedBy;
import iob.boundary.InstanceBoundary;
import iob.boundary.InstanceId;
import iob.boundary.InstanceIdBoundary;
import iob.boundary.Location;
import iob.boundary.NewUserBoundary;
import iob.boundary.UserBoundary;
import iob.boundary.UserId;
import iob.dal.InstanceDao;
import iob.dal.UserDao;
import iob.data.InstanceEntity;
import iob.data.UserConverter;
import iob.data.UserRole;
import iob.logic.InstancesService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class InstanceTestKit {
	private int port;
	// setup helper object to invoke HTTP requests
	private RestTemplate client;
	// setup a String used to represent the URL used to access the server
	private String url,deleteInstanceURL,userUrl;
	private String userDomain,managerEmail,playerEmail,adminEmail;
	private InstanceDao instanceDao;
	private String deleteUsersURL;

	// get random port used by server
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@Autowired
	public InstanceTestKit(InstanceDao instanceDao)
	{
		super();
		this.instanceDao = instanceDao;
	}
	
	@Value("${spring.application.name:2022a.demo}")
	public void setDomain(String domain)
	{
		this.userDomain=domain;
	}

	@PostConstruct
	public void initTestCase() {
		this.url = "http://localhost:" + this.port +"/iob/instances/{userDomain}/{userEmail}" ;
		this.deleteInstanceURL = "http://localhost:" + this.port + "/iob/admin/instances/{userDomain}/{userEmail}";
		this.deleteUsersURL = "http://localhost:" + this.port + "/iob/admin/users/{userDomain}/{userEmail}";
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
			.delete(this.deleteInstanceURL,userDomain,adminEmail);
		
		this.client
		.delete(this.deleteUsersURL,userDomain,adminEmail);

	}
	
	//GIVEN
		//the server is up
	//WHEN
		//I invoke the POST request through the URL:/iob/instances/{userDomain}/{managerEmail}
		//And passes InstanceBoundary (with no instance id) in the BODY of the request
	//THEN
		//the respond status is 200(OK) and responds with the instance data with a generated id for this instance
	@Test
	void testCreateInstanceWithCorrectData() {	
		
        Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put("origin", "USA");
        instanceAttributes.put("Language", "English");
        instanceAttributes.put("testFunc", "testCreateInstanceWithCorrectData");

		InstanceBoundary instanceBoundaryNoID=
				new InstanceBoundary(null
						, "Movie"
						, "Batman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		InstanceBoundary instanceBoundaryWithID=this.client.postForObject(this.url, instanceBoundaryNoID, InstanceBoundary.class,userDomain,managerEmail);
		
		
		assertThat(this.client
				.getForObject(this.url+"/{instanceDomain}/{instanceId}" , InstanceBoundary.class,userDomain,managerEmail,instanceBoundaryWithID.getInstanceId().getDomain(),instanceBoundaryWithID.getInstanceId().getId()))
				.isNotNull()
				.isEqualTo(instanceBoundaryWithID);
	}
	
	//GIVEN
		//the server is up
		//DB contain at least one instanceEntity
	//WHEN
		//I invoke the PUT request through the URL: /iob/instances/{userDomain}/{managerEmail}/{instanceDomain}/{instanceId}
		//and passes InstanceBoundary in the BODY of the request
	//THEN
		//the respond status is 200(OK) and The application updates the instance data
	@Test
	void testUpdateExistingInstance() {		
		
        Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put("origin", "USA");
        instanceAttributes.put("Language", "English");
        instanceAttributes.put("testFunc", "testUpdateExistingInstance");

		InstanceBoundary instanceBoundaryNoID=
				new InstanceBoundary(null
						, "Movie"
						, "Batman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		InstanceBoundary instanceBoundaryWithID=this.client.postForObject(this.url, instanceBoundaryNoID, InstanceBoundary.class,userDomain,managerEmail);
		instanceBoundaryWithID.setName("superman");
		
		this.client.put(this.url + "/{instanceDomain}/{instanceId}", instanceBoundaryWithID,userDomain,managerEmail
				,instanceBoundaryWithID.getInstanceId().getDomain(),instanceBoundaryWithID.getInstanceId().getId());
		
		assertThat(this.client
				.getForObject(
						this.url+"/{instanceDomain}/{instanceId}" ,
						InstanceBoundary.class,
						userDomain,managerEmail,
						instanceBoundaryWithID.getInstanceId().getDomain(),
						instanceBoundaryWithID.getInstanceId().getId())
				)
				.isNotNull()
				.extracting("name")
				.isEqualTo(instanceBoundaryWithID.getName());
		

	}
	
	
	
	//GIVEN
		//the server is up
		//DB contain at least one instanceEntity
	//WHEN
		//I invoke the PUT request through the URL: /iob/instances/{userDomain}/{managerEmail}/{instanceDomain}/{instanceId} (non existing instance id)
		//and passes InstanceBoundary in the BODY of the request
	//THEN
		//The application doesn't update the instance data
		//And returns error status 404 with an error message "Could not find instance with id: {instanceID}"
	@Test
	void testUpdateNonExistingInstance() {		
		
        Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put("origin", "USA");
        instanceAttributes.put("Language", "English");
        instanceAttributes.put("testFunc", "testUpdateNonExistingInstance");

		InstanceBoundary instanceBoundaryNoID=
				new InstanceBoundary(null
						, "Movie"
						, "Batman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		InstanceBoundary instanceBoundaryWithID=this.client.postForObject(this.url, instanceBoundaryNoID, InstanceBoundary.class,userDomain,managerEmail);
		instanceBoundaryWithID.setName("superman");
		String nonExistingId="-5";
		Exception exception=assertThrows(
				NotFound.class,
				()->{client.put(this.url + "/{instanceDomain}/{instanceId}", instanceBoundaryWithID,userDomain,managerEmail
						,instanceBoundaryWithID.getInstanceId().getDomain(),nonExistingId);
				}
				);
		
		assertTrue(exception.getMessage().contains("Could not find instance with id: "+nonExistingId));

	}

	//GIVEN
		//the server is up
		//DB contain at least one instanceEntity
	//WHEN
		//I invoke the GET request through the URL: /iob/instances/{userDomain}/{managerEmail}
	//THEN
		//the respond status is 200(OK) and we get array of instance boundaries
	@Test
	void testGetAllInstances() {
		
        Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put("origin", "USA");
        instanceAttributes.put("Language", "English");
        instanceAttributes.put("testFunc", "testGetAllInstances");

		InstanceBoundary instanceBoundaryNoID=
				new InstanceBoundary(null
						, "Movie"
						, "Batman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		InstanceBoundary instanceBoundaryNoID2=
				new InstanceBoundary(null
						, "Movie"
						, "superman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		this.client.postForObject(this.url, instanceBoundaryNoID, InstanceBoundary.class,userDomain,managerEmail);
		this.client.postForObject(this.url, instanceBoundaryNoID2, InstanceBoundary.class,userDomain,managerEmail);
		
		InstanceBoundary[] array = this.client.getForObject(this.url, InstanceBoundary[].class,userDomain,managerEmail);
		assertThat(array)
		.isNotNull()
		.hasSize(2);
		
	}
	
	//GIVEN
		//the server is up
		//DB contain at least one instanceEntity
	//WHEN
		//I invoke the GET request through the URL: /iob/instances/{userDomain}/{palyerEmail}
	//THEN
		//the respond status is 200(OK) and we get array of instance boundaries that actived
	@Test
	void testGetAllActivedInstances() {
		
	    Map<String, Object> instanceAttributes = new HashMap<>();
	    instanceAttributes.put("origin", "USA");
	    instanceAttributes.put("Language", "English");
        instanceAttributes.put("testFunc", "testGetAllActivedInstances");

		InstanceBoundary instanceBoundaryNoID=
				new InstanceBoundary(null
						, "Movie"
						, "Batman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		InstanceBoundary instanceBoundaryNoID2=
				new InstanceBoundary(null
						, "Movie"
						, "superman"
						, false
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		this.client.postForObject(this.url, instanceBoundaryNoID, InstanceBoundary.class,userDomain,managerEmail);
		this.client.postForObject(this.url, instanceBoundaryNoID2, InstanceBoundary.class,userDomain,managerEmail);
		
		InstanceBoundary[] array = this.client.getForObject(this.url, InstanceBoundary[].class,userDomain,playerEmail);
		assertThat(array)
		.isNotNull()
		.hasSize(1);
		
	}
	//GIVEN
		//the server is up
		//DB contain 10 instanceEntities
	//WHEN
		//I invoke the GET request through the URL: /iob/instances/{userDomain}/{managerEmail}?size=2&page=1
	//THEN
		//the respond status is 200(OK) and we get array of instance boundaries with size 2
	@Test
	void testGetAllInstancesPaginated() {
		
	    List<InstanceBoundary> instanceBoundaries=
	    		IntStream.range(0, 10)
	    		.mapToObj(i-> new InstanceBoundary(null, "type", "instance"+i, null, null, null, null, null))
	    		.map(instanceBoundary->this.client.postForObject(this.url, instanceBoundary, InstanceBoundary.class,userDomain,managerEmail))
	    		.collect(Collectors.toList());
	    
	    InstanceBoundary[] actualResuults=this.client.getForObject(this.url+"?size={size}&page={page}", InstanceBoundary[].class,userDomain,managerEmail,2,1);
	    		
		assertThat(actualResuults)
		.isNotNull()
		.hasSize(2)
		.usingRecursiveFieldByFieldElementComparator()
		.containsAnyElementsOf(instanceBoundaries);
		
	}

	//GIVEN
		//the server is up
	//WHEN
		//I invoke the GET request through the URL: /iob/instances/{userDomain}/{managerEmail}/{instanceDomain}/{instanceId}
		//Inserting user domain, user email, instance domain and instance id 
	//THEN
		//the respond status is 200(OK) and we get Instance boundary
	@Test
	void testGetSpecificInstance() {
		
        Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put("origin", "USA");
        instanceAttributes.put("Language", "English");
        instanceAttributes.put("testFunc", "testGetSpecificInstance");

  		InstanceBoundary instanceBoundaryNoID=
				new InstanceBoundary(null
						, "Movie"
						, "Batman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);

		InstanceBoundary instanceBoundaryWithID=this.client.postForObject(this.url, instanceBoundaryNoID, InstanceBoundary.class,userDomain,managerEmail);

		InstanceBoundary requestedInstance = this.client.getForObject(this.url + "/{instanceDomain}/{instanceId}", InstanceBoundary.class,userDomain,managerEmail,
				instanceBoundaryWithID.getInstanceId().getDomain(),instanceBoundaryWithID.getInstanceId().getId());

		assertThat(this.client
				.getForObject(this.url+"/{instanceDomain}/{instanceId}" , InstanceBoundary.class,userDomain,managerEmail,instanceBoundaryWithID.getInstanceId().getDomain(),instanceBoundaryWithID.getInstanceId().getId()))
		.isNotNull()
		.extracting("name")
		.isEqualTo(requestedInstance.getName());
	}
	
	
	//GIVEN
		//the server is up
	//WHEN
		//I invoke the GET request through the URL: /iob/instances/{userDomain}/{managerEmail}/{instanceDomain}/{instanceId}
		//Inserting user domain, user email, instance domain and instance id 
	//THEN
		//the respond is faild with status 404 "Could not find instance with id: fakeID
	@Test
	void testFailedGetSpecificInstance () {

		try {
			
			this.client.getForObject(this.url + "/{instanceDomain}/{instanceId}", InstanceBoundary.class,userDomain,managerEmail,
					this.userDomain,"fakeID");
		}catch (Exception e) {
			assertTrue(e.getMessage().contains("Could not find instance with id: fakeID"));
		}

		
	}

	//GIVEN
		//the server is up
	//WHEN
		//I invoke the GET request through the URL: /iob/instances/{userDomain}/{managerEmail}/{instanceDomain}/{instanceId}/children?size={size}&page={page}
		//Inserting user domain, user email, instance domain and instance id 
		//And passes InstanceIdBoundary of child in the BODY of the request
	//THEN
		//the respond status is 200(OK) and we get array of Instance boundary of children
	@Test
	void testGetAllInstanceChildren() {
		
		Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put("origin", "USA");
        instanceAttributes.put("Language", "English");
        instanceAttributes.put("testFunc", "testGetAllInstanceChildren");

		InstanceBoundary instanceBoundaryNoID=
				new InstanceBoundary(null
						, "Movie"
						, "Batman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		InstanceBoundary instanceBoundaryNoID2=
				new InstanceBoundary(null
						, "Movie"
						, "superman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		InstanceBoundary instanceBoundaryNoID3=
				new InstanceBoundary(null
						, "Movie"
						, "spiderman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		
		//create three instances
		InstanceBoundary parent=this.client.postForObject(this.url, instanceBoundaryNoID, InstanceBoundary.class,userDomain,managerEmail);
		InstanceBoundary child1=this.client.postForObject(this.url, instanceBoundaryNoID2, InstanceBoundary.class,userDomain,managerEmail);
		InstanceBoundary child2=this.client.postForObject(this.url, instanceBoundaryNoID3, InstanceBoundary.class,userDomain,managerEmail);

		//bind parent to children
		this.client.put(this.url + "/{instanceDomain}/{instanceId}/children",
				new InstanceIdBoundary(child1.getInstanceId().getDomain(), child1.getInstanceId().getId()), userDomain,managerEmail
				,parent.getInstanceId().getDomain(),parent.getInstanceId().getId());
		
		this.client.put(this.url + "/{instanceDomain}/{instanceId}/children",
				new InstanceIdBoundary(child2.getInstanceId().getDomain(), child2.getInstanceId().getId()), userDomain,managerEmail
				,parent.getInstanceId().getDomain(),parent.getInstanceId().getId());

		
		//get all the children of the parent
		InstanceBoundary[] array  = this.client.getForObject(this.url + "/{instanceDomain}/{instanceId}/children?size={size}&page={page}"
				,InstanceBoundary[].class, userDomain,managerEmail
				,parent.getInstanceId().getDomain(),parent.getInstanceId().getId(),1,0);
		
		assertThat(array).hasSize(1)
;
		
	}
	
	//GIVEN
		//the server is up
	//WHEN
		//I invoke the GET request through the URL: /iob/instances/{userDomain}/{managerEmail}/{instanceDomain}/{instanceId}/parents?size={size}&page={page}
		//Inserting user domain, user email, instance domain and instance id 
		//And passes InstanceIdBoundary of parent in the BODY of the request
	//THEN
		//the respond status is 200(OK) and we get array of Instance boundary of parent
	@Test
	void testGetAllInstanceParents() {
		
		Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put("origin", "USA");
        instanceAttributes.put("Language", "English");
        instanceAttributes.put("testFunc", "testGetAllInstanceParents");

		InstanceBoundary instanceBoundaryNoID=
				new InstanceBoundary(null
						, "Movie"
						, "Batman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		InstanceBoundary instanceBoundaryNoID2=
				new InstanceBoundary(null
						, "Movie"
						, "superman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		InstanceBoundary instanceBoundaryNoID3=
				new InstanceBoundary(null
						, "Movie"
						, "spiderman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		//create three instances
		InstanceBoundary parent1=this.client.postForObject(this.url, instanceBoundaryNoID, InstanceBoundary.class,userDomain,managerEmail);
		InstanceBoundary parent2=this.client.postForObject(this.url, instanceBoundaryNoID3, InstanceBoundary.class,userDomain,managerEmail);
		InstanceBoundary child=this.client.postForObject(this.url, instanceBoundaryNoID2, InstanceBoundary.class,userDomain,managerEmail);
        
		//bind parent to children
		this.client.put(this.url + "/{instanceDomain}/{instanceId}/children",
				new InstanceIdBoundary(child.getInstanceId().getDomain(), child.getInstanceId().getId()), userDomain,managerEmail
				,parent1.getInstanceId().getDomain(),parent1.getInstanceId().getId());
		
		this.client.put(this.url + "/{instanceDomain}/{instanceId}/children",
				new InstanceIdBoundary(child.getInstanceId().getDomain(), child.getInstanceId().getId()), userDomain,managerEmail
				,parent2.getInstanceId().getDomain(),parent2.getInstanceId().getId());

		
		//get all the parents of the child
		InstanceBoundary[] array  = this.client.getForObject(this.url + "/{instanceDomain}/{instanceId}/parents?size={size}&page={page}"
						,InstanceBoundary[].class, userDomain,managerEmail
						,child.getInstanceId().getDomain(),child.getInstanceId().getId(),1,0);
		
		assertThat(array).hasSize(1);		
	}
	
	
	//GIVEN
		//the server is up
	//WHEN
		//I invoke the PUT request through the URL: /iob/instances/{userDomain}/{managerEmail}/{instanceDomain}/{instanceId}/children
		//Inserting user domain, user email, instance domain and instance id 
		//And passes InstanceIdBoundary of child in the BODY of the request
	//THEN
		//the respond is faild with message "Instance cannot be a child of himself!" 
	@Test
	void testBindingInstanceWithItself() {
		        
		Map<String, Object> instanceAttributes = new HashMap<>();
        instanceAttributes.put("origin", "USA");
        instanceAttributes.put("Language", "English");
        instanceAttributes.put("testFunc", "testBindingInstanceWithItself");

		InstanceBoundary instanceBoundaryNoID=
				new InstanceBoundary(null
						, "Movie"
						, "Batman"
						, true
						, new Date()
						, new CreatedBy(new UserId(userDomain,managerEmail))
						, new Location(34.5, 34.5)
						, instanceAttributes);
		
		
		
		//create a instance
		InstanceBoundary instanceBoundary=this.client.postForObject(this.url, instanceBoundaryNoID, InstanceBoundary.class,userDomain,managerEmail);		
		
		//bind parent to children
		try {
			this.client.put(this.url + "/{instanceDomain}/{instanceId}/children",
					new InstanceIdBoundary(instanceBoundary.getInstanceId().getDomain(), instanceBoundary.getInstanceId().getId()), userDomain,managerEmail
					,instanceBoundary.getInstanceId().getDomain(),instanceBoundary.getInstanceId().getId()
					);
		
		}catch (Exception e) {
			assertTrue(e.getMessage().contains("Instance cannot be a child of himself!"));
		}

	}
	
	//GIVEN
		//the server is up
		//DB contain 10 instanceEntities
	//WHEN
		//I invoke the GET request through the URL: /iob/instances/{userDomain}/{managerEmail}/search/byName/{name}?size=10&page=0
	//THEN
		//the respond status is 200(OK) and we get array of instance boundaries with size 5 that match the name
	@Test
	void testSearchInstancesByName() {
		
	    List<InstanceBoundary> instanceBoundaries=
	    		IntStream.range(0, 10)
	    		.mapToObj(i-> new InstanceBoundary(null, "type", "instance"+i%2, null, null, null, null, null))
	    		.map(instanceBoundary->this.client.postForObject(this.url, instanceBoundary, InstanceBoundary.class,userDomain,managerEmail))
	    		.collect(Collectors.toList());
	    
	    InstanceBoundary[] actualResuults=this.client.getForObject(this.url+"/search/byName/{name}?size={size}&page={page}",
	    		InstanceBoundary[].class,userDomain,managerEmail,"instance0",10,0);
	    		
		assertThat(actualResuults)
		.isNotNull()
		.hasSize(5)
		.usingRecursiveFieldByFieldElementComparator()
		.containsAnyElementsOf(instanceBoundaries);

	}	
	
	//GIVEN
		//the server is up
		//DB contain 10 instanceEntities
	//WHEN
		//I invoke the GET request through the URL: /iob/instances/{userDomain}/{managerEmail}/search/byType/{type}?size=10&page=0
	//THEN
		//the respond status is 200(OK) and we get array of instance boundaries with size 5 that match the type
	@Test
	void testSearchInstancesByType() {
		
	    List<InstanceBoundary> instanceBoundaries=
	    		IntStream.range(0, 10)
	    		.mapToObj(i-> new InstanceBoundary(null, "type"+i%2, "name", null, null, null, null, null))
	    		.map(instanceBoundary->this.client.postForObject(this.url, instanceBoundary, InstanceBoundary.class,userDomain,managerEmail))
	    		.collect(Collectors.toList());
	    
	    InstanceBoundary[] actualResuults=this.client.getForObject(this.url+"/search/byType/{name}?size={size}&page={page}",
	    		InstanceBoundary[].class,userDomain,managerEmail,"type0",10,0);
	    		
		assertThat(actualResuults)
		.isNotNull()
		.hasSize(5)
		.usingRecursiveFieldByFieldElementComparator()
		.containsAnyElementsOf(instanceBoundaries);
	
	}	
	
	//GIVEN
		//the server is up
		//DB contain 10 instanceEntities
	//WHEN
		//I invoke the GET request through the URL: /iob/instances/{userDomain}/{managerEmail}/search/near/{lat}/{lng}/{distance}?size=10&page=0
	//THEN
		//the respond status is 200(OK) and we get array of instance boundaries with size 5 that near the location
	@Test
	void testSearchInstancesByLocation() {
		
		//Create 5 instanceBoundary near the location
	    List<InstanceBoundary> nearInstanceBoundaries=
		IntStream.range(0, 5)
		.mapToObj(i-> new InstanceBoundary(null, "type", "name", null, null, null, null, null))
		.map(instanceBoundary->this.client.postForObject(this.url, instanceBoundary, InstanceBoundary.class,userDomain,managerEmail))
		.collect(Collectors.toList());
		
		//Create 5 instanceBoundary don't near the location
		IntStream.range(0, 5)
		.mapToObj(i-> new InstanceBoundary(null, "type", "name", null, null, null, new Location(120,120), null))
		.forEach(instanceBoundary->this.client.postForObject(this.url, instanceBoundary, InstanceBoundary.class,userDomain,managerEmail));

	    InstanceBoundary[] actualResuults=this.client.getForObject(this.url+"/search/near/{lat}/{lng}/{distance}?size={size}&page={page}",
	    		InstanceBoundary[].class,userDomain,managerEmail,50,50,5,10,0);
	    		
		assertThat(actualResuults)
		.isNotNull()
		.hasSize(5)
		.usingRecursiveFieldByFieldElementComparator()
		.containsAnyElementsOf(nearInstanceBoundaries);

	
	}	
	
	
	//GIVEN
		//the server is up
		//DB contain 10 instanceEntities
	//WHEN
		//I invoke the GET request through the URL: /iob/instances/{userDomain}/{managerEmail}/search/created/{creationWindow}?size={size}&page={page}?size=10&page=0
	//THEN
		//the respond status is 200(OK) and we get array of instance boundaries with size 5 that in the last 24 hours
	@Test
	void testSearchInstancesByCreation() {
		
		//Create 5 instanceBoundary in the last 24 hours
	    List<InstanceBoundary> instanceBoundariesInLast24Hours=
		IntStream.range(0, 5)
		.mapToObj(i-> new InstanceBoundary(null, "type", "name", null, null, null, null, null))
		.map(instanceBoundary->this.client.postForObject(this.url, instanceBoundary, InstanceBoundary.class,userDomain,managerEmail))
		.collect(Collectors.toList());
		
		//Create 5 instanceBoundary not in the last 24 hours
	    Date lastWeek=new Date(System.currentTimeMillis()-1000L*3600*24*7);
	    
	    List<InstanceBoundary> instanceBoundariesInLastWeek=
		IntStream.range(0, 5)
		.mapToObj(i-> new InstanceBoundary(null, "type", "lastWeek", null, null, null, null, null))
		.map(instanceBoundary->this.client.postForObject(this.url, instanceBoundary, InstanceBoundary.class,userDomain,managerEmail))
		.collect(Collectors.toList());

		
	    
		Pageable pageable = PageRequest.of(0, 10, Direction.DESC, "createdTimestamp", "instanceId");

		//update the time stamp
	    List<InstanceEntity> resultPage=this.instanceDao.findAllByName("lastWeek",pageable).
	    		stream().peek(instanceEntity->instanceEntity.setCreatedTimestamp(lastWeek)).collect(Collectors.toList());
	    
	    
	    this.instanceDao.saveAll(resultPage);
	    
	    
	    InstanceBoundary[] actualResuults=this.client.getForObject(this.url+"/search/created/{creationWindow}?type={size}&page={page}",
	    		InstanceBoundary[].class,userDomain,managerEmail,"LAST_24_HOURS",10,0);

	    
		assertThat(actualResuults)
		.isNotNull()
		.hasSize(5)
		.usingRecursiveFieldByFieldElementComparator()
		.containsAnyElementsOf(instanceBoundariesInLast24Hours);

	
	}
	
	
}
