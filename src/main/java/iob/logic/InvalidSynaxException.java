package iob.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidSynaxException extends RuntimeException {
	private static final long serialVersionUID = -3892274678923036459L;

	public InvalidSynaxException() {
		super();
	}

	public InvalidSynaxException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidSynaxException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidSynaxException(String message) {
		super(message);
	}

	public InvalidSynaxException(Throwable cause) {
		super(cause);
	}
}
