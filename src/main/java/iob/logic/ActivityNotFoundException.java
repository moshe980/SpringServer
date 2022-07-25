package iob.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ActivityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -4752917008480934631L;

	public ActivityNotFoundException() {
		super();
	}

	public ActivityNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ActivityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActivityNotFoundException(String message) {
		super(message);
	}

	public ActivityNotFoundException(Throwable cause) {
		super(cause);
	}

}
