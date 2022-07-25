package iob.data;

import org.springframework.stereotype.Component;
import iob.boundary.ActivityBoundary;
import iob.boundary.ActivityId;
import iob.boundary.CreatedBy;
import iob.boundary.Instance;
import iob.boundary.InstanceId;
import iob.boundary.UserId;

@Component
public class ActivityConverter {
	
	public ActivityBoundary convertToBoundary(ActivityEntity entity)
	{
		ActivityBoundary activityBoundary=new ActivityBoundary();
		
		activityBoundary.setActivityAttributes(entity.getActivityAttributes());
		String[] parts = entity.getActivityId().split("&");
		activityBoundary.setActivityId(new ActivityId(parts[0], parts[1]));
		activityBoundary.setCreatedTimestamp(entity.getCreatedTimestamp());
		
		parts = entity.getInstance().split("&");
		activityBoundary.setInstance(new Instance(new InstanceId(parts[0], parts[1])));
		
		parts = entity.getInvokedBy().split("&");
		activityBoundary.setInvokedBy(new CreatedBy(new UserId(parts[0], parts[1])));
		activityBoundary.setType(entity.getType());
		
		return activityBoundary;
	}

	
	public ActivityEntity convertToEntity(ActivityBoundary boundary)
	{
		ActivityEntity activityEntity=new ActivityEntity();
		
		activityEntity.setActivityAttributes(boundary.getActivityAttributes());
		activityEntity.setActivityId(boundary.getActivityId().getDomain()+"&"+boundary.getActivityId().getId());
		activityEntity.setCreatedTimestamp(boundary.getCreatedTimestamp());
		activityEntity.setInstance(boundary.getInstance().getInstanceId().getDomain()+"&"+boundary.getInstance().getInstanceId().getId());
		activityEntity.setInvokedBy(boundary.getInvokedBy().getUserId().getDomain()+"&"+boundary.getInvokedBy().getUserId().getEmail());
		activityEntity.setType(boundary.getType());
		
		return activityEntity;

	}
	

}
