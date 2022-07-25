package iob.data;

import org.springframework.stereotype.Component;

import iob.boundary.UserBoundary;
import iob.boundary.UserId;

@Component
public class UserConverter {
	
	public UserBoundary convertToBoundary(UserEntity entity)
	{
		UserBoundary userBoundary=new UserBoundary();
		
		String[] parts = entity.getUserId().split("&");

		userBoundary.setAvatar(entity.getAvatar());
		userBoundary.setUserId(new UserId(parts[0], parts[1]));
		userBoundary.setUsername(entity.getUsername());
		
		if (entity.getRole() != null) {
			userBoundary.setRole(entity.getRole().name());
		}else {
			userBoundary.setRole(UserRole.PLAYER.name());
		}
		
		return userBoundary;

	}
	public UserEntity convertToEntity(UserBoundary boundary)
	{
		UserEntity userEntity=new UserEntity();
		
		userEntity.setAvatar(boundary.getAvatar());
		userEntity.setUserId(boundary.getUserId().getDomain()+"&"+boundary.getUserId().getEmail());
		userEntity.setUsername(boundary.getUsername());
		
		if (boundary.getRole() != null) {
			userEntity.setRole(UserRole.valueOf(boundary.getRole()));
		}else {
			userEntity.setRole(UserRole.PLAYER);
		}
		
		return userEntity;

	}
}
