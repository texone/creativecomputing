package cc.creativecomputing.control.code.memorycompile;

public class CCInMemoryExecutionException extends RuntimeException{

	public CCInMemoryExecutionException() {
		super();
	}

	public CCInMemoryExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CCInMemoryExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public CCInMemoryExecutionException(String message) {
		super(message);
	}

	public CCInMemoryExecutionException(Throwable cause) {
		super(cause);
	}

}
