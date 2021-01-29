package piero.aldinucci.apt.bookstore.view;

import java.util.List;

import piero.aldinucci.apt.bookstore.model.Book;

/**
 * View component of the MVN architecture.
 * It only manages Book objects view.
 * 
 * @author Piero Aldinucci
 *
 */
public interface BookView {

	/**
	 * 
	 * @param books collection of available books
	 */
	public void showAllBooks(List<Book> books);

	/**
	 * Add a book to the view
	 * 
	 * @param book book to be added
	 */
	public void bookAdded(Book book);

	/**
	 * Remove a book from the view
	 * 
	 * @param book to be removed
	 */
	public void bookRemoved(Book book);

	/**
	 * Show an error into the view
	 * 
	 * @param message message to be shown
	 * @param book    responsible for the error
	 */
	public void showError(String message, Book book);
}
