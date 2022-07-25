package iob.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class MissingAttributeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -525820241548883013L;

	public MissingAttributeException() {
		super();
	}

	public MissingAttributeException(String message) {
		super(message);
	}

	public MissingAttributeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingAttributeException(Throwable cause) {
		super(cause);
	}

	public MissingAttributeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
