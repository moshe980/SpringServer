package iob.logic;

import java.util.List;

import iob.boundary.UserBoundary;

public interface UsersService {

	public UserBoundary createUser(UserBoundary user);
	
	public UserBoundary login(String userDomain, String userEmail);
	
	public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary update);
	
	@Deprecated
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail);
	
	public void deleteAllUsers(String adminDomain, String adminEmail);
}
