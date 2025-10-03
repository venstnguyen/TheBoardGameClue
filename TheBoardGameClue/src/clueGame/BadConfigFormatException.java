package clueGame;

public class BadConfigFormatException extends Exception {

	public BadConfigFormatException() {
		super("Error: Configuration file has bad format ");
	}

	public BadConfigFormatException(String msg) {
		super(msg);
	}
}
