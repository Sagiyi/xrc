package xrc.api.common;

public class RcException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8916567845157568207L;

	public RcException() {
		super();
	}

	public RcException(String message, Throwable cause) {
		super(message, cause);
	}

	public RcException(String message) {
		super(message);
	}

	public RcException(Throwable cause) {
		super(cause);		
	}	
}
