package cc.creativecomputing.gl4;

public class GLException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2703229756527290772L;

	public GLException() {
		super();
	}

	public GLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GLException(String message, Throwable cause) {
		super(message, cause);
	}

	public GLException(String message) {
		super(message);
	}

	public GLException(Throwable cause) {
		super(cause);
	}

}
