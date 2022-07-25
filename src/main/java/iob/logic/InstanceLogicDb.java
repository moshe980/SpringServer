package iob.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import iob.aop.MyLogger;
import iob.aop.Permissions;
import iob.boundary.CreatedBy;
import iob.boundary.InstanceBoundary;
import iob.boundary.InstanceId;
import iob.boundary.InstanceIdBoundary;
import iob.boundary.Location;
import iob.boundary.UserBoundary;
import iob.boundary.UserId;
import iob.dal.IdGeneratorDao;
import iob.dal.InstanceDao;
import iob.data.CreationWindow;
import iob.data.IdGeneratorEntity;
import iob.data.InstanceConverter;
import iob.data.InstanceEntity;
import iob.data.UserRole;

@Service
public class InstanceLogicDb implements InstancesServiceExtended {
	private static final long ONE_DAY=1000L*60*60*24;
	private static final long ONE_HOUR=1000L*60*60;

	private InstanceDao instanceDao; 
	private InstanceConverter converter;
	private String domain;
//	private AtomicLong counter;
	private UsersServiceExtended usersServiceExtended;
	private UserBoundary userBoundary;
	private IdGeneratorDao idGeneratorDao;

	@Autowired
	public InstanceLogicDb(InstanceDao instanceDao, InstanceConverter converter,UsersServiceExtended usersServiceExtended,IdGeneratorDao idGeneratorDao)
	{
		this.instanceDao=instanceDao;
		this.converter=converter;
		this.usersServiceExtended=usersServiceExtended;
		this.idGeneratorDao=idGeneratorDao;
	}
	
	@Value("${spring.application.name:2022a.demo}")
	public void setDomain(String domain)
	{
		this.domain=domain;
	}
	
	/*@PostConstruct
	public void init()
	{		
		// initialize counter
		this.counter = new AtomicLong(1L);

	}*/
	
	@Transactional
	@MyLogger(logType="debug")
	@Permissions(userRoles = {UserRole.MANAGER})
	@Override
	public InstanceBoundary createInstance(String userDomain, String userEmail, InstanceBoundary instance) {
		
		if (instance.getType()==null||instance.getType().isEmpty()) {
			throw new MissingAttributeException("Missing Type");
		}
		if (instance.getName()==null||instance.getName().isEmpty()) {
			throw new MissingAttributeException("Missing Name");
		}
		
		//String newId=String.valueOf(this.counter.getAndIncrement());

		IdGeneratorEntity idContainer=new IdGeneratorEntity();
		idContainer = this.idGeneratorDao.insert(idContainer);
		String newId=this.idGeneratorDao.findAll().get(0).getId();
		this.idGeneratorDao.deleteAll();;

		instance.setCreatedBy(new CreatedBy(new UserId(userDomain, userEmail)));
		instance.setCreatedTimestamp(new Date());
		instance.setInstanceId(new InstanceId(this.domain, newId));
		
		if(instance.getLocation()==null) {
			instance.setLocation(new Location(55.5, 55.5));
		}
		if(instance.getInstanceAttributes()==null)
		{
			instance.setInstanceAttributes(new HashMap<>());
		}
		
		InstanceEntity entity =this.converter.convertToEntity(instance);

		entity=this.instanceDao.save(entity);
		
		return this.converter.convertToBoundary(entity);

	}
	
	@Transactional
	@MyLogger(logType="debug")
	@Permissions(userRoles = UserRole.MANAGER)
	@Override
	public InstanceBoundary updateInstance(String userDomain, String userEmail, String instanceDomain,
			String instanceId, InstanceBoundary update) {
				
		if (instanceDomain.contains(" ")&&instanceId.contains(" ")) {
			throw new MissingAttributeException("Missing attributes");
		}		
		
		InstanceBoundary instanceBoundary=getSpecificInstance(userDomain, userEmail, instanceDomain, instanceId);
		
		if(update.getActive()!=null)
		{
			instanceBoundary.setActive(update.getActive());
		}
		
		if(update.getInstanceAttributes()!=null)
		{
			instanceBoundary.setInstanceAttributes(update.getInstanceAttributes());
		}
		
		if(update.getLocation()!=null)
		{
			instanceBoundary.setLocation(update.getLocation());
		}
		
		if(update.getName()!=null)
		{
			instanceBoundary.setName(update.getName());
		}
		
		if(update.getType()!=null)
		{
			instanceBoundary.setType(update.getType());
		}
		
		InstanceEntity entity =this.converter.convertToEntity(instanceBoundary);

		// update map => db update ONLY if the data was actually modified
		entity=this.instanceDao.save(entity);		
		
		return this.converter.convertToBoundary(entity);

	}

	@Transactional(readOnly = true)
	@MyLogger(logType="error")
	@Override
	@Deprecated
	public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail) {
		
		if (userDomain.contains(" ")&&userEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attributes");
		}		

		throw new RuntimeException("Deprecated function");
    }
	
	@Transactional(readOnly = true)
	@MyLogger()
	@Permissions(userRoles = {UserRole.MANAGER,UserRole.PLAYER})
	@Override
	public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail, int size, int page) {
		
		
		userBoundary=usersServiceExtended.login(userDomain, userEmail);

		Pageable pageable = PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "instanceId");

		if (userBoundary.getRole().equals(UserRole.MANAGER.name())) {
			return this.instanceDao.findAll(pageable)
					.stream()
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());

		}else {
			List<InstanceEntity> resultPage = this.instanceDao.findAllByActive(true,pageable);

			return resultPage
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toList());

		}
	}
	
	@Transactional(readOnly = true)
	@MyLogger()
	@Permissions(userRoles = {UserRole.MANAGER,UserRole.PLAYER})
	@Override
	public List<InstanceBoundary> getAllInstanceByName(String userDomain, String userEmail, String name, int size, int page) {

		userBoundary=usersServiceExtended.login(userDomain, userEmail);

		if (userBoundary.getRole().equals(UserRole.MANAGER.name())) {

			Pageable pageable = PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "instanceId");
	
			List<InstanceEntity> resultPage = this.instanceDao.findAllByName(name,pageable);
	
			return resultPage
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toList());
			
		}else {
			
			Pageable pageable = PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "instanceId");
			
			List<InstanceEntity> resultPage = this.instanceDao.findAllByNameAndActive(name,true,pageable);
	
			return resultPage
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toList());
			
		}
	}
	
	@Transactional(readOnly = true)
	@MyLogger()
	@Permissions(userRoles = {UserRole.MANAGER,UserRole.PLAYER})
	@Override
	public List<InstanceBoundary> getAllInstanceByType(String userDomain, String userEmail, String type, int size, int page) {
		
		userBoundary=usersServiceExtended.login(userDomain, userEmail);

		if (userBoundary.getRole().equals(UserRole.MANAGER.name())) {

			Pageable pageable = PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "instanceId");
	
			List<InstanceEntity> resultPage = this.instanceDao.findAllByType(type,pageable);
	
	
			return resultPage
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toList());
		}else{
			
			Pageable pageable = PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "instanceId");
			
			List<InstanceEntity> resultPage = this.instanceDao.findAllByTypeAndActive(type,true,pageable);
	
			return resultPage
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toList());
			
		}
	}
	
	@Transactional(readOnly = true)
	@MyLogger()
	@Permissions(userRoles = {UserRole.MANAGER,UserRole.PLAYER})
	@Override
	public List<InstanceBoundary> getAllInstanceByLocation(String userDomain, String userEmail, double lat, double lng,
			double distance, int size, int page) {
		
		userBoundary=usersServiceExtended.login(userDomain, userEmail);

		if (userBoundary.getRole().equals(UserRole.MANAGER.name())) {

			Pageable pageable = PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "instanceId");
	
			double minLat=(lat)-(2*distance);
			double maxLat=(lat)+(2*distance);
			double minLng=(lng)-(2*distance);
			double maxLng=(lng)+(2*distance);
			
			List<InstanceEntity> resultPage = this.instanceDao.findAllByLocation_LatBetweenAndLocation_LngBetween(
					minLat,
					maxLat,
					minLng,
					maxLng,
					pageable);
	
	
			return resultPage
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toList());
		}else {
			
			Pageable pageable = PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "instanceId");
			
			double minLat=(lat)-(2*distance);
			double maxLat=(lat)+(2*distance);
			double minLng=(lng)-(2*distance);
			double maxLng=(lng)+(2*distance);
			
			List<InstanceEntity> resultPage = this.instanceDao.findAllByLocation_LatBetweenAndLocation_LngBetweenAndActive(
					minLat,
					maxLat,
					minLng,
					maxLng,
					true,
					pageable);
	
	
			return resultPage
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toList());
			
		}

		
	}
	
	@Transactional(readOnly = true)
	@MyLogger()
	@Permissions(userRoles = {UserRole.MANAGER,UserRole.PLAYER})
	@Override
	public List<InstanceBoundary> getAllInstanceByTimeCreation(String userDomain, String userEmail, String creationWindow,
			int size, int page) {
		
		userBoundary=usersServiceExtended.login(userDomain, userEmail);

		if (userBoundary.getRole().equals(UserRole.MANAGER.name())) {

			long time = 0;
			long now=System.currentTimeMillis();
			
			try {
				if(CreationWindow.valueOf(creationWindow).equals(CreationWindow.LAST_HOUR))
				{
					time=now-ONE_HOUR;
	
				}else if(CreationWindow.valueOf(creationWindow).equals(CreationWindow.LAST_24_HOURS))
				{
					time=now-ONE_DAY;
	
				}else if(CreationWindow.valueOf(creationWindow).equals(CreationWindow.LAST_7_DAYS))
				{
					time=now-ONE_DAY*7;
	
				}else if(CreationWindow.valueOf(creationWindow).equals(CreationWindow.LAST_30_DAYS))
				{
					time=now-ONE_DAY*30;
	
				}
				
				Pageable pageable = PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "instanceId");
	
				List<InstanceEntity> resultPage = this.instanceDao.findAllByCreatedTimestampAfter(new Date(time),pageable);
	
	
				return resultPage
					.stream()
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());
	
	
			} catch (Exception e) {
				throw new MissingAttributeException("Invalid creationWindow:"+e.getMessage());
			}
		
		}else{
			
			long time = 0;
			long now=System.currentTimeMillis();
			
			try {
				if(CreationWindow.valueOf(creationWindow).equals(CreationWindow.LAST_HOUR))
				{
					time=now-ONE_HOUR;
	
				}else if(CreationWindow.valueOf(creationWindow).equals(CreationWindow.LAST_24_HOURS))
				{
					time=now-ONE_DAY;
	
				}else if(CreationWindow.valueOf(creationWindow).equals(CreationWindow.LAST_7_DAYS))
				{
					time=now-ONE_DAY*7;
	
				}else if(CreationWindow.valueOf(creationWindow).equals(CreationWindow.LAST_30_DAYS))
				{
					time=now-ONE_DAY*30;
	
				}
				
				Pageable pageable = PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "instanceId");
	
				List<InstanceEntity> resultPage = this.instanceDao.findAllByCreatedTimestampAfterAndActive(new Date(time),true,pageable);
	
	
				return resultPage
					.stream()
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());
	
	
			} catch (Exception e) {
				throw new MissingAttributeException("Invalid creationWindow:"+e.getMessage());
			}
			
		}

		
	}

	@Transactional(readOnly = true)
	@MyLogger()
	@Permissions(userRoles = {UserRole.MANAGER,UserRole.PLAYER})
	@Override
	public InstanceBoundary getSpecificInstance(String userDomain, String userEmail, String instanceDomain,String instanceId) {
		
		if (instanceDomain.contains(" ")&&instanceId.contains(" ")) {
			throw new MissingAttributeException("Missing attributes");
		}		
		
		InstanceBoundary instanceBoundary=	this.converter
				.convertToBoundary(this.instanceDao.findById(instanceDomain+"&"+instanceId)
					  .orElseThrow(()->new InstanceNotFoundException("Could not find instance with id: " + instanceId)));

		
		userBoundary=usersServiceExtended.login(userDomain, userEmail);

		if (userBoundary.getRole().equals(UserRole.MANAGER.name())) {
			
			return instanceBoundary;
			
		}else{
			
			if(instanceBoundary.getActive()) {
				return instanceBoundary;

			}else {
				throw new InstanceNotFoundException("Could not find instance with id: " + instanceId+" that active");
			}
		}

	}
	
	@Transactional
	@MyLogger(logType="debug")
	@Permissions(userRoles = UserRole.ADMIN)
	@Override
	public void deleteAllInstances(String adminDomain, String adminEmail) {		

		this.instanceDao.deleteAll();
		//counter.getAndSet(1L);

	}

	@Transactional
	@MyLogger(logType="debug")
	@Permissions(userRoles = UserRole.MANAGER)
	@Override
	public void bindChildToInstance(String userDomain, String userEmail, String instanceDomain, String instanceId,
			InstanceIdBoundary instanceIdBoundary) {
		if (instanceIdBoundary==null) {
			throw new MissingAttributeException("Missing child");
		}
		if (instanceDomain.equals(instanceIdBoundary.getDomain())&&instanceId.equals(instanceIdBoundary.getId())) {
			throw new RuntimeException("Instance cannot be a child of himself!");
		}			
		
		InstanceEntity parent=this.instanceDao.findById(instanceDomain+"&"+instanceId)
				.orElseThrow(()->new InstanceNotFoundException("Could not find instance with id: " + instanceId));
				
		InstanceEntity child=this.instanceDao.findById(instanceIdBoundary.getDomain()+"&"+instanceIdBoundary.getId())
				.orElseThrow(()->new InstanceNotFoundException("Could not find instance with id: " + instanceIdBoundary.getId()));

		parent.addChild(child);

		this.instanceDao.save(parent);
		this.instanceDao.save(child);

	}

	@Transactional
	@MyLogger(logType="debug")
	@Permissions(userRoles = UserRole.MANAGER)
	@Override
	public void removeBindChildToInstance(String userDomain, String userEmail, String instanceDomain, String instanceId,
			InstanceIdBoundary instanceIdBoundary) {
		if (instanceIdBoundary==null) {
			throw new MissingAttributeException("Missing child");
		}
		if (instanceDomain.equals(instanceIdBoundary.getDomain())&&instanceId.equals(instanceIdBoundary.getId())) {
			throw new RuntimeException("Instance cannot be a child of himself!");
		}			
		
		InstanceEntity parent=this.instanceDao.findById(instanceDomain+"&"+instanceId)
				.orElseThrow(()->new InstanceNotFoundException("Could not find instance with id: " + instanceId));
				
		InstanceEntity child=this.instanceDao.findById(instanceIdBoundary.getDomain()+"&"+instanceIdBoundary.getId())
				.orElseThrow(()->new InstanceNotFoundException("Could not find instance with id: " + instanceIdBoundary.getId()));

		parent.removeChild(child);

		this.instanceDao.save(parent);
		this.instanceDao.save(child);

	}
	@Transactional(readOnly = true)
	@MyLogger()
	@Permissions(userRoles = {UserRole.MANAGER,UserRole.PLAYER})
	@Override
	public Set<InstanceBoundary> getAllInstanceParents(String userDomain, String userEmail, String instanceDomain,
			String instanceId, int size, int page) {
		
		if (instanceDomain.contains(" ")&&instanceId.contains(" ")) {
			throw new MissingAttributeException("Missing attributes");
		}

		Pageable pageable = PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "instanceId");

		userBoundary=usersServiceExtended.login(userDomain, userEmail);

		if (userBoundary.getRole().equals(UserRole.MANAGER.name())) {
			
			List<InstanceEntity> resultPage = this.instanceDao.findAllByChildren_instanceId(instanceDomain+"&"+instanceId,pageable);


			return resultPage
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toSet());

		
			
		}else {
			
			List<InstanceEntity> resultPage = this.instanceDao.findAllByChildren_instanceIdAndActive(instanceDomain+"&"+instanceId,true,pageable);

			return resultPage
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toSet());

		}



	}

	@Transactional(readOnly = true)
	@MyLogger()
	@Permissions(userRoles = {UserRole.MANAGER,UserRole.PLAYER})
	@Override
	public Set<InstanceBoundary> getAllInstanceChildren(String userDomain, String userEmail, String instanceDomain,
			String instanceId, int size, int page) {	
		
		if (instanceDomain.contains(" ")&&instanceId.contains(" ")) {
			throw new MissingAttributeException("Missing attributes");
		}


		Pageable pageable = PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "instanceId");

		userBoundary=usersServiceExtended.login(userDomain, userEmail);

		if (userBoundary.getRole().equals(UserRole.MANAGER.name())) {
			
			List<InstanceEntity> resultPage = this.instanceDao.findAllByParents_instanceId(instanceDomain+"&"+instanceId,pageable);

			return resultPage
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toSet());

		}else{

			List<InstanceEntity> resultPage = this.instanceDao.findAllByParents_instanceIdAndActive(instanceDomain+"&"+instanceId,true,pageable);

			return resultPage
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toSet());

		}


	}


}
