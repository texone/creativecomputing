package cc.creativecomputing.sound;

public class CCSoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2606588737303958717L;

	public CCSoundException() {
		super();
	}

	public CCSoundException(String theMessage, Throwable theCause, boolean theEnableSuppression, boolean theWritableStackTrace) {
		super(theMessage, theCause, theEnableSuppression, theWritableStackTrace);
	}

	public CCSoundException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

	public CCSoundException(String theMessage) {
		super(theMessage);
	}

	public CCSoundException(Throwable theCause) {
		super(theCause);
	}

}
