package iob.logic;

import java.util.List;

import iob.boundary.UserBoundary;

public interface UsersServiceExtended extends UsersService{
	
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail,int size,int page);

	
}
