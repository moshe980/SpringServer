package iob.data;

import org.springframework.stereotype.Component;

import iob.boundary.CreatedBy;
import iob.boundary.InstanceBoundary;
import iob.boundary.InstanceId;
import iob.boundary.Location;
import iob.boundary.UserId;

@Component
public class InstanceConverter {
	
	public InstanceBoundary convertToBoundary(InstanceEntity entity)
	{
		InstanceBoundary boundary=new InstanceBoundary();
		
		String[] parts = entity.getInstanceId().split("&");

		boundary.setInstanceId(new InstanceId(parts[0], parts[1]));
		boundary.setType(entity.getType());
		boundary.setName(entity.getName());
		boundary.setActive(entity.getActive());
		boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
		
		parts = entity.getCreatedBy().split("&");
		boundary.setCreatedBy(new CreatedBy(new UserId(parts[0], parts[1])));
		
		boundary.setLocation(new Location(entity.getLocation().getLat(), entity.getLocation().getLng()));
		boundary.setInstanceAttributes(entity.getInstanceAttributes());
		
		return boundary;
	}

	public InstanceEntity convertToEntity(InstanceBoundary boundary)
	{
		InstanceEntity entity=new InstanceEntity();
		
		entity.setInstanceId(boundary.getInstanceId().getDomain()+"&"+boundary.getInstanceId().getId());		
		entity.setType(boundary.getType());
		entity.setName(boundary.getName());
		
		if(boundary.getActive()!=null) {
			entity.setActive(boundary.getActive());
		}else {
			entity.setActive(false);
		}		
		entity.setCreatedTimestamp(boundary.getCreatedTimestamp());
		entity.setCreatedBy(boundary.getCreatedBy().getUserId().getDomain()+"&"+boundary.getCreatedBy().getUserId().getEmail());
		entity.setLocation(new LocationEntity(boundary.getLocation().getLat(), boundary.getLocation().getLng()));		
		entity.setInstanceAttributes(boundary.getInstanceAttributes());
			
		return entity;
		
	}

}
