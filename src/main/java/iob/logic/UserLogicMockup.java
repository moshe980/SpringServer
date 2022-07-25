package iob.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import iob.boundary.UserBoundary;
import iob.boundary.UserId;
import iob.data.ExceptionsConstants;
import iob.data.InstanceEntity;
import iob.data.UserConverter;
import iob.data.UserEntity;
import iob.data.UserRole;

//@Service
public class UserLogicMockup implements UsersService {
	private Map<String, UserEntity> storage;
	private UserConverter converter;
	private String domain;

	@Autowired
	public UserLogicMockup(UserConverter converter)
	{
		this.converter=converter;
	}
	
	@Value("${spring.application.name:2022a.demo}")
	public void setDomain(String domain)
	{
		this.domain=domain;
	}
	
	@PostConstruct
	public void init()
	{
		// initialize thread safe storage
		this.storage=Collections.synchronizedMap(new HashMap<>());
	}
	
	@Override
	public UserBoundary createUser(UserBoundary user) {
		
		if(user.getUserId().getEmail()==null)
		{
			throw new MissingAttributeException("Missing email");
		}

		UserEntity userEntity = this.converter.convertToEntity(user);
		
		userEntity.setUserId(this.domain+"&"+user.getUserId().getEmail());
		if(userEntity.getUsername()==null)
		{
			userEntity.setUsername(user.getUserId().getEmail().substring(0, user.getUserId().getEmail().indexOf("@")));
		}
		if(userEntity.getAvatar()==null)
		{
			userEntity.setAvatar("");
		}
		this.storage.put(this.domain+"&"+user.getUserId().getEmail(), userEntity);
		
		return this.converter.convertToBoundary(userEntity);

	}

	@Override
	public UserBoundary login(String userDomain, String userEmail) {
		
		if (userDomain.contains(" ")&&userEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}	
		
		UserEntity userEntity=this.storage.get(userDomain+"&"+userEmail);
		
		if(userEntity!=null) {
			return this.converter.convertToBoundary(userEntity);
		}else {
			throw new UserNotFoundException("Could not find user for Email: " + userEmail);
		}
		
	}

	@Override
	public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {
		
		if (userDomain.contains(" ")&&userEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}	
		
		UserBoundary userBoundary=login(userDomain, userEmail);
		boolean dirtyFlag=false;
		
		if(update.getRole()!=null)
		{
			userBoundary.setRole(update.getRole());
			dirtyFlag=true;
		}
		
		if(update.getAvatar()!=null)
		{
			userBoundary.setAvatar(update.getAvatar());
			dirtyFlag=true;
		}
		
		if(update.getUsername()!=null)
		{
			userBoundary.setUsername(update.getUsername());
			dirtyFlag=true;
		}
		
		if (dirtyFlag) {
			this.storage.put(userDomain+"&"+userEmail, this.converter.convertToEntity(userBoundary));
		}
		
		return userBoundary;
		
		
	}

	@Override
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
		
		if (adminDomain.contains(" ")&&adminEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}	

		return storage.values().stream().parallel().map(converter::convertToBoundary).collect(Collectors.toList());
	}

	@Override
	public void deleteAllUsers(String adminDomain, String adminEmail) {
		
		if (adminDomain.contains(" ")&&adminEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}	
		
		this.storage.clear();

	}
}
