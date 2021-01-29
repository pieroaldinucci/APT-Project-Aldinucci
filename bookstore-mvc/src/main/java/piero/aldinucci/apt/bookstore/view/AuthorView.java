package piero.aldinucci.apt.bookstore.view;

import java.util.List;

import piero.aldinucci.apt.bookstore.model.Author;

/**
 * View component of the MVC architecture.
 * It manages only Author class objects views.
 * 
 * @author Piero Aldinucci
 *
 */
public interface AuthorView {

	/**
	 * 
	 * @param authors Collection of available authors
	 */
	public void showAllAuthors(List<Author> authors);

	/**
	 * Add an author to the view
	 * @param author author to be added
	 */
	public void authorAdded(Author author);

	/**
	 * remove an author from the view
	 * @param author to be removed
	 */
	public void authorRemoved(Author author);

	/**
	 * 
	 * @param message message to be shown
	 * @param author object responsible for the error
	 */
	public void showError(String message, Author author);
}
