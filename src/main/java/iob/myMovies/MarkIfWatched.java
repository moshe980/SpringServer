package iob.myMovies;

import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import iob.boundary.ActivityBoundary;
import iob.boundary.InstanceBoundary;
import iob.boundary.UserBoundary;
import iob.boundary.UserId;
import iob.data.UserRole;
import iob.logic.InstancesServiceExtended;
import iob.logic.UsersServiceExtended;

@Service("markIfWatched")
public class MarkIfWatched implements DoesSomething{
	private InstancesServiceExtended instancseService;
	private UsersServiceExtended usersService;

	
	@Autowired
	public MarkIfWatched(InstancesServiceExtended serviceExtended,UsersServiceExtended usersService) {
		this.instancseService= serviceExtended;
		this.usersService=usersService;
	}


	@Override
	public Object doSomthing(ActivityBoundary activityBoundary) {
		
		boolean myIsWatched=(boolean) activityBoundary.getActivityAttributes().get("isWatched");
		
		UserBoundary currentUser=usersService.login(activityBoundary.getInvokedBy().getUserId().getDomain(), 
				activityBoundary.getInvokedBy().getUserId().getEmail());

		InstanceBoundary movie=instancseService.getSpecificInstance(
				activityBoundary.getInvokedBy().getUserId().getDomain(), 
				activityBoundary.getInvokedBy().getUserId().getEmail(), 
				activityBoundary.getInstance().getInstanceId().getDomain(), 
				activityBoundary.getInstance().getInstanceId().getId());
		
		if (!(movie.getType().equals("movie")||movie.getType().equals("series")||movie.getType().equals("season")||movie.getType().equals("episode"))) {
			throw new RuntimeException("Only movie/series/season/episode can be marked as watched!");
		}

		ArrayList<UserId> usersWatched=  (ArrayList<UserId>) movie.getInstanceAttributes().get("usersWatched");
		
		if(usersWatched==null) {
			usersWatched=new ArrayList<UserId>();
		}

		if(myIsWatched&&usersWatched.indexOf(currentUser.getUserId())==-1)
		{
			usersWatched.add(currentUser.getUserId());

		}else {
			usersWatched.remove(currentUser.getUserId());

		}
		
		movie.getInstanceAttributes().put("usersWatched", usersWatched);
		

		currentUser.setRole(UserRole.MANAGER.name());
		
		usersService.updateUser(currentUser.getUserId().getDomain(), currentUser.getUserId().getEmail(), currentUser);

		
		instancseService.updateInstance(currentUser.getUserId().getDomain(),
				currentUser.getUserId().getEmail(),
				movie.getInstanceId().getDomain(), 
				movie.getInstanceId().getId(),
				movie);

		currentUser.setRole(UserRole.PLAYER.name());
		
		usersService.updateUser(currentUser.getUserId().getDomain(), currentUser.getUserId().getEmail(), currentUser);


		return activityBoundary;
	}


}
