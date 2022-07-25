package iob.aop;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import iob.data.UserRole;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Permissions {

	public UserRole[] userRoles() default UserRole.PLAYER;

}
