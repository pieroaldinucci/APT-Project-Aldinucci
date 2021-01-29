package piero.aldinucci.apt.bookstore.exceptions;

/**
 * Thrown by the service layer when a problem occur during transaction.
 */
public class BookstorePersistenceException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public BookstorePersistenceException() {
		super();
	}

	/**
	 * @param message the detail message.
	 * @param cause the cause (which is saved for later retrieval).
	 */
	public BookstorePersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message the detail message.
	 */
	public BookstorePersistenceException(String message) {
		super(message);
	}

	/**
	 * @param cause the cause (which is saved for later retrieval)
	 */
	public BookstorePersistenceException(Throwable cause) {
		super(cause);
	}

}
