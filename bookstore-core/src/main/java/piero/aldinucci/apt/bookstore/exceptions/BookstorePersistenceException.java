package piero.aldinucci.apt.bookstore.exceptions;

public class BookstorePersistenceException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BookstorePersistenceException() {
		super();
	}

	public BookstorePersistenceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BookstorePersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public BookstorePersistenceException(String message) {
		super(message);
	}

	public BookstorePersistenceException(Throwable cause) {
		super(cause);
	}

}
