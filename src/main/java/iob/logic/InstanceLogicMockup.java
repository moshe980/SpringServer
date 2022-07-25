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

import iob.boundary.CreatedBy;
import iob.boundary.InstanceBoundary;
import iob.boundary.InstanceId;
import iob.boundary.Location;
import iob.boundary.UserId;
import iob.data.InstanceConverter;
import iob.data.InstanceEntity;

//@Service
public class InstanceLogicMockup implements InstancesService{
	private Map<String, InstanceEntity> storage;
	private InstanceConverter converter;
	private String domain;
	private AtomicLong counter;

	@Autowired
	public InstanceLogicMockup(InstanceConverter converter)
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
		
		// initialize counter
		this.counter = new AtomicLong(1L);

	}
	
	
	@Override
	public InstanceBoundary createInstance(String userDomain, String userEmail, InstanceBoundary instance) {
		
		if (userDomain.contains(" ")&&userEmail.contains(" ") ) {
			throw new MissingAttributeException("Missing attribute");
		}
		
		String newId=String.valueOf(this.counter.getAndIncrement());
		
		instance.setCreatedBy(new CreatedBy(new UserId(userDomain, userEmail)));
		instance.setCreatedTimestamp(new Date());
		instance.setInstanceId(new InstanceId(this.domain, newId));
		
		if (instance.getType()==null) {
			throw new MissingAttributeException("Missing Type");
		}
		if (instance.getName()==null) {
			throw new MissingAttributeException("Missing Name");
		}
		if(instance.getLocation()==null) {
			instance.setLocation(new Location(55.5, 55.5));
		}
		if(instance.getInstanceAttributes()==null)
		{
			instance.setInstanceAttributes(new HashMap<>());
		}
		
		InstanceEntity entity =this.converter.convertToEntity(instance);
		this.storage.put(this.domain+"&"+newId, entity);
		return this.converter.convertToBoundary(entity);
	}

	@Override
	public InstanceBoundary updateInstance(String userDomain, String userEmail, String instanceDomain,
			String instanceId, InstanceBoundary update) {
				
		if (userDomain.contains(" ")&&userEmail.contains(" ")&&instanceDomain.contains(" ")&&instanceId.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}		
		InstanceBoundary instanceBoundary=getSpecificInstance(userDomain, userEmail, instanceDomain, instanceId);
		boolean dirtyFlag=false;
		
		if(update.getActive()!=null)
		{
			instanceBoundary.setActive(update.getActive());
			dirtyFlag=true;
		}
		
		if(update.getInstanceAttributes()!=null)
		{
			instanceBoundary.setInstanceAttributes(update.getInstanceAttributes());
			dirtyFlag=true;
		}
		
		if(update.getLocation()!=null)
		{
			instanceBoundary.setLocation(update.getLocation());
			dirtyFlag=true;
		}
		
		if(update.getName()!=null)
		{
			instanceBoundary.setName(update.getName());
			dirtyFlag=true;
		}
		
		if(update.getType()!=null)
		{
			instanceBoundary.setType(update.getType());
			dirtyFlag=true;
		}
		
		if(dirtyFlag)
		{
			this.storage.put(instanceDomain+"&"+instanceId,this.converter.convertToEntity(instanceBoundary));
		}
		
		return instanceBoundary;
	}

	@Override
	public List<InstanceBoundary> getAllInstances(String userDomain, String userEmail) {
		
		if (userDomain.contains(" ")&&userEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}		

        return this.storage.values()
                .stream().parallel()
                .map(this.converter::convertToBoundary)
                .collect(Collectors.toList());
        }

	@Override
	public InstanceBoundary getSpecificInstance(String userDomain, String userEmail, String instanceDomain,String instanceId) {
		
		if (userDomain.contains(" ")&&userEmail.contains(" ")&&instanceDomain.contains(" ")&&instanceId.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}		

		InstanceEntity entity=this.storage.get(instanceDomain+"&"+instanceId);
		
		if(entity!=null)
		{
			InstanceBoundary boundary=this.converter.convertToBoundary(entity);
			return boundary;
		}else {
			throw new InstanceNotFoundException("Could not find instance with id: " + instanceId);
		}
	}

	@Override
	public void deleteAllInstances(String adminDomain, String adminEmail) {
		
		if (adminDomain.contains(" ")&&adminEmail.contains(" ")) {
			throw new MissingAttributeException("Missing attribute");
		}		

		this.storage.clear();
		counter.getAndSet(1L);
		
	}

}
