package iob.myMovies;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import iob.boundary.ActivityBoundary;
import iob.boundary.CreatedBy;
import iob.boundary.Instance;
import iob.boundary.InstanceBoundary;
import iob.boundary.InstanceIdBoundary;
import iob.boundary.UserBoundary;
import iob.data.UserRole;
import iob.logic.InstancesServiceExtended;
import iob.logic.UsersServiceExtended;

@Service("addToWatchList")
public class AddToWatchList implements DoesSomething{
	private InstancesServiceExtended instancseService;
	private UsersServiceExtended usersService;

	
	@Autowired
	public AddToWatchList(InstancesServiceExtended serviceExtended,UsersServiceExtended usersService) {
		this.instancseService= serviceExtended;
		this.usersService=usersService;
	}
	

	@Override
	public Object doSomthing(ActivityBoundary activityBoundary) {
	//	ObjectMapper jackson=new ObjectMapper();
	//	InstanceIdBoundary movie= jackson.convertValue(activityBoundary.getActivityAttributes().get("instanceIdBoundary"), InstanceIdBoundary.class);
		
		UserBoundary currentUser=usersService.login(activityBoundary.getInvokedBy().getUserId().getDomain(), 
				activityBoundary.getInvokedBy().getUserId().getEmail());
		
		Instance movieId=activityBoundary.getInstance();
		
		InstanceBoundary movie=instancseService.getSpecificInstance(
				currentUser.getUserId().getDomain(),
				currentUser.getUserId().getEmail(),
				movieId.getInstanceId().getDomain(),
				movieId.getInstanceId().getId());

		if (!(movie.getType().equals("movie")||movie.getType().equals("series"))) {
			throw new RuntimeException("Only movie/series can be added to watch list!");
		}

		currentUser.setRole(UserRole.MANAGER.name());
		usersService.updateUser(currentUser.getUserId().getDomain(), currentUser.getUserId().getEmail(), currentUser);

		String watchListId=currentUser.getAvatar();
		InstanceBoundary watchList=null;
		
		if(watchListId.isEmpty()) {
			 watchList=instancseService.createInstance(
					currentUser.getUserId().getDomain(), 
					currentUser.getUserId().getEmail(), 
					new InstanceBoundary(null, "watchList", "watchList", false, null, new CreatedBy(currentUser.getUserId()), null, null));
			 currentUser.setAvatar(watchList.getInstanceId().getDomain()+"&"+watchList.getInstanceId().getId());
		}else {
			String[] parts = watchListId.split("&");
			
			watchList=instancseService.getSpecificInstance(
					currentUser.getUserId().getDomain(), 
					currentUser.getUserId().getEmail(), 
					parts[0],
					parts[1]);
			
			Set<InstanceBoundary> moviesInWatchList = instancseService.getAllInstanceChildren(	
					currentUser.getUserId().getDomain(), 
					currentUser.getUserId().getEmail(), 
					watchList.getInstanceId().getDomain(), 
					watchList.getInstanceId().getId(),
					1000,0);
			
			if(moviesInWatchList.contains(movie)) {
				currentUser.setRole(UserRole.PLAYER.name());
				usersService.updateUser(currentUser.getUserId().getDomain(), currentUser.getUserId().getEmail(), currentUser);

				throw new RuntimeException("The movie/series is already in the watch list!");

			}
		}
		
		instancseService.bindChildToInstance(
				currentUser.getUserId().getDomain(),
				currentUser.getUserId().getEmail(),
				watchList.getInstanceId().getDomain(), 
				watchList.getInstanceId().getId(), 
				new InstanceIdBoundary(movieId.getInstanceId().getDomain(), movieId.getInstanceId().getId()));
		
		currentUser.setRole(UserRole.PLAYER.name());
		usersService.updateUser(currentUser.getUserId().getDomain(), currentUser.getUserId().getEmail(), currentUser);

				
		return activityBoundary.getActivityAttributes().put("status", "added to watchList");
	}

}
