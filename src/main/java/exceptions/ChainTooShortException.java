package exceptions;

public class ChainTooShortException extends Exception {
	public ChainTooShortException(String errorMessage) {
		super(errorMessage);
	}
}
