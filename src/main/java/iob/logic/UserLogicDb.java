package iob.logic;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import iob.dal.UserDao;
import iob.aop.MyLogger;
import iob.aop.Permissions;
import iob.boundary.UserBoundary;
import iob.data.ExceptionsConstants;
import iob.data.UserConverter;
import iob.data.UserEntity;
import iob.data.UserRole;

@Service
public class UserLogicDb implements UsersServiceExtended {
	private String domain;
	private UserDao userDao;
	private UserConverter converter;

	@Autowired
	public UserLogicDb(UserDao userDao, UserConverter converter)
	{
		super();
		this.userDao = userDao;
		this.converter = converter;
	}
	
	@Value("${spring.application.name:2022a.demo}")
	public void setDomain(String domain)
	{
		this.domain=domain;
	}
	
	@PostConstruct
	public void init()
	{
		
	}

	@Transactional
	@MyLogger(logType="debug")
	@Override
	public UserBoundary createUser(UserBoundary user) {
		
		if(user.getUserId().getEmail()==null)
		{
			throw new MissingAttributeException("Missing email");
		}
		
        if (!isValidEmail(user.getUserId().getEmail())) {
            throw new MissingAttributeException("Invalid user Email!");
        }		
        
        if(UserRole.valueOf(user.getRole()) == null)
        {
            throw new MissingAttributeException("Invalid user role!");

        }
		if(user.getAvatar()==null)
		{
            throw new MissingAttributeException("Invalid avatar!");
		}
		
		if(user.getUsername()==null||user.getUsername().isEmpty())
		{
            throw new MissingAttributeException("Invalid username!");
		}
		
        UserEntity userEntity = this.converter.convertToEntity(user);
		
        Optional<UserEntity> existUser = this.userDao.findById(userEntity.getUserId());

        if (existUser.isPresent()) {
            throw new RuntimeException("User ID:" + userEntity.getUserId() + " already exists!");
        }

		userEntity.setUserId(this.domain+"&"+user.getUserId().getEmail());

		userEntity=this.userDao.save(userEntity);
		
		return this.converter.convertToBoundary(userEntity);
	}

	@Transactional(readOnly = true)
	@MyLogger(logType="debug")
	@Override
	public UserBoundary login(String userDomain, String userEmail) {
		
		if (userDomain.contains(" ")&&userEmail.contains(" ")&&!isValidEmail(userEmail)) {
			throw new MissingAttributeException("Missing attribute");
		}	
		
		return this.converter.convertToBoundary(
				this.userDao.findById(userDomain+"&"+ userEmail)
					  .orElseThrow(()->new UserNotFoundException("Could not find user with email: " + userEmail)));
		
		
	}
	
	@Transactional
	@MyLogger(logType="debug")
	@Override
	public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update) {

		if (userDomain.contains(" ")&&userEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}	

		// get original entity from map using domain and email
		UserEntity existingUser = this.userDao
							.findById(userDomain+"&"+ userEmail)
							.orElseThrow(()->new UserNotFoundException("Could not find user with email: " + userEmail));
				
		if(update.getRole()!=null&&UserRole.valueOf(update.getRole()) != null)
		{
			existingUser.setRole(UserRole.valueOf(update.getRole().toUpperCase()));
		}
		
		if(update.getAvatar()!=null)
		{
			existingUser.setAvatar(update.getAvatar());
		}
		
		if(update.getUsername()!=null)
		{
			existingUser.setUsername(update.getUsername());
		}

		// update map => db update ONLY if the data was actually modified
		existingUser = this.userDao.save(existingUser);
		

		// convert entity to boundary and return it
		return this.converter.convertToBoundary(existingUser);
		
		
	}

	@Transactional(readOnly = true)
	@MyLogger(logType="debug")
	@Permissions(userRoles = UserRole.ADMIN)
	@Deprecated
	@Override
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
		
		if (adminDomain.contains(" ")&&adminEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}	
		throw new RuntimeException("Deprecated function");

	}
	
	@Transactional(readOnly = true)
	@MyLogger(logType="debug")
	@Permissions(userRoles = UserRole.ADMIN)
	@Override
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail, int size, int page) {

		Pageable pageable = PageRequest.of(page, size, Direction.DESC, "username", "role");

		return this.userDao.findAll(pageable)
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toList());
	}

	@Transactional
	@MyLogger(logType="debug")
	@Permissions(userRoles = UserRole.ADMIN)
	@Override
	public void deleteAllUsers(String adminDomain, String adminEmail) {
		
		if (adminDomain.contains(" ")&&adminEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}	
		
		this.userDao.deleteAll();

	}
	
    private boolean isValidEmail(String userEmail) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return userEmail.matches(regex);
    }


}
