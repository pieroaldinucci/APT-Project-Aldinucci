package piero.aldinucci.apt.bookstore.transaction;

/**
 * 
 * @author Piero Aldinucci
 *
 */

public interface TransactionManager {
	/**
	 * 
	 * @param <R> an instance of the specified class
	 * @param code the argument code needed for the transaction
	 * @return the transaction result
	 */
	public <R> R doInTransaction(TransactionCode<R> code);
}
