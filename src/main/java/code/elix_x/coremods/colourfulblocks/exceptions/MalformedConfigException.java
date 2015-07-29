package code.elix_x.coremods.colourfulblocks.exceptions;

public class MalformedConfigException extends Exception {

	private Throwable cause;
	
	public MalformedConfigException(Throwable e) {
		cause = e;
	}
	
	public Throwable getCause() {
		return cause;
	}
	
}
