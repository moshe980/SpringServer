package iob.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import iob.aop.MyLogger;
import iob.aop.Permissions;
import iob.boundary.ActivityBoundary;
import iob.boundary.ActivityId;
import iob.dal.ActivityDao;
import iob.dal.IdGeneratorDao;
import iob.data.ActivityConverter;
import iob.data.ActivityEntity;
import iob.data.IdGeneratorEntity;
import iob.data.UserRole;
import iob.myMovies.DoesSomething;

@Service
public class ActivityLogicDb implements ActivitiesServiceExtended {
	
	private ActivityDao activityDao;
	private ActivityConverter converter;
	private String domain;
//	private AtomicLong counter;
	private IdGeneratorDao idGeneratorDao;
	private ApplicationContext ctx;

	
	@Autowired
	public ActivityLogicDb(ActivityDao activityDao, ActivityConverter converter,IdGeneratorDao idGeneratorDao,ApplicationContext ctx)
	{
		this.activityDao = activityDao;
		this.converter = converter;
		this.idGeneratorDao=idGeneratorDao;
		this.ctx = ctx;

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
	@Permissions(userRoles = UserRole.PLAYER)
	@Override
	public Object invokeActivity(ActivityBoundary activity) {
		
		String whatToDo;
		
		if(activity.getInvokedBy()==null)
		{
			throw new MissingAttributeException("Missing InvokedBy");

		}
		if(activity.getInstance()==null)
		{
			throw new MissingAttributeException("Missing Instance");

		}
		
		if(activity.getType()==null||activity.getType().isEmpty())
		{
			throw new MissingAttributeException("Missing Type");

		}else {
			whatToDo=activity.getType();
		}
		
		//String newId=String.valueOf(this.counter.getAndIncrement());
		IdGeneratorEntity idContainer=new IdGeneratorEntity();
		idContainer = this.idGeneratorDao.insert(idContainer);
		String newId=this.idGeneratorDao.findAll().get(0).getId();
		this.idGeneratorDao.deleteAll();;

		activity.setActivityId(new ActivityId(this.domain, newId));
		activity.setCreatedTimestamp(new Date());
		
		if(activity.getActivityAttributes()==null)
		{
			activity.setActivityAttributes(new HashMap<>());
		}

		ActivityEntity activityEntity=this.converter.convertToEntity(activity);
		
		DoesSomething myMoviesActivities=null;
		
		try {
			myMoviesActivities=ctx.getBean(whatToDo,DoesSomething.class);
		
		}catch (Exception e) {
			throw new RuntimeException("undefined bean that does: " + whatToDo);
		}
		
		myMoviesActivities.doSomthing(activity);

		activityEntity=this.activityDao.save(activityEntity);
		
		return activity;
		

	}
	
	@Transactional(readOnly = true)
	@MyLogger(logType="debug")
	@Permissions(userRoles = UserRole.ADMIN)
	@Override
	public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail){
		
		if (adminDomain.contains(" ")&&adminEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}	
		
		throw new RuntimeException("Deprecated function");

	}
	
	@Transactional(readOnly = true)
	@MyLogger(logType="debug")
	@Permissions(userRoles = UserRole.ADMIN)
	@Override
	public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail, int size, int page) {

		Pageable pageable = PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "activityId");

		return this.activityDao.findAll(pageable)
				.stream()
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toList());
	}

	@Transactional
	@MyLogger(logType="debug")
	@Permissions(userRoles = UserRole.ADMIN)
	@Override
	public void deleteAllActivities(String adminDomain, String adminEmail) {
				
		this.activityDao.deleteAll();
	//	counter.getAndSet(1L);

	}





}
