package iob.myMovies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import iob.boundary.ActivityBoundary;
import iob.boundary.InstanceBoundary;
import iob.boundary.UserBoundary;
import iob.data.UserRole;
import iob.logic.InstancesServiceExtended;
import iob.logic.UsersServiceExtended;

@Service("rateMovie")
public class RateMovie implements DoesSomething {
	private InstancesServiceExtended instancseService;
	private UsersServiceExtended usersService;

	
	@Autowired
	public RateMovie(InstancesServiceExtended serviceExtended,UsersServiceExtended usersService) {
		this.instancseService= serviceExtended;
		this.usersService=usersService;
	}


	@Override
	public Object doSomthing(ActivityBoundary activityBoundary) {
		int myRating = Integer.valueOf((String) activityBoundary.getActivityAttributes().get("rating")) ;
		
		if(myRating<0||myRating>10)
		{
			throw new RuntimeException("Invalid rating value(0-10)!");
		}
		
		InstanceBoundary movie=instancseService.getSpecificInstance(
				activityBoundary.getInvokedBy().getUserId().getDomain(), 
				activityBoundary.getInvokedBy().getUserId().getEmail(), 
				activityBoundary.getInstance().getInstanceId().getDomain(), 
				activityBoundary.getInstance().getInstanceId().getId());
		
		if (!(movie.getType().equals("movie")||movie.getType().equals("series"))) {
			throw new RuntimeException("Only movie/series can be rate!");

		}
		int ratingAmount = Integer.valueOf((String) movie.getInstanceAttributes().get("ratingAmount")) ;
		int ratingsum = Integer.valueOf((String) movie.getInstanceAttributes().get("ratingSum")) ;

		ratingsum+=myRating;
		ratingAmount++;
		
		float rating=(float)ratingsum/(float)ratingAmount;
		
		movie.getInstanceAttributes().put("rating", String.format("%.1f", rating));
		movie.getInstanceAttributes().put("ratingAmount", String.valueOf(ratingAmount));
		movie.getInstanceAttributes().put("ratingSum", String.valueOf(ratingsum));

		UserBoundary currentUser=usersService.login(activityBoundary.getInvokedBy().getUserId().getDomain(), 
				activityBoundary.getInvokedBy().getUserId().getEmail());

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
