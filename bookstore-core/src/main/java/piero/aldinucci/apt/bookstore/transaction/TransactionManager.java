package piero.aldinucci.apt.bookstore.transaction;

public interface TransactionManager {
	public <R> R doInTransaction(TransactionCode<R> code);
}
