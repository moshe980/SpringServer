package iob.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class MissingPermissionsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -691552574672034988L;

	public MissingPermissionsException() {
		super();
	}

	public MissingPermissionsException(String message) {
		super(message);
	}

	public MissingPermissionsException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingPermissionsException(Throwable cause) {
		super(cause);
	}

	public MissingPermissionsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
