package piero.aldinucci.apt.bookstore.transaction;

/**
 * Ensure that the code will be executed as a single transaction. 
 * 
 * @author Piero Aldinucci
 * 
 */

public interface TransactionManager {
	/**
	 * 
	 * @param <R> Type of return
	 * @param code the argument code needed to be executed in a transaction
	 * @return the transaction result
	 */
	public <R> R doInTransaction(TransactionCode<R> code);
}
