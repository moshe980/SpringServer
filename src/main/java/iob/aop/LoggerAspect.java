package iob.aop;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.stream.IntStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


@Component
@Aspect
public class LoggerAspect {
	private Log logger = LogFactory.getLog(LoggerAspect.class);
	
	@Around("@annotation(iob.aop.MyLogger)")
	public Object logProxy (ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Object[] args = proceedingJoinPoint.getArgs();
		StringBuilder argsString = new StringBuilder();
		
		IntStream.range(0,args.length)
		.forEach(i->{
			if (i<args.length-1) {
				argsString.append(args[i]+", ");
				
			}else {
				argsString.append(args[i]);
			}

			
		});
		
		String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
		String methodName = proceedingJoinPoint.getSignature().getName();
		
		Method method = ((MethodSignature)(proceedingJoinPoint.getSignature())).getMethod();
		MyLogger annotation  = method.getAnnotation(MyLogger.class);
		String logType = annotation.logType();
		
		log(logType, "***** " + className + "." + methodName + "(" + argsString.toString() + ") - begin");
		
		// invoke original method
		try {
			Object rv = proceedingJoinPoint.proceed();
			log(logType, "***** " + className + "." + methodName  + "(" + argsString.toString() + ") - ended successfully");
			return rv;
		} catch (Throwable e) {
			log("error", "***** " + className + "." + methodName  + "(" + argsString.toString() + ") - ended with error");
			throw e;
		}
	}

	private void log(String logType, String message) {
		switch (logType.toLowerCase()) {
		case "info":
			this.logger.info(message);
			break;

		case "fatal":
			this.logger.fatal(message);	
			break;

		case "trace":
			this.logger.trace(message);
			break;

		case "error":
			this.logger.error(message);
			break;

		case "warn":
		case "warning":
			this.logger.warn(message);
			break;

		case "debug":
			this.logger.debug(message);
			break;
			
		default:
			break;
		}
	}
}