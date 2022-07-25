package iob.logic;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import iob.boundary.ActivityBoundary;
import iob.boundary.ActivityId;
import iob.data.ActivityConverter;
import iob.data.ActivityEntity;

//@Service
public class ActivityLogicMockup implements ActivitiesService {
	
	private Map<String, ActivityEntity> storage;
	private ActivityConverter converter;
	private String domain;
	private AtomicLong counter;
	
	@Autowired
	public ActivityLogicMockup(ActivityConverter converter)
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
		this.storage = Collections.synchronizedMap(new HashMap<>());
		
		// initialize counter
		this.counter = new AtomicLong(1L);		
	}
	
	@Override
	public Object invokeActivity(ActivityBoundary activity) {
		
		String newId=String.valueOf(this.counter.getAndIncrement());
		
		activity.setActivityId(new ActivityId(this.domain, newId));
		activity.setCreatedTimestamp(new Date());
		
		if(activity.getActivityAttributes()==null)
		{
			activity.setActivityAttributes(new HashMap<>());
		}
		if(activity.getInvokedBy()==null)
		{
			throw new MissingAttributeException("Missing InvokedBy");

		}
		if(activity.getInstance()==null)
		{
			throw new MissingAttributeException("Missing Instance");

		}
		ActivityEntity activityEntity=this.converter.convertToEntity(activity);

		this.storage.put(this.domain+"&"+newId, activityEntity);
		
		return activity;
		

	}

	@Override	
	public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail){
		
		if (adminDomain.contains(" ")&&adminEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}	
		
		return storage.values().stream().parallel().map(converter::convertToBoundary).collect(Collectors.toList());

	}

	@Override
	public void deleteAllActivities(String adminDomain, String adminEmail) {
		
		if (adminDomain.contains(" ")&&adminEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}	
		
		storage.clear();
		counter.getAndSet(1L);

	}

}
