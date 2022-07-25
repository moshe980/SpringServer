package iob.aop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import iob.boundary.ActivityBoundary;
import iob.boundary.InstanceBoundary;
import iob.data.InstanceEntity;
import iob.data.UserRole;
import iob.logic.InstancesServiceExtended;
import iob.logic.MissingPermissionsException;
import iob.logic.UsersService;
import iob.logic.UsersServiceExtended;

@Component
@Aspect
public class PermissionAspect {
	private UsersServiceExtended usersService;
	private InstancesServiceExtended instancesService;
	
	
	@Autowired
	public PermissionAspect(UsersServiceExtended usersService,InstancesServiceExtended instancesService) {
		this.usersService = usersService;
		this.instancesService=instancesService;
	}

	@Around("@annotation(iob.aop.Permissions)")
	public Object checkPermissions(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
	{
		
		Object[] args = proceedingJoinPoint.getArgs();

		Method method = ((MethodSignature)(proceedingJoinPoint.getSignature())).getMethod();
		Permissions annotation  = method.getAnnotation(Permissions.class);
		List<UserRole> permissions = Arrays.asList(annotation.userRoles()) ;
		
		UserRole currentUserRole;
		if(method.getName().equals("invokeActivity"))
		{
			ActivityBoundary activityBoundary=ActivityBoundary.class.cast(args[0]);
			
			currentUserRole=getUserRole(activityBoundary.getInvokedBy().getUserId().getDomain(),activityBoundary.getInvokedBy().getUserId().getEmail());
			if(!containsPermission(permissions, currentUserRole))
			{
				throw new MissingPermissionsException("Need permissions "+permissions.toString());

			}

			checkInstanceActive(activityBoundary);
		}else {
			currentUserRole=getUserRole(args[0].toString(), args[1].toString());
			
			if(!containsPermission(permissions, currentUserRole))
			{
				throw new MissingPermissionsException("Need permissions "+permissions.toString());

			}

		}
		
		// invoke original method
		try {
			Object rv = proceedingJoinPoint.proceed();
			return rv;
		} catch (Throwable e) {
			throw e;
		}


	}
	private boolean containsPermission(final List<UserRole> list, final UserRole name){
	    return list.stream().anyMatch(o -> o.equals(name));
	}
	
	private UserRole getUserRole(String userDomain,String userEmail)
	{
		return UserRole
				.valueOf(usersService.login(userDomain, userEmail).getRole());
	}
	
	private void checkInstanceActive(ActivityBoundary activityBoundary)
	{
		instancesService.getSpecificInstance(activityBoundary.getInvokedBy().getUserId().getDomain(), 
				activityBoundary.getInvokedBy().getUserId().getEmail(),
				activityBoundary.getInstance().getInstanceId().getDomain(), 
				activityBoundary.getInstance().getInstanceId().getId());
		
	}


}
